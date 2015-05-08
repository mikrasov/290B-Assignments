package system;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import util.Log;
import api.Capabilities;
import api.Computer;
import api.ProxyCallback;
import api.Result;
import api.SharedState;
import api.Task;

public class Proxy<R> {

	private final Computer<R> computer;
	private final int id;
	private final Collector collector;
	private final Dispatcher dispatcher;
	private final ProxyCallback<R> callback;
	private final Capabilities spec;
	
	private Map<Long, Task<R>> taskRegistry = new ConcurrentHashMap<Long, Task<R>>();
	private BlockingQueue<Task<R>> assignedTasks = new LinkedBlockingQueue<Task<R>>();
	
	private boolean isRunning = false;
	

	public Proxy(Computer<R> computer, Capabilities spec, int computerId, ProxyCallback<R> callback) throws RemoteException{
		this.id = computerId;
		this.computer = computer;
		this.spec = spec;
		this.collector = new Collector();
		this.dispatcher = new Dispatcher();
		this.callback = callback;
		
		isRunning = true;
		collector.start();
		dispatcher.start();
	}
	
	private synchronized void stopProxyWithError(){
		if(!isRunning) return;
		
		isRunning = false;
		System.out.println("Error accessing "+toString());

		callback.doOnError(id, taskRegistry.values());
	}
	
	public void updateState(SharedState updatedState, boolean force) {
		try {
			computer.updateState(updatedState, force);
			Log.debug("==> "+updatedState+(force?" FORCED":""));
		} catch (RemoteException e) {
			System.err.println("Undable to send state "+updatedState+" to "+toString());
		}
	}
	
	public void enqueue(Task<R> task) {
		taskRegistry.put(task.getUID(), task);
		assignedTasks.add(task);
	}
	
	public int getId(){ return id;}
	
	@Override
	public String toString() {
		return "Computer - "+spec.getNumberOfThreads()+" threads as ID: '"+id+"'";
	}
	
	private class Dispatcher extends Thread {
		
		@Override
		public void run() {	
			while(isRunning) try {
				Task<R> task = assignedTasks.take();

				Log.debug("="+id+"=> "+task);
				computer.addTask(task);
			} 
			catch (InterruptedException e)	{} 
			catch (RemoteException e)		{stopProxyWithError(); return;}
		}
	}
	
	private  class Collector extends Thread {
		@Override
		public void run() {
			while(isRunning) try {
				Result<R> result = computer.collectResult();
				taskRegistry.remove(result.getTaskCreatorId());
				Log.debug("<== "+id+"- "+result);
				callback.processResult(result);
			}
			catch (InterruptedException e)	{} 
			catch (RemoteException e)		{stopProxyWithError(); return;}
		}
	}
}
