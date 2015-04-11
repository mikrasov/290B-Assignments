package client;

import java.rmi.RemoteException;

import system.SpaceLocal;
import api.Space;

public class JobRunnerDistributed<T> extends JobRunner<T> {

	private Space space;

	public JobRunnerDistributed(Job<T> job) throws RemoteException {
		super(job);

		space = new SpaceLocal();		
	}

	@Override
	public Space getSpace() {
		return space;
	}

	
}
