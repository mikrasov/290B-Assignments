package system;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import api.Task;

public class ComputerImp extends UnicastRemoteObject implements Computer {

	protected ComputerImp() throws RemoteException {
		super();
	}

	@Override
	public <T> T execute(Task<T> task) throws RemoteException {
		return task.call();
	}

	@Override
	public void exit() throws RemoteException {
		System.exit(0);
	}
	
}
