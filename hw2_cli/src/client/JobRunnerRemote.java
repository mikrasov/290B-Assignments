package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import api.Space;

public class JobRunnerRemote<T> extends JobRunner<T>{

	private Space space;
	
	public JobRunnerRemote(Job<T> job, String domainName) throws MalformedURLException, RemoteException, NotBoundException{
		super(job);
		
		String url = "rmi://" + domainName + ":" + Space.PORT + "/" + Space.SERVICE_NAME;
        space = (Space) Naming.lookup( url );		
	}

	@Override
	public Space getSpace() {
		return space;
	}

	
}
