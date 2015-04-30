package system;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
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
	
	public ComputeNode() throws RemoteException {
		this(true, true);
	}
	
	public ComputeNode(boolean enableAmerlioration, boolean multiThread) throws RemoteException {
		this(enableAmerlioration?BUFFER_DEFAULT_SIZE:BUFFER_NO_PREFETCH, 
			 multiThread?Runtime.getRuntime().availableProcessors():SINGLE_PROCESSOR);
	}
	
	public ComputeNode(int prefetchBufferSize, int numThreads) throws RemoteException {
		super();
		results = new LinkedBlockingQueue<Result<R>>();
		threads = new LinkedList<ComputeThread>();
		
		tasks = new LinkedBlockingQueue<Task<R>>(prefetchBufferSize);
		
		for(int i=0; i<numThreads; i++){
			ComputeThread thread = new ComputeThread(i);
			threads.add(thread);
			thread.start();
		}
	}
	
	@Override
	public void addTask(Task<R> task) throws RemoteException, InterruptedException {
		Log.debug("--> "+task);
		tasks.put(task);
	}

	@Override
	public Result<R> collectResult() throws RemoteException, InterruptedException {
		Result<R> result = results.take();
		Log.debug("<-- "+result);
		return result;
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
				results.put(result);
			}catch(InterruptedException e){	}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		String domain = (args.length > 0)? args[0]: "localhost";
		boolean enableAmerlioration = (args.length > 1)? Boolean.parseBoolean(args[1]): true;
		boolean multiThread = (args.length > 2)? Boolean.parseBoolean(args[2]): true;
		
		String url = "rmi://" + domain + ":" + Space.DEFAULT_PORT + "/" + Space.DEFAULT_NAME;
        
		try {
			System.out.println("Starting Computer on Space @ "+domain);

			Space<Object> space = (Space<Object>) Naming.lookup( url );
			Computer computer = new ComputeNode(enableAmerlioration,multiThread);
			int id = space.register(computer);
			System.out.println("Computer Registered as:\t"+id);
			System.out.println("Number Threads:\t\t"+computer.getNumThreads());
			System.out.println("Amerlioration Enabled:\t"+enableAmerlioration);
			
			
		} catch (MalformedURLException | RemoteException | NotBoundException e)  {
            System.err.println("Error Connecting to Space at "+url);
            System.err.println(e);
        } 
	}




}