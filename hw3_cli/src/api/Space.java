package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Space<R> extends Remote {

	public static int PORT = 8001;
    public static String SERVICE_NAME = "Space";

	void assignTask ( Closure<R> task ) throws RemoteException;

	R collectResult() throws RemoteException; 

    boolean hasResult() throws RemoteException;
    
    void register( Computer computer ) throws RemoteException;
    
    void startSpace() throws RemoteException;
}