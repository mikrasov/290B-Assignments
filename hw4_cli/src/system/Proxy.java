package system;

import Closure;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import tasks.TaskClosure;
import util.Log;
import api.Computer;
import api.Result;
import api.Task;

public class Proxy<R> {

	private final Computer<R> computer;
	
	public Proxy(Computer<R> computer){
		this.computer = computer;
	}
	

	
	private void assignValueToTarget(final Task<R> origin, final R value){
		Task<R> target = registeredTasks.get(origin.getTargetUid());
		int targetPort = origin.getTargetPort();
		target.setInput(targetPort, value);
	}
	
	private class Collector extends Thread {
		
		@Override
		public void run() {
			
		}
		
	}
	
	private class Dispatcher extends Thread {
		
		@Override
		public void run() {	
			
			
		}
	}


}
