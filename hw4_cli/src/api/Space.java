package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Space<R> extends Remote {

	public static int DEFAULT_PORT = 8001;
	public static String DEFAULT_NAME = "Space";
    
	void setTask(Task<R> task) throws RemoteException, InterruptedException;
	
	R getSolution() throws RemoteException, InterruptedException;
    
	int register( Computer<R> computer ) throws RemoteException;

}