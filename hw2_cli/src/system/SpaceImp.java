package system;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import api.Computer;
import api.Result;
import api.Space;
import api.Task;


public class SpaceImp extends UnicastRemoteObject implements Space{

	/** Generate Serial ID	 */
	private static final long serialVersionUID = -4984737327501341125L;

	private static final int CYCLE_TIME = 50;
	
	private boolean isRunning = false;
	private BlockingQueue<Computer> allComputers = new LinkedBlockingQueue<Computer>();
	private BlockingQueue<Computer> availableComputers = new LinkedBlockingQueue<Computer>();
	
	private BlockingQueue<Task> tasks = new LinkedBlockingQueue<Task>();
	private BlockingQueue<Result> results = new LinkedBlockingQueue<Result>();
		
	public SpaceImp() throws RemoteException {
		super();
	}

	@Override
	public void putAll(List<Task> taskList) throws RemoteException {
		for(Task task : taskList)
			put(task);
		
	}

	@Override
	public Result take() throws RemoteException {
		Result toSend = null;
		
		while(toSend== null) try {
			toSend = results.take();
		} catch (InterruptedException e) {}
		
		System.out.println("<-- Result Sent");
		return toSend;
	}

	@Override
	public void put(Task task) throws RemoteException {
		System.out.println("--> Task Recieved: "+ task);
		try {
			tasks.put(task);
		} catch (InterruptedException e) {}
		
	}

	@Override
	public void exit() throws RemoteException {
		isRunning = false;
		while(!allComputers.isEmpty()) try {
				allComputers.take().exit();
		} catch (InterruptedException e) {}
		
		System.exit(0);

	}

	@Override
	public void register(Computer computer) throws RemoteException {
		System.out.println("Registering computer "+computer.getName());
		try {
			allComputers.put(computer);
			availableComputers.put(computer);
		} catch (InterruptedException e) {}
		
	}
	

	@Override
	public boolean hasResult() throws RemoteException {
		return !results.isEmpty();
	}

	@Override
	public void startSpace() throws RemoteException {
		isRunning = true;

		while(isRunning) try {
			if(!tasks.isEmpty() && !availableComputers.isEmpty()){
				Task task = tasks.take();
				new Dispatcher(task).start();
			}

			Thread.sleep(CYCLE_TIME);
		} catch (InterruptedException e) {}
	}
		
	private class Dispatcher extends Thread{
		
		private Task task;
		
		public Dispatcher(Task task) {
			this.task = task;
		}
		
		@Override
		public void run() {
			try{
				Computer computer = availableComputers.take();
				Result result = (Result) computer.execute(task);
				results.put(result);
				availableComputers.put(computer);
			}
			catch(RemoteException e1){
				try {
					System.err.println("RMI Error when dispatching task to Computer, abandoning computer and trying again");
					tasks.put(task);
				} catch (InterruptedException e3) {	}
				System.err.println(e1);
			}
			catch (InterruptedException e2) {} 
		}
	}
	
	public static void main(String[] args) throws RemoteException, InterruptedException {
		// Set Security Manager 
        System.setSecurityManager( new SecurityManager() );

        // Create Registry on JVM
        Registry registry = LocateRegistry.createRegistry( Space.PORT );

        // Create Space
        SpaceImp space = new SpaceImp();
        registry.rebind( Space.SERVICE_NAME, space );

        //Print Acknowledgement
        System.out.println("Space ready and registered as '"+Space.SERVICE_NAME+"' on port "+Space.PORT);

        //Start space
        space.startSpace();
	}

}
