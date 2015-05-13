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
import api.SharedState;
import api.Space;
import api.Task;
import api.UpdateStateCallback;

public class ComputeNode<R> extends UnicastRemoteObject implements Computer<R> {

	private static final long serialVersionUID = -4962774042291137071L;
	
	private int id = -1;
	private transient BlockingQueue<Task<R>> tasks;
	private transient BlockingQueue<Result<R>> results;
	private transient List<ComputeThread> threads;
	
	private transient Space<R> space;
	private transient SharedState state;
	
	public ComputeNode(ComputeNodeSpec spec) throws RemoteException {
		super();

		results = new LinkedBlockingQueue<Result<R>>();
		threads = new LinkedList<ComputeThread>();
		tasks = new LinkedBlockingQueue<Task<R>>(spec.getBufferSize());

		for(int i=0; i<spec.getNumberOfThreads(); i++){
			ComputeThread thread = new ComputeThread(i);
			threads.add(thread);
			thread.start();
		}
	}
		
	@Override
	public void addTask(Task<R> task) throws RemoteException, InterruptedException {
		Log.verbose("--> "+task);
		
		tasks.put(task);
	}

	@Override
	public Result<R> collectResult() throws RemoteException, InterruptedException {
		Result<R> result = results.take();
		Log.verbose("<-- "+result);
		return result;
	}
	
	@Override
	public void updateState(SharedState updatedState, boolean force) throws RemoteException {
		Log.verbose("--> "+(updatedState==null?"State NULL":updatedState)+(force?" FORCED":""));
		this.state = force? updatedState : state.update(updatedState);
	}

	@Override
	public void assignSpace(Space<R> space, int spaceId) throws RemoteException {
		this.space = space;
		this.id = spaceId;
	}

	private void updateStateLocally(SharedState updatedState) {
		SharedState original = state;
		state = state.update(updatedState);
		if(space == null){
			System.err.println("Computer does not know what space it is registered on");
		}
		else if(original != state) try {
			Log.verbose("<-- "+updatedState);
			space.updateState(id, state);
		} catch (RemoteException e) {
			System.err.println("Error sending new state to server");
		}
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
				
				Log.verbose("-"+id+"- "+task+" = "+result);
				
				results.put(result);
			}catch(InterruptedException e){	}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		String domain = (args.length > 0)? args[0]: "localhost";
		int desiredPrefetchBufferSize = (args.length > 1)? Integer.parseInt(args[1]): -1;
		int desiredNumThreads = (args.length > 2)? Integer.parseInt(args[2]): -1;
		
		String url = "rmi://" + domain + ":" + Space.DEFAULT_PORT + "/" + Space.DEFAULT_NAME;
		
		try {
			System.out.println("Starting Computer on Space @ "+domain);

			Space<Object> space = (Space<Object>) Naming.lookup( url );
			ComputeNodeSpec spec = new ComputeNodeSpec(desiredNumThreads, desiredPrefetchBufferSize);
			Computer computer = new ComputeNode(spec);
			int registeredID= space.register(computer, spec);
			
			System.out.println("Computer Registered as:\t"+registeredID);
			System.out.println("Number Threads:\t\t"+spec.getNumberOfThreads());
			
			if(spec.getBufferSize()>1)
				System.out.println("Amerlioration Enabled with Buffer: "+spec.getBufferSize());
			else
				System.out.println("Amerlioration Disabled");
			
		} catch (MalformedURLException | RemoteException | NotBoundException e)  {
            System.err.println("Error Connecting to Space at "+url);
            System.err.println(e);
        } 
	}
}