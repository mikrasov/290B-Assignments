package system;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import api.Result;
import api.Space;
import api.Task;
import client.Client;

public abstract class SpaceAbstract extends UnicastRemoteObject implements Space{

	private BlockingQueue<Computer> allComputers = new LinkedBlockingQueue<Computer>();
	private BlockingQueue<Computer> availableComputers = new LinkedBlockingQueue<Computer>();
	
	private BlockingQueue<Task> tasks = new LinkedBlockingQueue<Task>();
	private BlockingQueue<Result> results = new LinkedBlockingQueue<Result>();
	
	private ConcurrentMap<Task, Client> assigned;
	
	
	public SpaceAbstract() throws RemoteException {
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
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public void put(Task task) throws RemoteException {
		try {
			tasks.put(task);
		} catch (InterruptedException e) {}
		
	}

	@Override
	public void exit() throws RemoteException {
		while(!allComputers.isEmpty()) try {
				allComputers.take().exit();
		} catch (InterruptedException e) {}
		
		System.exit(0);
	}

	@Override
	public void register(Computer computer) throws RemoteException {
		availableComputers.add(computer);
		allComputers.add(computer);
	}

}
