package client;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import tasks.TaskFib;
import util.Log;

public class ClientFib{

    protected final static String CLIENT_NAME = "Fibonacci";
    private long clientStartTime;
    
	private final int itteration;
	private final JobRunner<Integer> jobRunner;
    private Log log;
	
	public ClientFib(String domain, int itteration, int numComputers, Log log) throws MalformedURLException, RemoteException, NotBoundException {
		if(domain.equalsIgnoreCase("jvm")){
            this.jobRunner = new JobRunnerLocal<Integer>(new TaskFib(itteration), numComputers, log);
        }
        else {
            this.jobRunner = new JobRunnerRemote<Integer>(domain, new TaskFib(itteration));
        }

		this.itteration = itteration;
        this.log = log;
	}

	@Override
	public String toString() {
		return CLIENT_NAME+"("+itteration+")";
	}

	public int run() throws RemoteException { 
		log.log("Component, Time (ms)");
		
    	clientStartTime = System.nanoTime(); 
		int result = jobRunner.run();
		log.log( "Client Total,"+( System.nanoTime() - clientStartTime) / 1000000.0 +"\n");
		
		return result;
	}
	 
	public static void main(String[] args) throws RemoteException{
		String domain = (args.length > 0)? args[0] : "localhost";
        int numComputers = (args.length > 1)? Integer.parseInt(args[1]) : 1;
		int fibItteration = (args.length > 2)? Integer.parseInt(args[2]) : 16;

        Log log = new Log("fib");

		ClientFib client = null;
		try {
			client = new ClientFib(domain, fibItteration, numComputers, log);
		} catch (MalformedURLException e)  {
			System.err.println("No Space found at '"+domain+"'");
			System.err.println(e);
			System.exit(0);
		} catch (RemoteException e) {
            System.err.println("No Space found at '"+domain+"'");
            System.err.println(e);
            System.exit(0);
        } catch (NotBoundException e){
            System.err.println("No Space found at '"+domain+"'");
            System.err.println(e);
            System.exit(0);
        }
		int result = client.run();
		
		log.log(client +", Result: "+result);
		System.out.println(client +" = "+result);
		log.close();
	}
}
