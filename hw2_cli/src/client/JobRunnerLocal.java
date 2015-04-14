package program2;

import java.rmi.RemoteException;

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
