package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import api.Space;
import tasks.ChunkTsp;
import tasks.TaskFib;
import util.Log;

public class ClientFib{

    protected final static String CLIENT_NAME = "Fibonacci";
    
	private Space<Integer> space;
	
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

		ClientFib client = null;
		try {
			client = new ClientFib(domain);
		} catch (MalformedURLException | RemoteException | NotBoundException e)  {
			System.err.println("No Space found at '"+domain+"'");
			System.err.println(e);
			System.exit(0);
		} 
		
		Log.log("Component, Time (ms)");
    	long clientStartTime = System.nanoTime(); 
    	
		int result = client.runTask(fibItteration);
		Log.log( "Client Total,"+( System.nanoTime() - clientStartTime) / 1000000.0 +"\n");
		
		Log.log(client +", Result: "+result);
		System.out.println(client +" = "+result);
		Log.close();
	}
}
