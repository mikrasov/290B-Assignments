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
import api.SharedState;
import api.Space;
import api.Task;

public class SpaceImp<R> extends UnicastRemoteObject implements Space<R>{

	/** Serial ID */
	private static final long serialVersionUID = -1147376615845722661L;

	private static long SOLUTION_UID = 0;
	private static long UID_POOL = SOLUTION_UID+1;	
	private static int COMPUTER_ID_POOL = 0;
	
	private static boolean FORCE_STATE = true;
	private static boolean SUGGEST_STATE = false;
	
	private BlockingQueue<R> solution = new SynchronousQueue<R>();
	private BlockingQueue<Task<R>> waitingTasks = new LinkedBlockingQueue<Task<R>>();
	private Map<Long, Task<R>> registeredTasks = new ConcurrentHashMap<Long, Task<R>>();
	private Map<Integer, Proxy> proxies = new ConcurrentHashMap<Integer, Proxy>();
	
	private SharedState state;
	
	public SpaceImp() throws RemoteException {
		this(0);
	}
	
	public SpaceImp(int numLocalThreads) throws RemoteException {
		super();
		if(numLocalThreads > 0)
			new ComputeNode<R>(this, numLocalThreads);
	}
	
	@Override
	public void setTask(Task<R> task) throws RemoteException, InterruptedException {
		state = task.getInitialState();
		for(Proxy p: proxies.values()){
			p.updateState(state, FORCE_STATE);
		}
		task.setUid(UID_POOL++);
		task.setTarget(SOLUTION_UID, 0);
		registeredTasks.put(task.getUID(), task);
		waitingTasks.add(task);
	}

	@Override
	public R getSolution() throws RemoteException, InterruptedException {
		return solution.take();
	}
	
	@Override
	public int register(Computer<R> computer) throws RemoteException {
		Proxy proxy = new Proxy(COMPUTER_ID_POOL++, computer);
		System.out.println("Registered "+proxy);
		proxy.updateState(state, true);
		return proxy.id;
	}
	
	@Override
	public void updateState(int originatorID, SharedState updatedState) throws RemoteException {
		Log.debug("<== "+updatedState+(updatedState !=null && updatedState.isBetterThan(state)?" New Better":" Local Better") );
		if(updatedState != null && updatedState.isBetterThan(state)){
			
			this.state = updatedState;
			for(Proxy p: proxies.values()){
				if(p.id != originatorID)
					p.updateState(updatedState,SUGGEST_STATE);
			}
		}
		
	}
	
	/* ------------ Private methods ------------ */
	private synchronized void proccessResult(Result<R> result){

		Task<R> origin = registeredTasks.get(result.getTaskCreatorId()); //registeredTasks.remove(result.getTaskCreatorId());
		
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
				t.setUid(UID_POOL++);
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
				registeredTasks.put(t.getUID(), t);
				waitingTasks.add(t);
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
		
		Proxy(int id, Computer<R> computer) throws RemoteException{
			this.id = id;
			this.computer = computer;
			this.isLocal = computer.isRunningOnSpace();
			this.numThreads = computer.getNumThreads();
			this.collector = new Collector();
			this.dispatcher = new Dispatcher();
			
			proxies.put(id, this );
			
			isRunning = true;
			collector.start();
			dispatcher.start();
		}
		
		synchronized void stopProxyWithError(){
			if(!isRunning) return;
			
			isRunning = false;
			System.out.println("Error accessing "+toString());
			proxies.remove(id);

			System.out.println("Requeing "+inProgressTasks.size()+ " tasks");
			
			for(Task<R> task : inProgressTasks.values())
				waitingTasks.add(task);
		}
		
		void updateState(SharedState updatedState, boolean force) {
			try {
				computer.updateState(updatedState, force);
				if(!isLocal) Log.debug("==> "+updatedState+(force?" FORCED":""));
			} catch (RemoteException e) {
				System.err.println("Undable to send state "+updatedState+" to "+toString());
			}
		}
		
		@Override
		public String toString() {
			return (isLocal?"Local":"Remote")+" Computer - "+numThreads+" threads as ID: '"+id+"'";
		}
		
		class Dispatcher extends Thread {
			void enqueue(Task<R> task) throws RemoteException, InterruptedException{
				inProgressTasks.put(task.getUID(), task);
				computer.addTask(task);
				if(!isLocal) Log.debug("-"+id+"-> "+task);
			}
			
			@Override
			public void run() {	
				while(isRunning) try {
					Task<R> task = waitingTasks.take();
					
					if(task.isReady()) { 
						if(!isLocal){	//Remote Computer
							enqueue(task);
							continue;
						}
						else if(task.isShortRunning() || proxies.size() == 1){ //Local but task is short or no others
							enqueue(task);
							continue;
						}
					}
					
					//Throw back: don't schedule
					waitingTasks.put(task);		
				} 
				catch (InterruptedException e)	{} 
				catch (RemoteException e)		{stopProxyWithError(); return;}
			}
		}
		
		class Collector extends Thread {
			@Override
			public void run() {
				while(isRunning) try {
					Result<R> result = computer.collectResult();
					inProgressTasks.remove(result.getTaskCreatorId());
					if(!isLocal) Log.debug("<== "+id+"- "+result);
					proccessResult(result);
				}
				catch (InterruptedException e)	{} 
				catch (RemoteException e)		{stopProxyWithError(); return;}
			}
		}
	}

	/* ------------ Main Method ------------ */
	public static void main(String[] args) throws RemoteException {
		int numLocalThreads = (args.length > 0)? Integer.parseInt(args[0]) : 0;
		String logFile = (args.length > 1)? args[1] : "space";

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
