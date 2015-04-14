package program2;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class ComputerImp extends UnicastRemoteObject implements Computer {

	/** Serialization ID */
	private static final long serialVersionUID = 6076192830229815531L;

	protected ComputerImp(Space space) throws RemoteException {
		super();
		space.register(this);
	}

	@Override
	public <T> T execute(Task<T> task) throws RemoteException {
		return task.call();
	}

	@Override
	public void exit() throws RemoteException {
		System.exit(0);
	}
	
	public static void main(String[] args) {
		
		String domain;
		if(args.length > 0)
			domain = args[0];
		else
			domain = "localhost";
		
		System.out.println("Starting Computer on Space @ "+domain);
				
		String url = "rmi://" + domain + ":" + Space.PORT + "/" + Space.SERVICE_NAME;
        
		try {
			Space space = (Space) Naming.lookup( url );
			Computer computer = new ComputerImp(space);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			System.err.println("No Space found at "+url);
		}		
	}
}
