package system;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import api.Task;

public class Scheduler<R>{
	
	private static final int INITIAL_CAPACITY = 25000;
	private BlockingQueue<Task<R>> waitingTasks = new LinkedBlockingQueue<Task<R>>();
	private BlockingQueue<Task<R>> readyTasks = new PriorityBlockingQueue<Task<R>>(INITIAL_CAPACITY, new TaskComparator());
	
	
	private Map<Integer, Proxy<R>>  proxies;
	
	public Scheduler(Map<Integer, Proxy<R>>  proxies){
		this.proxies = proxies;
		
		new ReadyChecker().start();
		new Assigner().start();
	}
	
	public void schedule(Task<R> task){
		waitingTasks.add(task);
	}
	
	class ReadyChecker extends Thread {
		@Override
		public void run() {
			while(true){
				try {
					Task<R> task = waitingTasks.take();
					
					if(task.isReady())
						readyTasks.add(task);
					else
						waitingTasks.add(task);
				}
				catch(InterruptedException e){}
			}
		}
	}
	
	class Assigner extends Thread {
		@Override
		public void run() {
			while(true){
				for(Proxy<R> p: proxies.values()) try {
				
					Task<R> task = readyTasks.take();
				
					//If local & shortrunning OR not local and not short running
					if(p.isLocal() == task.isShortRunning())
						p.enqueue(task);
					else
						readyTasks.add(task);
					
				}catch(InterruptedException e){}
			}
		}
	}
}