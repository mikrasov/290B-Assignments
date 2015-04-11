package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import api.Space;

public class JobRunnerDistributed<T> extends JobRunner<T> {

	private Space space;

	public JobRunnerDistributed(Job<T> job, String domainName) throws RemoteException, MalformedURLException, NotBoundException {
		super(job);

		String url = "rmi://" + domainName + ":" + Space.PORT + "/" + Space.SERVICE_NAME;
        space = (Space) Naming.lookup( url );		
	}

	@Override
	public Space getSpace() {
		return space;
	}

	
}
