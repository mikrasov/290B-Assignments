package system;

import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import api.Result;
import api.Space;
import api.Task;

public abstract class SpaceAbstract extends UnicastRemoteObject implements Space{

	private BlockingQueue<ComputerAbstract> computers = new LinkedBlockingQueue<ComputerAbstract>();

	private BlockingQueue<Task> tasks = new LinkedBlockingQueue<Task>();
	private BlockingQueue<Result> results = new LinkedBlockingQueue<Result>();
	
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
		for(ComputerAbstract c: computers)
			c.exit();
	}

	@Override
	public void register(ComputerAbstract computer) throws RemoteException {
		computers.add(computer);	
	}

}
