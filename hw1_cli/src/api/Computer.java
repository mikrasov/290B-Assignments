package program1;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Computer extends Remote, Serializable{

	public static String SERVICE_NAME = "Program1_Computer";
	public static int PORT = 1099;
	
	<T> T execute(Task<T> task) throws RemoteException;
}
