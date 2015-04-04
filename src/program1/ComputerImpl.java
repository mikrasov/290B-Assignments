package program1;

import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ComputerImpl implements Computer{

	@Override
	public <T> T execute(Task<T> task) throws RemoteException {
		return task.execute();
	}

	/**
	 * Run a new Computer on a new JVM
	 * 
	 * @param args				no command line arguments
	 * @throws AccessException	no permission to bind to RMI
	 * @throws RemoteException	problems connecting to computer
	 */
	public static void main(String[] args) throws AccessException, RemoteException {
		
		// Set Secutiry Manager 
		System.setSecurityManager( new SecurityManager() );

		// Create Registry on JVM
        Registry registry = LocateRegistry.createRegistry( Computer.PORT );

        // Create Computer
     	ComputerImpl computer = new ComputerImpl();
        registry.rebind( Computer.SERVICE_NAME, computer );

        //Print Acknowledgement
        System.out.println("Computer ready and registered as '"+Computer.SERVICE_NAME+"' on port "+Computer.PORT);
	}

}
