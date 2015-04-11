package system;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import api.Space;

public class ComputerDistributed extends ComputerAbstract{

	private Space space;

	public ComputerDistributed(String domainName) throws RemoteException, MalformedURLException, NotBoundException  {
		String url = "rmi://" + domainName + ":" + Space.PORT + "/" + Space.SERVICE_NAME;
        space = (Space) Naming.lookup( url );
	}
	
	public static void main(String[] args) throws InterruptedException, RemoteException, MalformedURLException, NotBoundException {
		
		//	Wait for an available Task from the ComputeSpace.
		//  Execute the Task.
		// Put the Task result back into the ComputeSpace.	
    
		if (args.length <1)
			System.out.println("USAGE: Computer [domain name]");
		
		ComputerAbstract computer = new ComputerDistributed(args[0]);
		while(true){Thread.sleep(100);}
	}

}
