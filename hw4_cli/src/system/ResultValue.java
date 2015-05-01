package system;

import api.Result;
import api.Task;

public class ResultValue<R> implements Result<R> {

	/** Serial ID */
	private static final long serialVersionUID = -6448590220525987278L;

	private double runTime;
	private final R value;

	private final long creatorId;
	
	public ResultValue(long creatorId, R value){
		this.value = value;
		this.creatorId = creatorId;
	}
	
	public ResultValue(ResultValue<R> toCopy, long newTargetId){
		this(toCopy.creatorId, toCopy.value);
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

}
