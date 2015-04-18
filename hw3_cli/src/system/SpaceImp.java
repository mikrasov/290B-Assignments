package system;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import tasks.TaskSolution;
import api.Closure;
import api.Computer;
import api.Result;
import api.Space;

public class SpaceImp<R> extends UnicastRemoteObject implements Space<R>{

	/** Serial ID */
	private static final long serialVersionUID = -1147376615845722661L;

	public static final int RETRY_TIMEOUT = 500;
	public static final int CYCLE_TIME = 1000;
	
	private boolean isRunning = false;
	
	private BlockingQueue<Closure<R>> waitingTasks = new LinkedBlockingQueue<Closure<R>>();
	private BlockingQueue<Computer> availableComputers = new LinkedBlockingQueue<Computer>();
	
	private Closure<R> solution = new TaskSolution<R>();
	
	protected SpaceImp() throws RemoteException { super(); }

	@Override
	public void assignTask(Closure<R> task) throws RemoteException {
		task.setTarget( solution, 0 );
		waitingTasks.add(task);
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
		System.out.println("Registering Computer: "+computer.getName());
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
			
			System.out.println("\n* --Current State--");
			for(Closure c: waitingTasks)
				System.out.println("* "+c);
			
			Thread.sleep(CYCLE_TIME);
		} catch (InterruptedException e) {}
	}
	
	private class Dispatcher extends Thread{
		
		private Closure<R> task;
		
		public Dispatcher(Closure<R> task) {
			this.task = task;
		}
		
		@Override
		public void run() {	
			System.out.println("--> "+task.toVerboseString());
			while(true)	try{
				Computer computer = availableComputers.take();
				Result<R> result = computer.execute(task);
				
				System.out.println("<-- "+result);
				
				//If Single value pass it on to target
				if(result.isValue())
					task.assignValueToTarget(result);
				
				//Else Add newly created tasks to waitlist 
				else for(Closure<R> t: result.getTasks() )
					waitingTasks.add(t);
					
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
