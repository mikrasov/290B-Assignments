package tasks;

import api.Closure;
import api.Result;
import system.ResultValue;

public class TaskSolution<R> extends Closure<R> {

	public TaskSolution() {
		super("Solution", null, -1, 1);
	}

	@Override
	protected Result<R> execute() {
		return new ResultValue<R>(input[0]);
	}



}
