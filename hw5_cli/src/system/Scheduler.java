package system;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import api.Task;

public class Scheduler<R> extends Thread{
	
	private static final int INITIAL_CAPACITY = 25000;
	private BlockingQueue<Task<R>> waitingTasks = new LinkedBlockingQueue<Task<R>>();
	private BlockingQueue<Task<R>> shortTaskPool = new PriorityBlockingQueue<Task<R>>(INITIAL_CAPACITY, new TaskComparator());
	private BlockingQueue<Task<R>> longTaskPool = new PriorityBlockingQueue<Task<R>>(INITIAL_CAPACITY, new TaskComparator());
	
	public void schedule(Task<R> task){
		waitingTasks.add(task);
	}
	
	public BlockingQueue<Task<R>> getLongTaskPool() { return longTaskPool;}
	public BlockingQueue<Task<R>> getShortTaskPool() { return shortTaskPool;}
	
	@Override
	public void run() {
		while(true){
			try {
				Task<R> task = waitingTasks.take();
				
				if(task.isReady()){
				
					if(task.isShortRunning())
						shortTaskPool.add(task); 
					else
						longTaskPool.add(task);
				}
				else
					waitingTasks.add(task);
			}
			catch(InterruptedException e){}
		}
	}
	
	@Override
	public String toString() {
		return longTaskPool.size()+" remote, "+shortTaskPool.size()+" local, "+waitingTasks.size()+" waiting ";
	}
}