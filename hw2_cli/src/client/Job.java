package client;

import api.Space;

public interface Job {

	public void generateTasks(Space space);
	
	public void collectResults(Space space);
	
	public boolean isJobComplete();
}
