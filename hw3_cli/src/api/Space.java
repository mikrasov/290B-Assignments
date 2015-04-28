package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Space<R> extends Remote {

	public static int PORT = 9001;
    public static String SERVICE_NAME = "Space";

    /**
     * Assign a single task into the space
     * 
     * It will be the job of that task to break up into smaller
     * subtasks.
     * 
     * @param task to assign
     * @throws RemoteException
     */
	void assignTask ( Closure<R> task ) throws RemoteException;

	/**
	 * Get the result of the execution
	 * To see if a result is available see hasResult()
	 * 
	 * @return the result or null if one is not available
	 * @throws RemoteException
	 */
	R collectResult() throws RemoteException; 

	/**
	 * Did the space find a solution
	 * 
	 * @return true if a solution has been found
	 * @throws RemoteException
	 */
    boolean hasResult() throws RemoteException;
    
    /**
     * Register a new computer onto this space
     * 
     * @param computer to Register
     * @throws RemoteException
     */
    void register( Computer computer ) throws RemoteException;
    
    /**
     * Start the scheduler on this space
     * 
     * @throws RemoteException
     */
    void startSpace() throws RemoteException;
}