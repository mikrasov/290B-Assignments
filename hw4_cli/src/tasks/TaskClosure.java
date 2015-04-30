package tasks;

import api.Result;
import api.Task;

public abstract class TaskClosure<R> implements Task<R>{
	
	/** Serial ID*/
	private static final long serialVersionUID = 1894632443394847590L;
	
	/** Id of the task as recognized by scheduler and computers */
	private long uid;
	
	// Inputs received so far
	protected final Object[] input;
	protected final String name;
	protected int joinCounter;
	
	// ID of closure to send result to and input port on that closure
	protected long targetUid;
	protected int targetPort;
		
	public TaskClosure(String name, int numInputs){
		this.name = name;
		this.input = new Object[numInputs];
		this.joinCounter = numInputs;
	}
	
	public TaskClosure(String name, int numInputs, long targetUid, int targetPort){
		this.name = name;
		this.input = new Object[numInputs];
		this.joinCounter = numInputs;
		this.targetUid = targetUid;
		this.targetPort = targetPort;
	}
	
	@Override
	public void setTarget(long targetUid, int targetPort){
		this.targetUid = targetUid;
		this.targetPort = targetPort;
	}
	
	@Override
	public void setInput(final int num, final Object value){
		input[num] = value;
		joinCounter--;
	}
	
	@Override
	public void setUid(long uid){ this.uid = uid; }
	
	@Override
	public boolean isReady(){ return joinCounter == 0; }
	
	@Override
	public long getUID(){ return uid; }
	
	@Override
	public long getTargetUid(){	return targetUid; }
	
	@Override
	public int getTargetPort(){ return targetPort; }
	
	@Override
	public String toString() {
		String out = name +"_"+hashCode()+"(";
		for(Object in: input){
			out+=in+" ";
		}
		out = out.substring(0,out.length()-1)+")";
		return out+" >["+targetUid+"]";
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object arg0) {
		return this.uid == ((TaskClosure) arg0).uid;
	}
	
	@Override
	public int hashCode() {	return (int) uid; }
	
	@Override
	public Result<R> call(){
		long clientStartTime = System.nanoTime();
		Result<R> result = execute();
		result.setRunTime( (System.nanoTime() - clientStartTime) / 1000000.0 );
		return result;
	}
	
	/**
	 * To be implemented by inheriting tasks:
	 * Execute the task code that take inputs and do ONE of 2 things:
	 * 	1. Produce a ResultTasks object that is a collection of tasks to execute
	 *  							OR
	 * 	2. Produce a ResultValue object that is sent to the target of this task
	 * 
	 * @return ResultTasks OR ResultValue
	 */
	protected abstract Result<R> execute();

}
