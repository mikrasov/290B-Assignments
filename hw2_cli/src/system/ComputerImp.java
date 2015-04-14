package system;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

import api.Computer;
import api.Space;
import api.Task;


public class ComputerImp extends UnicastRemoteObject implements Computer {


	/** Serial Version UID*/
	private static final long serialVersionUID = -7947707930904891607L;
	
	private final String name;
	
	protected ComputerImp(String name) throws RemoteException {
		super();
		this.name = name;
	}

	@Override
	public <T> T execute(Task<T> task) throws RemoteException {
		System.out.println("--> Recieving Task: "+task);
		T result = task.call();
		System.out.println("<-- Returning Result");
		return result;
	}

	@Override
	public void exit() throws RemoteException {
		System.exit(0);
	}
	
	@Override
	public String getName() throws RemoteException {
		return name;
	}

	public static void main(String[] args) {
		
		String domain = (args.length > 0)? args[0]: "localhost";
		String name = (args.length > 1)? args[1]: "C"+(new Random()).nextInt(Integer.MAX_VALUE);
		
		System.out.println("Starting "+name+" on Space @ "+domain);
				
		String url = "rmi://" + domain + ":" + Space.PORT + "/" + Space.SERVICE_NAME;
        
		try {
			Space space = (Space) Naming.lookup( url );
			Computer computer = new ComputerImp(name);
			space.register(computer);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			System.err.println("No Space found at "+url);
			System.err.println(e);
		}		
	}

}
