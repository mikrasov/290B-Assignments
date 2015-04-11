package system;

import java.rmi.RemoteException;

import api.Result;
import api.Task;

public interface Computer {

	public void assign(Task task);
	public boolean hasResult();
	public Result getResult();
	
	public void exit() throws RemoteException;
}
