package system;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

import util.Log;
import api.Computer;
import api.ProxyCallback;
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

	private static final boolean FORCE_STATE = true;
	private static final boolean SUGGEST_STATE = false;
	private static final boolean COMPUTER_IS_LOCAL = true;
	private static final boolean COMPUTER_IS_REMOTE = false;
	private static final int BUFFER_SIZE_OF_LOCAL_COMPUTER = 1;
	
	private Scheduler<R> scheduler;
	private BlockingQueue<R> solution = new SynchronousQueue<R>();
	
	private Map<Long, Task<R>> registeredTasks = new ConcurrentHashMap<Long, Task<R>>();
	private Map<Integer, Proxy<R>> proxies = new ConcurrentHashMap<Integer, Proxy<R>>();
	
	private SharedState state = new StateBlank();
	
	public SpaceImp(int numLocalThreads) throws RemoteException {
		super();		
		scheduler = new Scheduler<R>(proxies);
	
		int actualNumberOfThreadsToSet = numLocalThreads>0?numLocalThreads:1;
		register( new ComputeNode<R>(BUFFER_SIZE_OF_LOCAL_COMPUTER, actualNumberOfThreadsToSet), COMPUTER_IS_LOCAL);
	}
	
	@Override
	public void setTask(Task<R> task) throws RemoteException, InterruptedException {
		state = task.getInitialState();
		for(Proxy<R> p: proxies.values()){
			p.updateState(state, FORCE_STATE);
		}
		task.setUid(UID_POOL++);
		task.setTarget(SOLUTION_UID, 0);
		registeredTasks.put(task.getUID(), task);
		scheduler.schedule(task);
	}

	@Override
	public R getSolution() throws RemoteException, InterruptedException {
		return solution.take();
	}
	
	@Override
	public void register(Computer<R> computer) throws RemoteException {
		register(computer, COMPUTER_IS_REMOTE);
	}
	
	public void register(Computer<R> computer, boolean isLocal) throws RemoteException {
		int proxyID = COMPUTER_ID_POOL++;
		computer.assignSpace(this, proxyID);
		
		Proxy<R> proxy = new Proxy<R>(computer, proxyID, isLocal, proxyCallback);
		
		System.out.println("Registering "+proxy);
		proxy.updateState(state, FORCE_STATE);
		proxies.put(proxyID, proxy );
	}
	
	@Override
	public void updateState(int originatorID, SharedState updatedState) throws RemoteException {
		SharedState original = state;
		
		this.state = state.update(updatedState);
		
		Log.debug("<== "+updatedState+(updatedState !=null && (original != state)?" Updated":" Kept") );
		if( original != state){
			
			for(Proxy<R> p: proxies.values()){
				if(p.getId() != originatorID)
					p.updateState(updatedState, SUGGEST_STATE);
			}
		}
		
	}
	
	
	private ProxyCallback<R> proxyCallback = new ProxyCallback<R>() {

		@Override
		public synchronized void doOnError(int proxyId, Collection<Task<R>> leftoverTasks) {
			System.out.println("Requeing "+leftoverTasks.size()+ " tasks");
			
			for(Task<R> task : leftoverTasks)
				scheduler.schedule(task);
		}

		@Override
		public synchronized void processResult(Result<R> result) {

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
					scheduler.schedule(t);
				}
			}
		}
	};
	
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
