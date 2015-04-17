package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Space extends Remote {

	public static int PORT = 8001;
    public static String SERVICE_NAME = "Space";

	@SuppressWarnings("rawtypes")
	void put ( Closure task ) throws RemoteException;

	@SuppressWarnings("rawtypes")
	Result take() throws RemoteException;

    boolean hasResult() throws RemoteException;
    
    void register( Computer computer ) throws RemoteException;
    
    void startSpace() throws RemoteException;
}