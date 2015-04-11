package system;

import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import api.Space;

public class SpaceDistributed extends SpaceAbstract {

	public SpaceDistributed() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws AccessException, RemoteException, InterruptedException {
		
        // Set Secutiry Manager 
        System.setSecurityManager( new SecurityManager() );

        // Create Registry on JVM
        Registry registry = LocateRegistry.createRegistry( Space.PORT );

        // Create Space
        SpaceAbstract space = new SpaceDistributed();
        registry.rebind( Space.SERVICE_NAME, space );

        //Print Acknowledgement
        System.out.println("Computer ready and registered as '"+Space.SERVICE_NAME+"' on port "+Space.PORT);

        //Wait to receive request
        while(true){
        	
        	
        	Thread.sleep(100);
        }
	}

}
