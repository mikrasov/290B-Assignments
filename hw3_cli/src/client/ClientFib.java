package client;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import tasks.TaskFib;

public class ClientFib{

    protected final static String CLIENT_NAME = "Fibonacci";
    private long clientStartTime;
    
	private final int itteration;
	private final JobRunner<Integer> jobRunner;
	
	public ClientFib(String domain, int itteration) throws MalformedURLException, RemoteException, NotBoundException {
		this.jobRunner = new JobRunner<Integer>(domain, new TaskFib(itteration));
		this.itteration = itteration;
	}

	@Override
	public String toString() {
		return CLIENT_NAME+"("+itteration+")";
	}

	public int run() throws RemoteException { 
		Log.log("Component, Time (ms)");
    	clientStartTime = System.nanoTime(); 
    	
		int result = jobRunner.run();
		
		Log.log( "Client Total,"+( System.nanoTime() - clientStartTime) / 1000000.0 +"\n");
		
		return result;
	}
	 
	
	public static void main(String[] args) throws RemoteException{


		String domain = (args.length > 0)? args[0] : "localhost";
		int fibItteration = (args.length > 1)? Integer.parseInt(args[1]) : 16;
				
		ClientFib client = null;
		try {
			client = new ClientFib(domain, fibItteration);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			System.err.println("No Space found at '"+domain+"'");
			System.err.println(e);
			System.exit(0);
		}
		int result = client.run();
		
		Log.log(client +", Result: "+result);
		System.out.println(client +" = "+result);
		Log.close();
	}
}
