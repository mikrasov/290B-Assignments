package program2;

import java.rmi.RemoteException;

public abstract class JobRunner<T> {

	public static final int WAIT_TIME = 500;
	
	protected final Job<T> job;
	
	public JobRunner(Job<T> job){
		this.job = job;
	}
	
	public abstract Space getSpace();
	
	public T run() throws RemoteException{
		Space space = getSpace();
		
		System.out.println("--> Generating Tasks");
		job.generateTasks(space);
		
		System.out.println("<-- Collecting Results");
		return job.collectResults(space);
	}
	
}
