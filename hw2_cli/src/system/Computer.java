package system;

import java.rmi.RemoteException;

import api.Task;

public interface Computer {

	public <T> T execute(Task<T> task) throws RemoteException;

	public void exit() throws RemoteException;
}
