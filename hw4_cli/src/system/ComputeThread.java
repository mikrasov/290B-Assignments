package system;

import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import util.Log;
import api.Computer;
import api.Result;
import api.Task;

public class ComputeThread<R> extends Thread implements Computer<R> {

	private final String name;
	private BlockingQueue<Task<R>> tasks = new SynchronousQueue<Task<R>>();
	private BlockingQueue<Result<R>> results = new LinkedBlockingQueue<Result<R>>();
	
	public ComputeThread(String name) throws RemoteException {
		super();
		this.name = name;
	}

	@Override
	public void addTask(Task<R> task) throws RemoteException, InterruptedException {
		Log.debug("--> "+task);
		tasks.put(task);
	}

	@Override
	public Result<R> collectResult() throws RemoteException, InterruptedException {
		Result<R> result = results.take();
		Log.debugln("<-- "+result);
		return result;
	}

	@Override
	public void run() {
		while(true)try {
			Task<R> task = tasks.take();			
			Result<R> result = task.call();
			Log.debugln("$-- "+result);
			results.put(result);
		}catch(InterruptedException e){	}
	}
	
	@Override
	public String getComputerName() throws RemoteException {
		return name;
	}

}