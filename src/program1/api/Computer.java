package program1.api;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Generic interface of a Computer 'Server' that can exucute generic Tasks
 * @author Michael, Roman
 *
 */
public interface Computer extends Remote, Serializable{

	/** The default name of the Remote to register in RMI */
	public static String SERVICE_NAME = "Program1_Computer";
	
	/** The default port on which to start RMI (1099) */
	public static int PORT = 1099;
	
	/**
	 * Executes a generic Task
	 * @param task to execute where T is the intended return type
	 * @return result as type T which is specified by task
	 * @throws RemoteException if there is a RMI communication error
	 */
	<T> T execute(Task<T> task) throws RemoteException;
}
