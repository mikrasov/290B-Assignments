package system;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
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
	private static long UID_POOL = SOLUTION_UID+1;	
	private static int COMPUTER_ID_POOL = 0;
	
	private BlockingQueue<R> solution = new SynchronousQueue<R>();
	private BlockingQueue<Task<R>> waitingTasks = new LinkedBlockingQueue<Task<R>>();
	private Map<Long, Task<R>> registeredTasks = new ConcurrentHashMap<Long, Task<R>>();
	private Map<Integer, Proxy> proxies = new ConcurrentHashMap<Integer, Proxy>();
	
	public SpaceImp() throws RemoteException {
		this(0);
	}
	
	public SpaceImp(int numLocalThreads) throws RemoteException {
		super();
		if(numLocalThreads > 0)
			register( new ComputeNode<R>(1, numLocalThreads, false), true);
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
	public int register(Computer<R> computer) throws RemoteException {
		return register(computer, false);
	}
	
	public int register(Computer<R> computer, boolean isLocal) throws RemoteException {
		Proxy proxy = new Proxy(COMPUTER_ID_POOL++, computer, isLocal);
		System.out.println("Registered "+proxy);
		
		return proxy.id;
	}
	
	/* ------------ Private methods ------------ */
	private synchronized Task<R> addTask(Task<R> task){
		task.setUid(UID_POOL++);
		registeredTasks.put(task.getUID(), task);
		waitingTasks.add(task);
		return task;
	}
	
	private void proccessResult(Result<R> result){
		Task<R> origin = registeredTasks.remove(result.getTaskCreatorId());
		//If Single value pass it on to target
		if(result.isValue()){
			
			if(origin.getTargetUid() == SOLUTION_UID){
				solution.add(result.getValue());
			}
			else {
				Task<R> target = registeredTasks.get(origin.getTargetUid());
				target.setInput(origin.getTargetPort(), result.getValue());
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
		final int id;
		final int numThreads;
		final boolean isLocal;
		final Collector collector;
		final Dispatcher dispatcher;
		
		Map<Long, Task<R>> inProgressTasks = new ConcurrentHashMap<Long, Task<R>>();
		
		boolean isRunning = false;
		
		Proxy(int id, Computer<R> computer, boolean isLocal) throws RemoteException{
			this.id = id;
			this.computer = computer;
			this.isLocal = isLocal;
			this.numThreads = computer.getNumThreads();
			this.collector = new Collector();
			this.dispatcher = new Dispatcher();
			
			proxies.put(id, this );
			
			isRunning = true;
			collector.start();
			dispatcher.start();
		}
		
		
		void stopProxyWithError(){
			System.out.println("Error accessing "+toString());

			isRunning = false;
			proxies.remove(id);
			while(!dispatcher.isStopped && !collector.isStopped) try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
			
			for(Task<R> task : inProgressTasks.values())
				waitingTasks.add(task);
		}
		
		@Override
		public String toString() {
			return (isLocal?"Local":"Remote")+" Computer - "+numThreads+" threads as ID: '"+id+"'";
		}
		
		class Dispatcher extends Thread {
			boolean isStopped = true;
			
			@Override
			public void run() {	
				isStopped = false;
				while(isRunning) try {
					Task<R> task = waitingTasks.take();
					
					if(!task.isReady() || (isLocal && !task.isShortRunning()) ) {
						//Throw it back
						waitingTasks.put(task);
					}
					else {
						//Enqueue
						inProgressTasks.put(task.getUID(), task);
						computer.addTask(task);
						Log.debug("--> "+task);
					}
				} 
				catch (InterruptedException e)	{} 
				catch (RemoteException e)		{stopProxyWithError();}
				isStopped = true;
			}
		}
		
		class Collector extends Thread {
			boolean isStopped = true;
			@Override
			public void run() {
				isStopped = false;
				while(isRunning) try {
					Result<R> result = computer.collectResult();
					inProgressTasks.remove(result.getTaskCreatorId());
					Log.debug("<-- "+result);
					proccessResult(result);
				}
				catch (InterruptedException e)	{} 
				catch (RemoteException e)		{stopProxyWithError();}
				isStopped = true;
			}
		}
	}

	/* ------------ Main Method ------------ */
	public static void main(String[] args) throws RemoteException {
		String logFile = (args.length > 0)? args[0] : "space";
		int numLocalThreads = (args.length > 1)? Integer.parseInt(args[1]) : 0;

		Log.startLog(logFile);
		
		// Set Security Manager 
        System.setSecurityManager( new SecurityManager() );

        // Create Registry on JVM
        Registry registry = LocateRegistry.createRegistry( Space.DEFAULT_PORT );

        //Print Acknowledgement
        System.out.println("Starting Space as '"+Space.DEFAULT_NAME+"' on port "+Space.DEFAULT_PORT);
        
        // Create Space
        SpaceImp<Object> space = new SpaceImp<Object>(numLocalThreads);
        registry.rebind( Space.DEFAULT_NAME, space );


        //Log.close();
	}
}
