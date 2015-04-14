package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import api.Space;

public abstract class JobRunner<T> {

	protected final Job<T> job;
		
	public JobRunner(Job<T> job) throws MalformedURLException, RemoteException, NotBoundException{
		this.job = job;		
	}

	public abstract Space getSpace();
	
	public T run() throws RemoteException{
		System.out.println("--> Generating Tasks");
		job.generateTasks(getSpace());
		
		System.out.println("<-- Collecting Results");
		return job.collectResults(getSpace());
	}
	
}
