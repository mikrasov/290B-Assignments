package system;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

import util.Log;
import api.Closure;
import api.Computer;
import api.Result;
import api.Space;

public class ComputerImp extends UnicastRemoteObject  implements Computer {

	/** SERIAL ID */
	private static final long serialVersionUID = -2130122990828037966L;

	private final String name;
	
	protected ComputerImp(String name) throws RemoteException {
		super();
		this.name = name;
	}

	@Override
	public <T> Result<T> execute(Closure<T> task) throws RemoteException {
		Log.debug("--> "+task);
		Result<T> result = task.call();
		Log.debugln(" | "+result);
		return result;
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
			@SuppressWarnings("unchecked")
			Space<Object> space = (Space<Object>) Naming.lookup( url );
			Computer computer = new ComputerImp(name);
			space.register(computer);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			System.err.println("No Space found at "+url);
			System.err.println(e);
		}		
	}
}