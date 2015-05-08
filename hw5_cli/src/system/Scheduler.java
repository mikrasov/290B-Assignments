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
	private Proxy<R> localProxy;
	
	public Scheduler(Map<Integer, Proxy<R>>  allProxies, Proxy<R> localProxy){
		this.proxies = allProxies;
		this.localProxy = localProxy;
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
					
					if(task.isReady()){
					
						if(task.isShortRunning())
							localProxy.enqueue(task); //Enqueue locally
						else
							readyTasks.add(task);
					}
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

					if(p == localProxy) continue; //skip local
					
					Task<R> task = readyTasks.take();
					p.enqueue(task);
					
				}catch(InterruptedException e){}
			}
		}
	}
}