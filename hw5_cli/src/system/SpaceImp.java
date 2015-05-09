package system;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

import tsp.TInf;
import util.Log;
import api.Capabilities;
import api.Computer;
import api.ProxyCallback;
import api.Result;
import api.SharedState;
import api.Space;
import api.Task;

public class SpaceImp<R> extends UnicastRemoteObject implements Space<R>{

	/** Serial ID */
	private static final long serialVersionUID = -1147376615845722661L;

	private static final int STATUS_OUTPUT_INTERVAL = 1000;
	
	private static final long SOLUTION_UID = 0;
	private static long UID_POOL = SOLUTION_UID+1;	
	
	private static final int LOCAL_PROXY_ID = 0;
	private static int PROXY_ID_POOL = LOCAL_PROXY_ID+1;

	private static final boolean FORCE_STATE = true;
	private static final boolean SUGGEST_STATE = false;
	private static final int BUFFER_SIZE_OF_LOCAL_COMPUTER = 1;
	
	
	private Scheduler<R> scheduler;
	private BlockingQueue<R> solution = new SynchronousQueue<R>();
	
	private Map<Long, Task<R>> registeredTasks = new ConcurrentHashMap<Long, Task<R>>();
	private Map<Integer, Proxy<R>> allProxies = new ConcurrentHashMap<Integer, Proxy<R>>();
	
	private SharedState state = new StateBlank();

    //Tinf
    private Map<Long, TInf> timeTasks = new HashMap<Long, TInf>();
    private long rootNodeId;
	
	public SpaceImp(int numLocalThreads) throws RemoteException {
		super();		
		scheduler = new Scheduler<R>();	
		scheduler.start();
		new StatusPrinter().start();
		int actualNumberOfThreadsToSet = numLocalThreads>0?numLocalThreads:1;
		ComputeNodeSpec spec = new ComputeNodeSpec(BUFFER_SIZE_OF_LOCAL_COMPUTER, actualNumberOfThreadsToSet);
		register(new ComputeNode<R>(spec), spec, LOCAL_PROXY_ID, scheduler.getShortTaskPool());
	}

	@Override
	public void setTask(Task<R> task) throws RemoteException, InterruptedException {
		setTask(task, new StateBlank());
	}
	
	@Override
	public void setTask(Task<R> task, SharedState initialState) throws RemoteException, InterruptedException {
		state = initialState;
		
		for(Proxy<R> p: allProxies.values()){
			p.updateState(state, FORCE_STATE);
		}
		task.setUid(UID_POOL++);
        //Tinf
        rootNodeId = task.getUID();
		task.setTarget(SOLUTION_UID, 0);
		registeredTasks.put(task.getUID(), task);
		scheduler.schedule(task);
	}

	@Override
	public R getSolution() throws RemoteException, InterruptedException {
		return solution.take();
	}

    @Override
    public double getTinf() {

        double total = 0;

        //Starting from the bottom. get the TInf object that corresponds to solutionUID node, aka the last leaf node
        TInf node = timeTasks.get(SOLUTION_UID);
        //add it's time to the total
        total += node.getRuntime();

        while(true) {
            //loop through parent list, retaining the parent with the greatest run time
            TInf parent = null;
            TInf bestParent = null;
            double greatestRunTime = Double.MIN_VALUE;
            for (int i = 0; i < node.getParentUids().size(); i++) {
                long parentUid = node.getParentUids().get(i);
                parent = timeTasks.get(parentUid);
                if (parent.getRuntime() > greatestRunTime) {
                    greatestRunTime = parent.getRuntime();
                    bestParent = parent;
                }
            }

            //At this point, we know we have found the parent with the largest run time. Add it to total
            total += greatestRunTime;
            //Set node equal to the parent we chose, and keep looping
            node = bestParent;

            //TODO: how to know when to stop and return total time?

        }

        return total;

    }
	
	@Override
	public int register(Computer<R> computer, Capabilities spec) throws RemoteException {
		return register(computer, spec, PROXY_ID_POOL++, scheduler.getLongTaskPool()).getId();
	}
	
	public Proxy<R> register(Computer<R> computer, Capabilities spec, int proxyID,  BlockingQueue<Task<R>> taskPool) throws RemoteException {
		computer.assignSpace(this, proxyID);
		
		Proxy<R> proxy = new Proxy<R>(computer, spec, proxyID, taskPool, proxyCallback);
		
		System.out.println("Registering "+proxy);
		proxy.updateState(state, FORCE_STATE);
		allProxies.put(proxyID, proxy );
		
		return proxy;
	}
	
	@Override
	public void updateState(int originatorID, SharedState updatedState) throws RemoteException {
		SharedState original = state;
		
		this.state = state.update(updatedState);
		
		Log.debug("<="+originatorID+"= "+updatedState+(updatedState !=null && (original != state)?" Updated":" Kept") );
		if( original != state){
			
			for(Proxy<R> p: allProxies.values()){
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
			
			allProxies.remove(proxyId);
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

                //Tinf: The parent is origin, so add to time tasks map
                ArrayList<Long> parentUids = new ArrayList<Long>();
                parentUids.add(result.getTaskCreatorId());
                TInf tinf = new TInf(result.getRunTime(), parentUids);
                //TODO: How to get the UID of the result task to add it to the map though?
                timeTasks.put(result.getUid?, tinf);

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

                //TODO: ??
			}
		}
	};
	
	class StatusPrinter extends Thread{
		
		String last = "";
		@Override
		public void run() {
			while(true){
				try { Thread.sleep(STATUS_OUTPUT_INTERVAL); } catch (InterruptedException e) {}
				
				String newOutput = "Progress: "+scheduler+" Computers:";
				
				for(Proxy<R> p: allProxies.values())
					newOutput+= " ["+p.getId()+":"+p.getNumDispatched()+"|"+p.getNumCollected()+"]";
				if(!last.equals(newOutput)){
					last = newOutput;
					Log.debug(newOutput);
				}
			}
		}
	}
	
	/* ------------ Main Method ------------ */
	public static void main(String[] args) throws RemoteException {
		int numLocalThreads = (args.length > 0)? Integer.parseInt(args[0]) : 0;
		String logFile = "space";

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
