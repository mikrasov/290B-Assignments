package client;

import api.Space;

public interface Job <T> {

	public void generateTasks(Space space);
	
	public T collectResults(Space space);
	
	public boolean isJobComplete();
	
}
