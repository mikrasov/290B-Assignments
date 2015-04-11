package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import system.SpaceLocal;
import api.Space;

public class JobRunnerLocal<T> extends JobRunner<T> {

	private Space space;

	public JobRunnerLocal(Job<T> job) throws RemoteException {
		super(job);
		
		space = new SpaceLocal();

	}

	@Override
	public Space getSpace() {
		return space;
	}

}
