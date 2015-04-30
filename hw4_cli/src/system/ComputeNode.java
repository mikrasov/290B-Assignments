package system;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import util.Log;
import api.Computer;
import api.Result;
import api.Space;
import api.Task;

public class ComputeNode<R> extends UnicastRemoteObject implements Computer<R> {

	private static final int DEFAULT_BUFFER_SIZE = 4;
	private static final int DEFAULT_NUM_THREADS = 4;
	private final String name;
	private BlockingQueue<Task<R>> tasks;
	private BlockingQueue<Result<R>> results;
	
	private List<ComputeThread> threads;
	
	public ComputeNode(String name, int sizePrefetchBuffer, int numThreads) throws RemoteException {
		super();
		this.name = name;
		tasks = new LinkedBlockingQueue<Task<R>>(sizePrefetchBuffer);
		results = new LinkedBlockingQueue<Result<R>>();
		threads = new LinkedList<ComputeThread>();
		
		for(int i=0; i<numThreads; i++){
			ComputeThread thread = new ComputeThread(i);
			threads.add(thread);
			thread.start();
		}
	}

	public ComputeNode(String name) throws RemoteException {
		this(name, DEFAULT_BUFFER_SIZE, DEFAULT_NUM_THREADS);
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
	public String getName() throws RemoteException {
		return name;
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
				Result<R> result = task.call();
				Log.debug("-"+id+"- "+task+" = "+result);
				results.put(result);
			}catch(InterruptedException e){	}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		
		String domain = (args.length > 0)? args[0]: "localhost";
		String name = (args.length > 1)? args[1]: "C"+(new Random()).nextInt(Integer.MAX_VALUE);
		
		System.out.println("Starting "+name+" on Space @ "+domain);
				
		String url = "rmi://" + domain + ":" + Space.DEFAULT_PORT + "/" + Space.DEFAULT_NAME;
        
		try {
			@SuppressWarnings("unchecked")
			Space<Object> space = (Space<Object>) Naming.lookup( url );
			Computer computer = new ComputeNode(name);
			space.register(computer);
		} catch (MalformedURLException | RemoteException | NotBoundException e)  {
            System.err.println("Error Connecting to space at "+url);
            System.err.println(e);
        } 
	}

}