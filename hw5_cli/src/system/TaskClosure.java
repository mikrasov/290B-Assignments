package system;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import api.Result;
import api.SharedState;
import api.Task;
import api.UpdateStateCallback;

public abstract class TaskClosure<R> implements Task<R>{
	
	private static final long serialVersionUID = 1894632443394847590L;
	
	protected static final boolean SHORT_RUNNING = true;
	protected static final boolean LONG_RUNNING = false;
	protected static final int DEFAULT_PRIORITY = 0;
	protected static final int NO_INPUTS = 0;	
	
	
	/** Id of the task as recognized by scheduler and computers */
	private long uid;
	
	// Inputs received so far
	protected final Object[] input;
	protected final String name;
	protected int joinCounter;
	
	// ID of closure to send result to and input port on that closure
    protected transient long parentUid;
	protected long targetUid;
	protected int targetPort;
	protected int priority;
	
	//Properties
	boolean isShorRunning = true;
		
	// Metrics
	List<Double> criticalLengthsOfParents = new LinkedList<Double>();
	
	public TaskClosure(String name, int priority, int numInputs, boolean isShorRunning){
		this(name, priority, numInputs, isShorRunning, -1,-1);
	}
	
	public TaskClosure(String name, int priority, int numInputs, boolean isShorRunning, long targetUid, int targetPort){
		this.name = name;
		this.input = new Object[numInputs];
		this.joinCounter = numInputs;
		this.targetUid = targetUid;
		this.targetPort = targetPort;
		
		this.isShorRunning = isShorRunning;
		this.priority = priority;
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
	public boolean isShortRunning() { return isShorRunning; }
	
	@Override
	public int getPriority()		{return priority;}
	
	@Override
	public String toString() {
		String out = name +"_"+uid+"(";
		for(Object in: input){
			out+=in+" ";
		}
		out = out.substring(0,out.length()-1)+")";
		return out+" >["+targetUid+"]";
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		TaskClosure other = (TaskClosure) obj;
		if (!Arrays.equals(input, other.input))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(input);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	@Override
	public Result<R> call(SharedState currentState, UpdateStateCallback callback){
		long clientStartTime = System.nanoTime();
		Result<R> result = execute(currentState, callback);
		result.setRunTime( (System.nanoTime() - clientStartTime) / 1000000.0 );
		return result;
	}
	
	@Override
	public void addCriticalLengthOfParent(double timeInf) {
		criticalLengthsOfParents.add(timeInf);
	}

	@Override
	public String getName() {
		return this.name+"_"+uid;
	}
	protected double getMaxCriticalLength(){
		double max = 0;
		for(double leng: criticalLengthsOfParents)
			if(leng > max) max = leng;
		
		return max;
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
	protected abstract Result<R> execute(SharedState currentState, UpdateStateCallback callback);

}
