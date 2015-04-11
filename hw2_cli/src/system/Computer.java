package system;

import java.rmi.RemoteException;

import api.Task;

public interface Computer {

	public <V> V execute(Task<V> task);
	
	public void exit() throws RemoteException;
}
