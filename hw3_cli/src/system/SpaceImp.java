package system;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import tasks.TaskSolution;
import api.Closure;
import api.Computer;
import api.Result;
import api.Space;

public class SpaceImp<R> extends UnicastRemoteObject implements Space<R>{

	/** Serial ID */
	private static final long serialVersionUID = -1147376615845722661L;

	public static final int RETRY_TIMEOUT = 500;
	public static final int CYCLE_TIME = 50;
	
	private boolean isRunning = false;
	
	private BlockingQueue<Closure<R>> waitingTasks = new LinkedBlockingQueue<Closure<R>>();
	private BlockingQueue<Computer> availableComputers = new LinkedBlockingQueue<Computer>();
	
	private Closure<R> solution = new TaskSolution<R>();
	
	protected SpaceImp() throws RemoteException { super(); }

	@Override
	public void assignTask(Closure<R> task) throws RemoteException {
		task.setTarget( solution, 0 );
		waitingTasks.add(task);
	}

	@Override
	public R collectResult() throws RemoteException {
		return solution.call().getValue();
	}

	@Override
	public boolean hasResult() throws RemoteException {
		return solution.isReady();
	}

	@Override
	public void register(Computer computer) throws RemoteException {
		System.out.println("Registering computer "+computer.getName());
		availableComputers.add(computer);
		
	}

	@Override
	public void startSpace() throws RemoteException {
		isRunning = true;

		while(isRunning) try {
			if(!waitingTasks.isEmpty() && !availableComputers.isEmpty()){
				Closure<R> task = waitingTasks.take();
				new Dispatcher(task).start();
			}

			Thread.sleep(CYCLE_TIME);
		} catch (InterruptedException e) {}
		
	}
	
	
	private class Dispatcher extends Thread{
		
		private Closure<R> task;
		
		public Dispatcher(Closure<R> task) {
			this.task = task;
		}
		
		@Override
		public void run() {
			while(true)	try{
				Computer computer = availableComputers.take();
				Result<R> result = computer.execute(task);
				
				//If Single value pass it on to target
				if(result.isValue())
					task.assignValueToTarget(result);
				
				//Else Add newly created tasks to waitlist 
				else for(Closure<R> task: result.getTasks() )
					waitingTasks.add(task);
					
				//Release Computer
				availableComputers.put(computer);
			}
			catch(RemoteException e1){
				System.err.println("RMI Error when dispatching task to Computer, abandoning computer and trying again");
				System.err.println(e1);
				try {
					Thread.sleep(RETRY_TIMEOUT);
				}
				catch (InterruptedException e3) {}
			}
			catch (InterruptedException e2) {} 
		}
	}

}
