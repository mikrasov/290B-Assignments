package client;

import api.Space;

public abstract class JobRunner {

	public static final int WAIT_TIME = 500;
	
	protected final Job job;
	
	public JobRunner(Job job){
		this.job = job;
	}
	
	public abstract Space getSpace();
	
	public Object run(){
		Space space = getSpace();
		job.generateTasks(space);
		job.collectResults(space);
		
		//Wait till job is complete
		while(!job.isJobComplete()) try {Thread.sleep(WAIT_TIME);} catch (InterruptedException e) {}
		
		//Return result
		return job.getResult();
	}
	
}
