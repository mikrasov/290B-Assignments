package system;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import api.Space;
import api.Task;

public class Computer extends UnicastRemoteObject  {

    private Space space;
    
    protected Computer(final String domainName) throws RemoteException, MalformedURLException, NotBoundException {
		super();
        
        String url = "rmi://" + domainName + ":" + Space.PORT + "/" + Space.SERVICE_NAME;
        space = (Space) Naming.lookup( url );
	}
    
	public <V> V execute(Task<V> task){
		return task.call();
	}
	
	public static void main(String[] args) throws InterruptedException, RemoteException, MalformedURLException, NotBoundException {
		
		//	Wait for an available Task from the ComputeSpace.
		//  Execute the Task.
		// Put the Task result back into the ComputeSpace.	
    
		if (args.length <1)
			System.out.println("USAGE: Computer [domain name]");
		
		Computer computer = new Computer(args[0]);
		while(true){Thread.sleep(100);}
	}
}
