package system;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import util.Log;
import api.Computer;
import api.Result;
import api.Space;
import api.Task;

public class SpaceImp<R> extends UnicastRemoteObject implements Space<R>{

	/** Serial ID */
	private static final long serialVersionUID = -1147376615845722661L;

	private static long SOLUTION_UID = 0;
	private long UID_POOL = SOLUTION_UID+1;
	
	private BlockingQueue<R> solution = new SynchronousQueue<R>();
	
	private Map<Long, Task<R>> waitingTasks = new ConcurrentHashMap<Long, Task<R>>();
	private BlockingQueue<Task<R>> readyTasks = new LinkedBlockingQueue<Task<R>>();
	
	private BlockingQueue<Proxy> proxies = new LinkedBlockingQueue<Proxy>();
	
	public SpaceImp() throws RemoteException {
		super();
	}
	
	public void start()  {
		while(true) for(Task<R> task: waitingTasks.values()){
			if(task.isReady()){
				readyTasks.add(task);
				waitingTasks.remove(task.getUID());
			}
		}
	}

	@Override
	public void setTask(Task<R> task) throws RemoteException, InterruptedException {
		addTask(task).setTarget(SOLUTION_UID, 0);
	}

	@Override
	public R getSolution() throws RemoteException, InterruptedException {
		return solution.take();
	}
	
	@Override
	public void register(Computer<R> computer) throws RemoteException {
		System.out.println("Registering Computer: "+computer.getName());
		Proxy proxy = new Proxy(computer);
		proxy.startProxy();
		proxies.add( proxy );
	}
	
	/* ------------ Private methods ------------ */
	private synchronized Task<R> addTask(Task<R> task){
		task.setUid(UID_POOL++);
		waitingTasks.put(task.getUID(), task);
		return task;
	}
	
	private void proccessResult(Result<R> result){
		
		//If Single value pass it on to target
		if(result.isValue()){
			
			if(result.getTargetId() == SOLUTION_UID){
				solution.add(result.getValue());
			}
			else {
				Task<R> target = waitingTasks.get(result.getTargetId());
				target.setInput(result.getTargetPort(), result.getValue());
			}
		}
	
		//Else Add newly created tasks to waitlist 
		else{
			
			Task<R>[] tasksToAdd = result.getTasks();
			
			//First add all new tasks and generate UIDs for them
			for(Task<R> t: tasksToAdd){
				addTask(t);
			}
			
			/*
			 * Tasks can reference other tasks in the set via a negative UID.
			 * For example to set the target to another element in the set
			 * -1 would set to the 0th element
			 * -2 would set to the 1st element
			 * etc..
			 */
			for(Task<R> t: tasksToAdd){
				
				long targetUid = t.getTargetUid();
				if(targetUid <0){
					Task<R> realTarget = tasksToAdd[ Math.abs((int)targetUid)-1];
					t.setTarget(realTarget.getUID(), t.getTargetPort());
					
				}
			}
		}
	}
	
	/* ------------ Proxies ------------ */
	private class Proxy {

		final Computer<R> computer;
		final Collector collector;
		final Dispatcher dispatcher;
		
		final List<Task<R>> assignedTasks = new LinkedList<Task<R>>(); 
		
		boolean isRunning = false;
		
		Proxy(Computer<R> computer){
			this.computer = computer;
			this.collector = new Collector();
			this.dispatcher = new Dispatcher();
		}
		
		void startProxy(){
			isRunning = true;
			collector.start();
			dispatcher.start();
		}
		
		void stopProxy(){
			isRunning = false;
			
			for(Task<R> task : assignedTasks)
				readyTasks.add(task);
		}
		
		class Collector extends Thread {
			@Override
			public void run() {
				while(isRunning) try {
					Result<R> result = computer.collectResult();
					Log.debug("<-- "+result);
					proccessResult(result);
				}
				catch (InterruptedException e)	{} 
				catch (RemoteException e)		{stopProxy();}
			}
		}
		
		class Dispatcher extends Thread {
			@Override
			public void run() {	
				while(isRunning) try {
					Task<R> task = readyTasks.take();
					
					computer.addTask(task);
					Log.debug("--> "+task);
				} 
				catch (InterruptedException e)	{} 
				catch (RemoteException e)		{stopProxy();}
			}
		}
	}

	/* ------------ Main Method ------------ */
	public static void main(String[] args) throws RemoteException {
		String logFile = (args.length > 0)? args[0] : "space";

		Log.startLog(logFile);
		
		// Set Security Manager 
        System.setSecurityManager( new SecurityManager() );

        // Create Registry on JVM
        Registry registry = LocateRegistry.createRegistry( Space.DEFAULT_PORT );

        // Create Space
        SpaceImp<Object> space = new SpaceImp<Object>();
        registry.rebind( Space.DEFAULT_NAME, space );

        //Print Acknowledgement
        System.out.println("Space ready and registered as '"+Space.DEFAULT_NAME+"' on port "+Space.DEFAULT_PORT);
        
        //Start Scheduler
        space.start();
        
        Log.close();
	}
}
