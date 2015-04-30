package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import tasks.TaskFib;
import util.Log;
import api.Space;

public class ClientFib{

    protected final static String CLIENT_NAME = "Fibonacci";
    
	private Space<Integer> space;
	
	@SuppressWarnings("unchecked")
	public ClientFib(String domain) throws MalformedURLException, RemoteException, NotBoundException {
		String url = "rmi://" + domain + ":" + Space.DEFAULT_PORT + "/" + Space.DEFAULT_NAME;
		space = (Space<Integer>) Naming.lookup(url);  
	}

	public int runTask( int itteration) throws RemoteException, InterruptedException { 
		space.setTask( new TaskFib(itteration) );
		return space.getSolution();
	}
	
	public static void main(String[] args) throws RemoteException, InterruptedException{
		String domain = (args.length > 0)? args[0] : "localhost";
		int fibItteration = (args.length > 1)? Integer.parseInt(args[1]) : 16;

		final String CLIENT_NAME = "Client - Fib("+fibItteration+")"; 
		ClientFib client = null;
		try {
			client = new ClientFib(domain);
		} catch (MalformedURLException | RemoteException | NotBoundException e)  {
			System.err.println("Error Connecting to Space at '"+domain+"'");
			System.err.println(e);
			System.exit(0);
		} 

		System.out.println("Starting "+CLIENT_NAME+" @ "+domain);
		
    	long clientStartTime = System.nanoTime(); 
    	
		int result = client.runTask(fibItteration);
		Log.log( CLIENT_NAME+" Time, "+( System.nanoTime() - clientStartTime) / 1000000.0 );
		
		Log.log(CLIENT_NAME +", Result: "+result);
		System.out.println(CLIENT_NAME +" = "+result);
		Log.close();
	}
}
