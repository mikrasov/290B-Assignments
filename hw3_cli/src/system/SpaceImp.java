package system;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import tasks.TaskSolution;
import api.Closure;
import api.Computer;
import api.Result;
import api.Space;
import client.Log;

public class SpaceImp<R> extends UnicastRemoteObject implements Space<R>{

	/** Serial ID */
	private static final long serialVersionUID = -1147376615845722661L;

	public static final int RETRY_TIMEOUT = 500;
	public static final int CYCLE_TIME = 50;
	
	private static long UID_POOL=0;
	
	private boolean isRunning = false;
	
	private HashMap<Long, Closure<R>> registeredTasks = new HashMap<Long, Closure<R>>();
	
	private BlockingQueue<Closure<R>> waitingTasks = new LinkedBlockingQueue<Closure<R>>();
	private BlockingQueue<Computer> availableComputers = new LinkedBlockingQueue<Computer>();
	
	private Closure<R> solution = new TaskSolution<R>();
	
	protected SpaceImp() throws RemoteException { 
		super(); 
		
		solution.setUid(UID_POOL++);
		registeredTasks.put(solution.getUID(), solution);
	}

	@Override
	public void assignTask(Closure<R> task) throws RemoteException {
		addTask(task);
		task.setTarget( solution.getUID(), 0 );
	}

	@Override
	public R collectResult() throws RemoteException {
		return solution.call().getValue();
	}

	@Override
	public boolean hasResult() throws RemoteException {
		return solution.isReady();
	}

	@Override
	public void register(Computer computer) throws RemoteException {
		Log.debug("Registering Computer: "+computer.getName());
		availableComputers.add(computer);
	}

	@Override
	public void startSpace() throws RemoteException {
		isRunning = true;

		while(isRunning) try {			
			if(!availableComputers.isEmpty()){
				Closure<R> task = waitingTasks.take();
					
				if(task.isReady())	// Input Full
					new Dispatcher(task).start();

				else // Put back on queue
					waitingTasks.add(task);
			}
			
			Thread.sleep(CYCLE_TIME);
		} catch (InterruptedException e) {}
	}
	
	private synchronized Closure<R> addTask(Closure<R> task){
		task.setUid(UID_POOL++);
		registeredTasks.put(task.getUID(), task);
		waitingTasks.add(task);
		return task;
	}
	
	private void assignValueToTarget(final Closure<R> origin, final R value){
		Closure<R> target = registeredTasks.get(origin.getTargetUid());
		int targetPort = origin.getTargetPort();
		target.setInput(targetPort, value);
	}
	
	private class Dispatcher extends Thread{
		
		private Closure<R> task;
		
		public Dispatcher(Closure<R> task) {
			this.task = task;
		}
		
		@Override
		public void run() {	
			Log.debugln("--> "+task);
			while(true)	try{
				Computer computer = availableComputers.take();
				Result<R> result = computer.execute(task);
				
				Log.debugln("<-- "+result);
				
				//If Single value pass it on to target
				if(result.isValue()){
					assignValueToTarget(task, result.getValue());
					registeredTasks.remove(task.getUID()); //Release task
				}
				//Else Add newly created tasks to waitlist 
				else{
					
					Closure<R>[] tasksToAdd = result.getTasks();
					
					//First add all new tasks and generate UIDs for them
					for(Closure<R> t: tasksToAdd){
						addTask(t);
					}
					
					/*
					 * Tasks can reference other tasks in the set via a negative UID.
					 * For example to set the target to another element in the set
					 * -1 would set to the 0th element
					 * -2 would set to the 1st element
					 * etc..
					 */
					for(Closure<R> t: tasksToAdd){
						
						long targetUid = t.getTargetUid();
						if(targetUid <0){
							Closure<R> realTarget = tasksToAdd[ Math.abs((int)targetUid)-1];
							t.setTarget(realTarget.getUID(), t.getTargetPort());
							
						}
					}
				}
				
				Log.debugln("@ Assigned "+result);
					
				//Release Computer
				availableComputers.put(computer);
				
				//Task Executed
				return;
			}
			catch(RemoteException e1){
				System.err.println("RMI Error when dispatching task to Computer, abandoning computer and trying again");
				System.err.println(e1);
				try {
					Thread.sleep(RETRY_TIMEOUT);
				}
				catch (InterruptedException e3) {}
			}
			catch (InterruptedException e2) {} 
		}
	}
	
	public static void main(String[] args) throws RemoteException {
		// Set Security Manager 
        System.setSecurityManager( new SecurityManager() );

        // Create Registry on JVM
        Registry registry = LocateRegistry.createRegistry( Space.PORT );

        // Create Space
        SpaceImp<Object> space = new SpaceImp<>();
        registry.rebind( Space.SERVICE_NAME, space );

        //Print Acknowledgement
        System.out.println("Space ready and registered as '"+Space.SERVICE_NAME+"' on port "+Space.PORT);

        //Start space
        space.startSpace();
	}

}
