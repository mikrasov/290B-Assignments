package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import api.Closure;
import api.Space;

public class JobRunner<R> {

private static final int CHECK_TIME = 100;
	
    protected Space<R> space;
    protected Closure<R> task;
    
	@SuppressWarnings("unchecked")
	public JobRunner( String domainName, Closure<R> task) throws MalformedURLException, RemoteException, NotBoundException    {     
		String url = "rmi://" + domainName + ":" + Space.PORT + "/" + Space.SERVICE_NAME;
        this.space = (Space<R>) Naming.lookup( url );
        this.task = task;
	}

	public R run() throws RemoteException{
		space.assignTask(task);
		
		while(true){
			if(space.hasResult())
				return space.collectResult();
			
				
			try { Thread.sleep(CHECK_TIME); } 
			catch (InterruptedException e) {}
		}
		
	}

}
