package api;

import java.io.Serializable;
import java.util.concurrent.Callable;

public abstract class Closure<R> implements Callable<Result<R>>, Serializable{
	
	/** Serial ID*/
	private static final long serialVersionUID = 1894632443394847590L;
	
	/** Id of the task as recognized by scheduler and computers */
	private long uid;
	
	// ID of closure to send result to and input port on that closure
	protected long targetUid;
	protected int targetPort;

	// Inputs received so far
	protected final Object[] input;
	protected final String name;
	protected int joinCounter;
	
	public Closure(String name, long targetUid, int targetPort, int numInputs){
		this.name = name;
		this.targetUid = targetUid;
		this.targetPort = targetPort;
		this.input = new Object[numInputs];
		this.joinCounter = numInputs;
	}
	
	public void setTarget(long targetUid, int targetPort){
		this.targetUid = targetUid;
		this.targetPort = targetPort;
	}
	
	public void setInput(final int num, final Object value){
		input[num] = value;
		joinCounter--;
	}
	
	public void setUid(long uid){ this.uid = uid; }
	
	public boolean isReady(){ return joinCounter == 0; }
	
	public long getUID(){ return uid; }
	
	public long getTargetUid(){	return targetUid; }
	
	public int getTargetPort(){ return targetPort; }
	
	@Override
	public String toString() {
		String out = name +"_"+hashCode()+"(";
		for(Object in: input){
			out+=in+",";
		}
		out = out.substring(0,out.length()-1)+")";
		return out+" >["+targetUid+"]";
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object arg0) {
		return this.uid == ((Closure) arg0).uid;
	}
	
	@Override
	public int hashCode() {	return (int) uid; }
	
	
	@Override
	public final Result<R> call(){
		//TODO: start calculating computer run time 
		Result<R> result = execute();
		//TODO: end calculating computer run time
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
