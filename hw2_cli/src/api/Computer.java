package program2;

import java.rmi.RemoteException;

public interface Computer {

	public <T> T execute(Task<T> task) throws RemoteException;

	public void exit() throws RemoteException;
	
}
