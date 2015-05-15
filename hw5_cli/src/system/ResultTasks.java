package system;

import api.Result;
import api.Task;

public class ResultTasks<R> implements Result<R> {

	/** Serial ID */
	private static final long serialVersionUID = 6243904140408341588L;
	
	private double runTime;
	private Task<R>[] tasks;
	
	private final long creatorId;
	private final double criticalLength;
	
	public ResultTasks(long creatorId, Task<R>[] tasks, double criticalLength) {
		this.tasks = tasks;
		this.creatorId = creatorId;
		this.criticalLength = criticalLength;
	}

	@Override
	public String toString() {
		String out = "New Tasks:";
		for(Task<R> task: tasks)
			out += "| " +task+" ";
		
		return out;
	}
	
	@Override
	public double getRunTime()		{ return runTime; }

	@Override
	public void setRunTime(double time) { runTime = time; }
	
	@Override
	public boolean isValue()		{ return false; }

	@Override
	public Task<R>[] getTasks()		{ return tasks; }

	@Override
	public long getTaskCreatorId()	{ return creatorId; }

	@Override
	public R getValue() {
		throw new UnsupportedOperationException("This result is a list of tasks");
	}

	@Override
	public double getCriticalLengthOfParents() {
		return criticalLength;
	}
}
