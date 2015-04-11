package system;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import api.Result;
import api.Space;
import api.Task;
import client.Client;

public class SpaceImp extends UnicastRemoteObject implements Space{

	private static final int CYCLE_TIME = 1000;
	
	private boolean isRunning = false;
	private BlockingQueue<Computer> allComputers = new LinkedBlockingQueue<Computer>();
	private BlockingQueue<Computer> availableComputers = new LinkedBlockingQueue<Computer>();
	
	private BlockingQueue<Task> tasks = new LinkedBlockingQueue<Task>();
	private BlockingQueue<Result> results = new LinkedBlockingQueue<Result>();
	
	private ConcurrentMap<Task, Computer> assigned = new ConcurrentHashMap<Task, Computer>();
	
	
	private Thread taskAssigner = new TaskAssigner();
	private Thread resultCollector = new ResultCollecter();
	
	
	
	public SpaceImp() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void putAll(List<Task> taskList) throws RemoteException {
		for(Task task : taskList)
			put(task);
		
	}

	@Override
	public Result take() throws RemoteException {
		Result toSend = null;
		
		while(toSend== null) try {
			toSend = results.take();
		} catch (InterruptedException e) {}
		
		return toSend;
	}

	@Override
	public void put(Task task) throws RemoteException {
		try {
			tasks.put(task);
		} catch (InterruptedException e) {}
		
	}

	@Override
	public void exit() throws RemoteException {
		isRunning = false;
		while(!allComputers.isEmpty()) try {
				allComputers.take().exit();
		} catch (InterruptedException e) {}
		
		System.exit(0);

	}

	@Override
	public void register(Computer computer) throws RemoteException {
		allComputers.add(computer);
		availableComputers.add(computer);
	}
	

	@Override
	public boolean hasResult() throws RemoteException {
		return !results.isEmpty();
	}

	@Override
	public void startSpace() {
		isRunning = true;
		
		taskAssigner.start();
		resultCollector.start();
	}
	
	
	private class TaskAssigner extends Thread{

		@Override
		public void run() {
			while(isRunning) try {
				
				//Assign new tasks
				if(!tasks.isEmpty()){
					Computer computer = availableComputers.take();
					Task task = tasks.take();
					
					computer.assign(task);
					assigned.putIfAbsent(task, computer);
				}

				Thread.sleep(CYCLE_TIME);
			} catch (InterruptedException e) {}
			
		}
		
	}
	
	private class ResultCollecter extends Thread{

		@Override
		public void run() {
			while(isRunning) try {
				
			
				Thread.sleep(CYCLE_TIME);
			} catch (InterruptedException e) {}
			
		}
		
	}

}
