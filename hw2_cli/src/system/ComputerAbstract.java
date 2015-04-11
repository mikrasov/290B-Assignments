package system;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import api.Task;

public abstract class ComputerAbstract extends UnicastRemoteObject implements Computer {

    
	protected ComputerAbstract() throws RemoteException {
		super();
	}

	public <V> V execute(Task<V> task){
		return task.call();
	}
	
	public void exit() throws RemoteException {
		System.exit(0);
		
	}
	
}
