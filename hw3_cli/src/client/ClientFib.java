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

		if(args.length <1){
			System.out.println("Usage: ClientFib <num> [space domain]");
			System.exit(0);
		}
		
		int fibItteration = Integer.parseInt(args[0]);
		String domain = (args.length > 1)? args[1] : "localhost";
		
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
		
		Log.log(client +" Result: "+result);
	}
}
