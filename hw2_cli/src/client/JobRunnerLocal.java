package client;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import system.SpaceImp;
import system.ComputerImp;
import api.Space;

public class JobRunnerLocal<T> extends JobRunner<T> {

	private Space space;
	
	public JobRunnerLocal(Job<T> job, int numComputers) throws MalformedURLException, RemoteException, NotBoundException{
		super(job);
		this.space = new SpaceImp();
		
		for(int i=0; i<numComputers; i++)
			space.register( new ComputerImp("Local "+i));
		
	}
	
	@Override
	public Space getSpace() {
		return space;
	}
	
}
