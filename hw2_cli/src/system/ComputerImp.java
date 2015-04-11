package system;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import api.Result;
import api.Task;

public class ComputerImp extends UnicastRemoteObject implements Computer {

	protected ComputerImp() throws RemoteException {
		super();
	}
	
	@Override
	public void assign(Task task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasResult() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Result getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void exit() throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
}
