package system;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import api.Result;
import api.Space;
import api.Task;

public class SpaceImp extends UnicastRemoteObject implements Space{

	private static final int CYCLE_TIME = 1000;
	
	private boolean isRunning = false;
	private BlockingQueue<Computer> allComputers = new LinkedBlockingQueue<Computer>();
	private BlockingQueue<Computer> availableComputers = new LinkedBlockingQueue<Computer>();
	
	private BlockingQueue<Task> tasks = new LinkedBlockingQueue<Task>();
	private BlockingQueue<Result> results = new LinkedBlockingQueue<Result>();
		
	public SpaceImp() throws RemoteException {
		super();
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

		while(isRunning) try {
			if(!tasks.isEmpty() && !availableComputers.isEmpty()){
				Task task = tasks.take();
				new Dispatcher(task).start();
			}

			Thread.sleep(CYCLE_TIME);
		} catch (InterruptedException e) {}
	}
	
	

	
	private class Dispatcher extends Thread{
		
		private Task task;
		
		public Dispatcher(Task task) {
			this.task = task;
		}
		
		@Override
		public void run() {
			try{
				Computer computer = availableComputers.take();
				Result result = (Result) computer.execute(task);
				results.put(result);
			}
			catch(RemoteException e){
				try {
					System.err.println("RMI Error when dispatching task to Computer, abandoning computer and trying again");
					tasks.put(task);
				} catch (InterruptedException e1) {	}
			}
			catch (InterruptedException e) {} 
		}
	}
	

}
