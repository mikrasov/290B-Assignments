package system;

import api.Result;
import api.Task;

public class ResultTasks<R> implements Result<R> {

	/** Serial ID */
	private static final long serialVersionUID = 6243904140408341588L;
	
	private double runTime;
	private Task<R>[] tasks;
	
	
	public ResultTasks(Task<R>[] tasks) {
		this.tasks = tasks;
	}

	@Override
	public boolean isValue() {	return false; }

	@Override
	public Task<R>[] getTasks() { return tasks; }

	@Override
	public String toString() {
		String out = "New Tasks: \n";
		for(Task<R> task: tasks)
			out += "| " +task+"\n";
		
		return out;
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
	public long targetId() {
		throw new UnsupportedOperationException("This result is a list of tasks");
	}

	@Override
	public int targetPort() {
		throw new UnsupportedOperationException("This result is a list of tasks");
	}

	@Override
	public R getValue() {
		throw new UnsupportedOperationException("This result is a list of tasks");
	}
}
