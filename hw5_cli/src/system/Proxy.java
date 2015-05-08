package system;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import util.Log;
import api.Computer;
import api.ProxyCallback;
import api.Result;
import api.SharedState;
import api.Task;

public class Proxy<R> {

	private final Computer<R> computer;
	private final int id;
	private final int numThreads;
	private final boolean isLocal;
	private final Collector collector;
	private final Dispatcher dispatcher;
	private final ProxyCallback<R> callback;
	
	private Map<Long, Task<R>> taskRegistry = new ConcurrentHashMap<Long, Task<R>>();
	private BlockingQueue<Task<R>> assignedTasks = new LinkedBlockingQueue<Task<R>>();
	
	private boolean isRunning = false;
	

	public Proxy(Computer<R> computer, int computerId, boolean isLocal, ProxyCallback<R> callback) throws RemoteException{
		this.id = computerId;
		this.computer = computer;
		this.isLocal = isLocal;
		this.numThreads = computer.getNumThreads();
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
			if(!isLocal) Log.debug("==> "+updatedState+(force?" FORCED":""));
		} catch (RemoteException e) {
			System.err.println("Undable to send state "+updatedState+" to "+toString());
		}
	}
	
	public void enqueue(Task<R> task) {
		taskRegistry.put(task.getUID(), task);
		assignedTasks.add(task);
	}
	
	public int getId(){ return id;}
	public boolean isLocal(){ return isLocal;}
	
	@Override
	public String toString() {
		return (isLocal?"Local":"Remote")+" Computer - "+numThreads+" threads as ID: '"+id+"'";
	}
	
	private class Dispatcher extends Thread {
		
		@Override
		public void run() {	
			while(isRunning) try {
				Task<R> task = assignedTasks.take();
				
				computer.addTask(task);
				if(!isLocal) Log.debug("-"+id+"-> "+task);
				
				//Throw back: don't schedule
				assignedTasks.put(task);		
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
				if(!isLocal) Log.debug("<== "+id+"- "+result);
				callback.processResult(result);
			}
			catch (InterruptedException e)	{} 
			catch (RemoteException e)		{stopProxyWithError(); return;}
		}
	}
}
