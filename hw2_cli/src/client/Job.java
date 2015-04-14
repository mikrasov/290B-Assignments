package program2;

import java.rmi.RemoteException;

public interface Job <T> {

	public void generateTasks(Space space) throws RemoteException;
	
	public T collectResults(Space space) throws RemoteException;
	
	public boolean isJobComplete();
	
}
