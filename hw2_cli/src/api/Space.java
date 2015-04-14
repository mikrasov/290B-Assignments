package api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 *
 * @author Peter Cappello
 */
public interface Space extends Remote 
{
    public static int PORT = 8001;
    public static String SERVICE_NAME = "Space";

    void put ( Task task ) throws RemoteException;

    void putAll ( List<Task> taskList ) throws RemoteException;

    Result take() throws RemoteException;

    boolean hasResult() throws RemoteException;
    
    void exit() throws RemoteException;
    
    void register( Computer computer ) throws RemoteException;
    
    void startSpace() throws RemoteException;
}