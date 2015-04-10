package system;

import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;
import java.util.List;

import api.Result;
import api.Space;
import api.Task;

public class SpaceImpl implements Space{

	private List<Computer> computers = new LinkedList<Computer>();

	private List<Task> tasks = new LinkedList<Task>();
	private List<Result> results = new LinkedList<Result>();
	
	public SpaceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void putAll(List<Task> taskList) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Result take() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void exit() throws RemoteException {
		// TODO Auto-generated method stub
		
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
