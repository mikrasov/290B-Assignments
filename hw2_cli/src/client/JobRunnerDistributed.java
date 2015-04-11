package client;

import java.rmi.RemoteException;

import system.SpaceLocal;
import api.Space;

public class JobRunnerDistributed extends JobRunner {

	private Space space;

	public JobRunnerDistributed(Job job) throws RemoteException {
		super(job);

		space = new SpaceLocal();		
	}

	@Override
	public Space getSpace() {
		return space;
	}

	
}
