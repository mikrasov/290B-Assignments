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
import api.Space;
import api.Task;

public class ComputeNode<R> extends UnicastRemoteObject implements Computer<R> {

	private static final long serialVersionUID = -4962774042291137071L;

	private static final int BUFFER_DEFAULT_SIZE = 10;
	private static final int BUFFER_NO_PREFETCH = 1;
	private static final int SINGLE_PROCESSOR = 1;
	
	private transient BlockingQueue<Task<R>> tasks;
	private transient BlockingQueue<Result<R>> results;
	private transient List<ComputeThread> threads;
	
	private transient final boolean cacheEnabled;
	private transient ConcurrentHashMap<Task<R>, ResultValue<R>> cache = new ConcurrentHashMap<Task<R>, ResultValue<R>>();
	
	public ComputeNode() throws RemoteException {
		this(true, true, true);
	}
	
	public ComputeNode(boolean enableAmerlioration, boolean multiThread, boolean cacheEnabled) throws RemoteException {
		this(enableAmerlioration?BUFFER_DEFAULT_SIZE:BUFFER_NO_PREFETCH, 
			 multiThread?Runtime.getRuntime().availableProcessors():SINGLE_PROCESSOR,
			 cacheEnabled
			 );
	}
	
	public ComputeNode(int prefetchBufferSize, int numThreads, boolean cacheEnabled) throws RemoteException {
		super();
		results = new LinkedBlockingQueue<Result<R>>();
		threads = new LinkedList<ComputeThread>();
		tasks = new LinkedBlockingQueue<Task<R>>(prefetchBufferSize);
		
		this.cacheEnabled = false && cacheEnabled;
		
		for(int i=0; i<numThreads; i++){
			ComputeThread thread = new ComputeThread(i);
			threads.add(thread);
			thread.start();
		}
	}
	
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

	@Override
	public int getNumThreads() throws RemoteException { return threads.size(); }
	
	
	private class ComputeThread extends Thread {
		
		private final int id;
		public ComputeThread(int id){
			this.id = id;
		}
		
		@Override
		public void run() {
			while(true) try {
				Task<R> task = tasks.take();			
				Result<R> result = task.call();
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
		boolean enableAmerlioration = (args.length > 1)? Boolean.parseBoolean(args[1]): true;
		boolean multiThread = (args.length > 2)? Boolean.parseBoolean(args[2]): true;
		boolean enableCaching = (args.length > 3)? Boolean.parseBoolean(args[3]): true;
		
		String url = "rmi://" + domain + ":" + Space.DEFAULT_PORT + "/" + Space.DEFAULT_NAME;
        
		try {
			System.out.println("Starting Computer on Space @ "+domain);

			Space<Object> space = (Space<Object>) Naming.lookup( url );
			Computer computer = new ComputeNode(enableAmerlioration,multiThread,enableCaching);
			int id = space.register(computer);
			System.out.println("Computer Registered as:\t"+id);
			System.out.println("Number Threads:\t\t"+computer.getNumThreads());
			System.out.println("Amerlioration Enabled:\t"+enableAmerlioration);
			System.out.println("Caching Enabled:\t"+enableCaching);
			
		} catch (MalformedURLException | RemoteException | NotBoundException e)  {
            System.err.println("Error Connecting to Space at "+url);
            System.err.println(e);
        } 
	}
	
}