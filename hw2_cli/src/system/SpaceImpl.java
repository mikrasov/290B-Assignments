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

public class SpaceImpl extends UnicastRemoteObject implements Space{

	private BlockingQueue<Computer> computers = new LinkedBlockingQueue<Computer>();

	private BlockingQueue<Task> tasks = new LinkedBlockingQueue<Task>();
	private BlockingQueue<Result> results = new LinkedBlockingQueue<Result>();
	
	public SpaceImpl() throws RemoteException {
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
		for(Computer c: computers)
			c.exit();
	}

	@Override
	public void register(Computer computer) throws RemoteException {
		computers.add(computer);	
	}

	public static void main(String[] args) throws AccessException, RemoteException, InterruptedException {
		
        // Set Secutiry Manager 
        System.setSecurityManager( new SecurityManager() );

        // Create Registry on JVM
        Registry registry = LocateRegistry.createRegistry( Space.PORT );

        // Create Space
        SpaceImpl space = new SpaceImpl();
        registry.rebind( Space.SERVICE_NAME, space );

        //Print Acknowledgement
        System.out.println("Computer ready and registered as '"+Space.SERVICE_NAME+"' on port "+Space.PORT);

        //Wait to receive request
        while(true){
        	
        	
        	Thread.sleep(100);
        }
	}
}
