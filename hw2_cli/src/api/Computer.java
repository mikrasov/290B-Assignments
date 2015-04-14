package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Computer extends Remote {

	public <T> T execute(Task<T> task) throws RemoteException;

	public void exit() throws RemoteException;
	
	public String getName() throws RemoteException;
	
}
