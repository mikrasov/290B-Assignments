package api;

import java.io.Serializable;
import java.rmi.RemoteException;

public interface Result<R> extends Serializable {
	
	boolean isValue();
	
	R getValue();
	
	Task<R>[] getTasks();
	
	double getRunTime();
	
	void setRunTime(double d);
	
	long getTaskCreatorId();
	
	SharedState resultingState() throws RemoteException;
}
