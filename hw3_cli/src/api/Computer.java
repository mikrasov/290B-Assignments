package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Computer extends Remote {

	public <T> Result<T> execute(Closure<T> task) throws RemoteException;
	
	public String getName() throws RemoteException;
	
}
