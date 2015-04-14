package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import api.Space;

public class JobRunner<T> {

	public static final int WAIT_TIME = 500;
	
	protected final Job<T> job;
	
	private Space space;
	
	public JobRunner(Job<T> job, String domainName) throws MalformedURLException, RemoteException, NotBoundException{
		this.job = job;
		
		String url = "rmi://" + domainName + ":" + Space.PORT + "/" + Space.SERVICE_NAME;
        space = (Space) Naming.lookup( url );		
	}

	
	public T run() throws RemoteException{
		System.out.println("--> Generating Tasks");
		job.generateTasks(space);
		
		System.out.println("<-- Collecting Results");
		return job.collectResults(space);
	}
	
}
