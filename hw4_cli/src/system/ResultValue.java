package system;

import api.Result;
import api.Task;

public class ResultValue<R> implements Result<R> {

	/** Serial ID */
	private static final long serialVersionUID = -6448590220525987278L;

	private double runTime;
	private final R value;

	private final long creatorId;
	private final long targetUid;
	private final int targetPort;
	
	public ResultValue(Task<R> creator, R value){
		this.value = value;
		this.creatorId = creator.getUID();
		this.targetUid = creator.getTargetUid();
		this.targetPort = creator.getTargetPort();
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
	public long getTargetId()		{ return targetUid; }

	@Override
	public int getTargetPort()		{ return targetPort; }

	@Override
	public long getTaskCreatorId()	{ return creatorId; }

}
