package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Computer<R> extends Remote {

	void addTask(Task<R> task) throws RemoteException, InterruptedException;
	
	Result<R> collectResult() throws RemoteException, InterruptedException;
	
	int getNumThreads() throws RemoteException;
	
}
