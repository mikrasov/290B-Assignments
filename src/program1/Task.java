package program1;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Task<T> extends Remote {

	public static String SERVICE_NAME = "Program1_Task";
	public static int PORT = 1099;
	
    T execute() throws RemoteException;

}
