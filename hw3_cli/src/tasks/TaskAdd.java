package tasks;

import system.ResultValue;
import api.Closure;
import api.Result;

public class TaskAdd extends Closure<Integer>{

	/** Serial ID  */
	private static final long serialVersionUID = -401564542335493204L;

	public TaskAdd(Closure<Integer> target, int targetPort) {
		super("Add", target, targetPort, 2);
	}

	@Override
	public Result<Integer> execute() {
		return new ResultValue<Integer>( (Integer)input[0] + (Integer)input[1] );
	}

}
