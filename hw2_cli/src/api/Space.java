package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for Compute Space
 * @author Peter Cappello
 * @author Michael Nekrasov
 * @author Roman Kazarin
 */
public interface Space extends Remote 
{
    public static int PORT = 8001;
    public static String SERVICE_NAME = "Space";

    /**
     * Put a Task on list of tasks ready to dispatch
     * @param task to add
     * @throws RemoteException
     */
    @SuppressWarnings("rawtypes")
	void put ( Task task ) throws RemoteException;

    /**
     * Take a result from space
     * @return the result
     * @throws RemoteException
     */
    @SuppressWarnings("rawtypes")
	Result take() throws RemoteException;

    /**
     * Does the space have at least one result pending
     * @return true if there is a result pending
     * @throws RemoteException
     */
    boolean hasResult() throws RemoteException;
    
    /**
     * Register a new computer to handle tasks
     * @param computer
     * @throws RemoteException
     */
    void register( Computer computer ) throws RemoteException;
    
    /**
     * Starts a space, and begin dispatching tasks
     * @throws RemoteException
     */
    void startSpace() throws RemoteException;
}