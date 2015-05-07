package system;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import util.Log;
import api.Computer;
import api.Result;
import api.SharedState;
import api.Space;
import api.Task;
import api.UpdateStateCallback;

public class ComputeNode<R> extends UnicastRemoteObject implements Computer<R> {

	private static final long serialVersionUID = -4962774042291137071L;

	private static final int BUFFER_DEFAULT_SIZE = 2;
	
	private int id;
	private transient BlockingQueue<Task<R>> tasks;
	private transient BlockingQueue<Result<R>> results;
	private transient List<ComputeThread> threads;
	
	private transient final boolean cacheEnabled;
	private transient ConcurrentHashMap<Task<R>, ResultValue<R>> cache;
	
	private transient Space<R> space;
	private transient SharedState state;
	
	public ComputeNode() throws RemoteException {
		this( -1, -1, false);
	}
	
	public ComputeNode(int desiredPrefetchBufferSize, int desiredNumThreads, boolean cacheEnabled) throws RemoteException {
		super();
		int prefetchBufferSize = desiredPrefetchBufferSize>0?desiredPrefetchBufferSize:BUFFER_DEFAULT_SIZE;
		int numThreads = desiredNumThreads>0?desiredNumThreads:Runtime.getRuntime().availableProcessors();

		this.cacheEnabled = cacheEnabled;
		results = new LinkedBlockingQueue<Result<R>>();
		threads = new LinkedList<ComputeThread>();
		tasks = new LinkedBlockingQueue<Task<R>>(prefetchBufferSize);
		cache = new ConcurrentHashMap<Task<R>, ResultValue<R>>();
		
		for(int i=0; i<numThreads; i++){
			ComputeThread thread = new ComputeThread(i);
			threads.add(thread);
			thread.start();
		}
	}
	
	@Override
	public int getNumThreads() throws RemoteException { return threads.size(); }
	
	@Override
	public void addTask(Task<R> task) throws RemoteException, InterruptedException {
		Log.debug("--> "+task);
		
		if(!fetchFromCache(task)) //Add task only if not in cache
			tasks.put(task);
	}

	@Override
	public Result<R> collectResult() throws RemoteException, InterruptedException {
		Result<R> result = results.take();
		Log.debug("<-- "+result);
		return result;
	}
	
	@Override
	public void updateState(SharedState updatedState, boolean force) throws RemoteException {
		Log.debug("--> "+(updatedState==null?"State NULL":updatedState)+(force?" FORCED":""+(updatedState !=null && updatedState.isBetterThan(state)?" New Better":" Local Better")));
		if( force || (updatedState !=null && updatedState.isBetterThan(state)))
			this.state = updatedState;
	}
	
	@Override
	public int getID() throws RemoteException { return id;}

	@Override
	public void assignSpace(Space<R> space, int spaceId) throws RemoteException {
		this.space = space;
		this.id = spaceId;
		
	}

	private void updateStateLocally(SharedState updatedState) {
		if(updatedState != null && updatedState.isBetterThan(state)){
			state = updatedState;
			try {
				Log.debug("<-- "+updatedState);
				space.updateState(id, state);
			} catch (RemoteException e) {
				System.err.println("Error sending new state to server");
			}
		}
	}
	
	private boolean fetchFromCache(Task<R> task) throws InterruptedException{
		if(cacheEnabled && cache.containsKey(task)){
			Result<R> cachedResult = new ResultValue<R>(cache.get(task),task.getUID());
			Log.debug("Retrieving "+cachedResult);
			Log.debug("-#- "+task+" = "+cachedResult);
			results.put(cachedResult);
			return true;
		}
		return false;
	}
	
	private class ComputeThread extends Thread {
		
		private final int id;
		public ComputeThread(int id){
			this.id = id;
		}
		
		@Override
		public void run() {
			while(true) try {
				Task<R> task = tasks.take();			
				Result<R> result = task.call(state, new UpdateStateCallback() {
					
					@Override
					public void updateState(SharedState resultingState){
						updateStateLocally(resultingState);
					}
				});
				
				Log.debug("-"+id+"- "+task+" = "+result);
				
					
				if(cacheEnabled && task.isCachable() && result.isValue()) {
					cache.put(task, (ResultValue<R>)result);
					Log.debug("Caching "+task);
				}
				
				results.put(result);
			}catch(InterruptedException e){	}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		String domain = (args.length > 0)? args[0]: "localhost";
		int desiredPrefetchBufferSize = (args.length > 1)? Integer.parseInt(args[1]): 1;
		int desiredNumThreads = (args.length > 2)? Integer.parseInt(args[2]): 1;
		boolean enableCaching = (args.length > 3)? Boolean.parseBoolean(args[3]): false;
		
		String url = "rmi://" + domain + ":" + Space.DEFAULT_PORT + "/" + Space.DEFAULT_NAME;
        
		try {
			System.out.println("Starting Computer on Space @ "+domain);

			Space<Object> space = (Space<Object>) Naming.lookup( url );
			Computer computer = new ComputeNode(desiredPrefetchBufferSize,desiredNumThreads,enableCaching);
			space.register(computer);
			
			System.out.println("Computer Registered as:\t"+computer.getID());
			System.out.println("Number Threads:\t\t"+computer.getNumThreads());
			
			if(desiredPrefetchBufferSize>1)
				System.out.println("Amerlioration Enabled with Buffer: "+desiredPrefetchBufferSize);
			else
				System.out.println("Amerlioration Disabled");
			
			System.out.println("Caching "+(enableCaching?"Enabled":"Disabled"));
			
		} catch (MalformedURLException | RemoteException | NotBoundException e)  {
            System.err.println("Error Connecting to Space at "+url);
            System.err.println(e);
        } 
	}

}