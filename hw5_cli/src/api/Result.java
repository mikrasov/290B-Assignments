package api;

import java.io.Serializable;

public interface Result<R> extends Serializable {
	
	boolean isValue();
	
	R getValue();
	
	Task<R>[] getTasks();
	
	double getRunTime();
	
	void setRunTime(double d);
	
	long getTaskCreatorId();
	
	double getCriticalLengthOfParents();
}
