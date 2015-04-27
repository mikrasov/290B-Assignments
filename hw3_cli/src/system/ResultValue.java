package system;

import api.Closure;
import api.Result;

public class ResultValue<R> implements Result<R> {

	/** Serial ID */
	private static final long serialVersionUID = -6448590220525987278L;

	private double runTime;
	private final R value;
	
	public ResultValue(R value){
		this.value = value;
	}
	
	@Override
	public boolean isValue() { return true; }

	@Override
	public R getValue() { return value; }

	@Override
	public Closure<R>[] getTasks() {
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

}
