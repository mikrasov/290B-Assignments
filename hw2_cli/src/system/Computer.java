package system;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import api.Result;
import api.Task;

public class Computer extends UnicastRemoteObject 
{

    
    protected Computer() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}
    
	public Result execute(Task t){
		
		//TODO: implement
		return null;
	}
	
	public static void main(String[] args) {
		/* TODO: Its main method gets the domain name of its Space's machine from the 
		 command line. Using Naming.lookup, it gets a remote reference to the Space 
		service from the rmiregistry.
		*/
		
		//Register with space
		
		
		while(true){
			//	Wait for an available Task from the ComputeSpace.
			//  Execute the Task.
			// Put the Task result back into the ComputeSpace.	
        	
        	Thread.sleep(100);
        }
		
	}
}
