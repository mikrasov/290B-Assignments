package system;

import java.rmi.RemoteException;

import api.Result;
import api.SharedState;
import api.Task;

public class ResultValue<R> implements Result<R> {

	/** Serial ID */
	private static final long serialVersionUID = -6448590220525987278L;

	private double runTime;
	private final R value;
	private SharedState resultingState;

	private final long creatorId;
	
	public ResultValue(long creatorId, R value, SharedState resultingState){
		this.value = value;
		this.creatorId = creatorId;
		this.resultingState = resultingState;
	}
	
	public ResultValue(long creatorId, R value){
		this(creatorId,value,null);
	}
	
	public ResultValue(ResultValue<R> toCopy, long newOriginID){
		this(newOriginID, toCopy.value);
	}
	
	@Override
	public Task<R>[] getTasks() {
		throw new UnsupportedOperationException("This result is a single value");
	}
	
	@Override
	public String toString() {
		return "Value: "+value.toString();
	}

	@Override
	public void setRunTime(double time) {runTime = time;}
	
	@Override
	public boolean isValue() 		{ return true; }

	@Override
	public R getValue() 			{ return value; }
	
	@Override
	public double getRunTime() 		{ return runTime;}

	@Override
	public long getTaskCreatorId()	{ return creatorId; }

	@Override
	public SharedState resultingState() throws RemoteException { return resultingState;}

}
