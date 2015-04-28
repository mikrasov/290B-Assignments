package system;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import tasks.TaskSolution;
import util.Log;
import api.Closure;
import api.Computer;
import api.Result;
import api.Space;

public class SpaceImp<R> extends UnicastRemoteObject implements Space<R>{

	/** Serial ID */
	private static final long serialVersionUID = -1147376615845722661L;

	private static long UID_POOL=0;
	
	private boolean isRunning = false;
    private Log log;
	
	private HashMap<Long, Closure<R>> registeredTasks = new HashMap<Long, Closure<R>>();
	
	private BlockingQueue<Closure<R>> readyTasks = new LinkedBlockingQueue<Closure<R>>();
	private BlockingQueue<Closure<R>> waitingTasks = new LinkedBlockingQueue<Closure<R>>();
	private BlockingQueue<Dispatcher> runningComputers = new LinkedBlockingQueue<Dispatcher>();
	
	private Closure<R> solution = new TaskSolution<R>();
	
	public SpaceImp(Log log) throws RemoteException {
		super();
        this.log = log;
		
		solution.setUid(UID_POOL++);
		registeredTasks.put(solution.getUID(), solution);
	}

	@Override
	public void assignTask(Closure<R> task) throws RemoteException {
		addTask(task).setTarget( solution.getUID(), 0 );
	}

	@Override
	public R collectResult() throws RemoteException {
		Log.flush();
		if(!hasResult())
			return null;
		return solution.call().getValue();
	}

	@Override
	public boolean hasResult() throws RemoteException {
		return solution.isReady();
	}

	@Override
	public void register(Computer computer) throws RemoteException {
		System.out.println("Registering Computer: "+computer.getName());
		
		Dispatcher dispatcher = new Dispatcher(computer);
		dispatcher.start();
		runningComputers.add(dispatcher);
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
		
		private final Computer computer;
		
		public Dispatcher(Computer computer) {
			this.computer = computer;
		}
		
		@Override
		public void run() {	
			
			while(true)	{
				Closure<R> task;
				try {
					task = readyTasks.take();
				} 
				catch (InterruptedException e) { continue; }
				
				log.debugln("--> "+task);
				
				Result<R> result;
				try {
					result = computer.execute(task);
				} catch (RemoteException e) {
					System.err.println("Comuter "+computer+" suffered an RMI Failure, ceasing communication");
					System.err.println(e);
					System.err.println("Returning "+task+" to task queue");
					waitingTasks.add(task);
					return; //Terminates the dispatcher
				}
				
				log.debugln("<-- "+result);
				log.log( task +", "+result.getRunTime() );
				
				//If Single value pass it on to target
				if(result.isValue())
					assignValueToTarget(task, result.getValue());
					
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
				
				log.debugln("@ Assigned "+result);
					
				//Release task from UID index
				registeredTasks.remove(task.getUID()); 
			}
		}
	}
	
	@Override
	public void startSpace() throws RemoteException {
		isRunning = true;
		while(isRunning) for(Closure<R> task: waitingTasks){
			if(task.isReady()){
				readyTasks.add(task);
				waitingTasks.remove(task);
			}
		}
	}
	
	
	public static void main(String[] args) throws RemoteException {
		String logFile = (args.length > 0)? args[0] : "space";
		Log log = new Log(logFile);
		log.log("Task, Run Time (ms)");
		
		// Set Security Manager 
        System.setSecurityManager( new SecurityManager() );

        // Create Registry on JVM
        Registry registry = LocateRegistry.createRegistry( Space.PORT );

        // Create Space
        SpaceImp<Object> space = new SpaceImp<Object>(log);
        registry.rebind( Space.SERVICE_NAME, space );

        //Print Acknowledgement
        System.out.println("Space ready and registered as '"+Space.SERVICE_NAME+"' on port "+Space.PORT);
        
        //Start Scheduler
        space.startSpace();
        
        log.close();
	}

}
