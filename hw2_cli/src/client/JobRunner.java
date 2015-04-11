package client;

import api.Space;

public abstract class JobRunner<T> {

	public static final int WAIT_TIME = 500;
	
	protected final Job<T> job;
	
	public JobRunner(Job<T> job){
		this.job = job;
	}
	
	public abstract Space getSpace();
	
	public T run(){
		Space space = getSpace();
		job.generateTasks(space);
		return job.collectResults(space);
	}
	
}
