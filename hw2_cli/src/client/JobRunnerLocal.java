package client;

import java.rmi.RemoteException;

import system.SpaceImp;
import api.Space;

public class JobRunnerLocal<T> extends JobRunner<T> {

	private Space space;

	public JobRunnerLocal(Job<T> job) throws RemoteException {
		super(job);
		
		space = new SpaceImp();

	}

	@Override
	public Space getSpace() {
		return space;
	}

}
