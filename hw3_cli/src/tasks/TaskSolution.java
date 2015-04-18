package tasks;

import system.ResultValue;
import api.Closure;
import api.Result;

public class TaskSolution<R> extends Closure<R> {

	/** Serial ID */
	private static final long serialVersionUID = 650090682148073997L;

	public TaskSolution() {
		super("Solution", -1, -1, 1);
	}

	@Override
	protected Result<R> execute() {
		return new ResultValue<R>( (R) input[0]);
	}

}
