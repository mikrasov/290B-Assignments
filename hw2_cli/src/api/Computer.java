package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for defining a basic computer that a space can do work on
 * 
 * @author Michael Nekrasov
 * @author Roman Kazarin
 *
 */
public interface Computer extends Remote {

	/**
	 * Execute a task on this computer
	 * @param task to execute
	 * @return corresponding result
	 * @throws RemoteException
	 */
	public <T> T execute(Task<T> task) throws RemoteException;
	
	/**
	 * Name of this computer
	 * @return the assigned name
	 * @throws RemoteException
	 */
	public String getName() throws RemoteException;
	
}
