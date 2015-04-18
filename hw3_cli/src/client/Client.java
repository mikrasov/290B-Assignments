package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import api.Space;
import api.Closure;

public class Client<R> {

	private static final int CHECK_TIME = 100;
	
    private long clientStartTime;
    
    protected final String name;
    protected Space<R> space;
    protected Closure<R> task;
    
	@SuppressWarnings("unchecked")
	public Client( final String name, String domainName, Closure<R> task) throws MalformedURLException, RemoteException, NotBoundException    {     
		String url = "rmi://" + domainName + ":" + Space.PORT + "/" + Space.SERVICE_NAME;
        this.space = (Space<R>) Naming.lookup( url );
        this.task = task;
        this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
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
	
	
    public void begin() { 
    	Log.log("Component, Time (ms)");
    	clientStartTime = System.nanoTime(); 
    }
    
    public void end()  { 
    	Log.log( "Client Total,"+( System.nanoTime() - clientStartTime) / 1000000.0 +"\n");
    	Log.close();
    }

}
