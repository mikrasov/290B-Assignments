package client;

import api.Result;
import api.Space;

public interface Job <T> {

	public void generateTasks(Space space);
	
	public void collectResults(Space space);
	
	public boolean isJobComplete();
	
	public T getResult();
}
