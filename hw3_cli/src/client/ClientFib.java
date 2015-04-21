package client;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import tasks.TaskFib;

public class ClientFib extends Client<Integer>{

	private final int itteration;
	public ClientFib(String domain, int itteration) throws MalformedURLException, RemoteException, NotBoundException {
		super("Fibonacci", domain, new TaskFib(itteration));
		this.itteration = itteration;
	}

	@Override
	public String toString() {
		return super.toString() +"("+itteration+")";
	}

	public static void main(String[] args) throws RemoteException{


		String domain = (args.length > 0)? args[0] : "localhost";
		int fibItteration = (args.length > 1)? args[1] : 16;
				
		ClientFib client = null;
		try {
			client = new ClientFib(domain, fibItteration);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			System.err.println("No Space found at '"+domain+"'");
			System.err.println(e);
			System.exit(0);
		}
		client.begin();
		int result = client.run();
		client.end();
		
		Log.log(client +", Result: "+result);
		System.out.println(client +" = "+result);
		client.closeLog();
	}
}
