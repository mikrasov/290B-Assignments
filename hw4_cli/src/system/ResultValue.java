package system;

import api.Result;
import api.Task;

public class ResultValue<R> implements Result<R> {

	/** Serial ID */
	private static final long serialVersionUID = -6448590220525987278L;

	private double runTime;
	private final R value;
	
	private final long targetUid;
	private final int targetPort;
	
	public ResultValue(Task<R> origin, R value){
		this.value = value;
		this.targetUid = origin.getTargetUid();
		this.targetPort = origin.getTargetPort();
	}
	
	@Override
	public boolean isValue() { return true; }

	@Override
	public R getValue() { return value; }

	@Override
	public Task<R>[] getTasks() {
		throw new UnsupportedOperationException("This result is a single value");
	}
	
	@Override
	public String toString() {
		return "Value: "+value.toString();
	}

	@Override
	public double getRunTime() {
		return runTime;
	}

	@Override
	public void setRunTime(double time) {
		runTime = time;
	}

	@Override
	public long getTargetId() {
		return targetUid;
	}

	@Override
	public int getTargetPort() {
		return targetPort;
	}

}
