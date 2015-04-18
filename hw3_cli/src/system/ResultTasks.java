package system;

import api.Closure;
import api.Result;

public class ResultTasks<R> implements Result<R> {

	/** Serial ID */
	private static final long serialVersionUID = 6243904140408341588L;
	
	private Closure<R>[] tasks;
	
	public ResultTasks(Closure<R>[] tasks) {
		this.tasks = tasks;
	}

	@Override
	public boolean isValue() {	return false; }

	@Override
	public R getValue() {
		throw new UnsupportedOperationException("This result is a list of tasks");
	}

	@Override
	public Closure<R>[] getTasks() { return tasks; }

	@Override
	public String toString() {
		String out = "New Tasks: \n";
		for(Closure task: tasks)
			out += "| " +task.toVerboseString()+"\n";
		
		return out;
	}
}
