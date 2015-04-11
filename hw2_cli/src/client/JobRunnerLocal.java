package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import api.Space;

public class JobRunnerLocal extends JobRunner {

	private Space space;

	public JobRunnerLocal(Job job, String domainName) throws MalformedURLException, RemoteException, NotBoundException {
		super(job);
		
		String url = "rmi://" + domainName + ":" + Space.PORT + "/" + Space.SERVICE_NAME;
        space = (Space) Naming.lookup( url );

	}

	@Override
	public Space getSpace() {
		return space;
	}

}
