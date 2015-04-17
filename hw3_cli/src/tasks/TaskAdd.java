package tasks;

import system.ResultValue;
import api.Closure;
import api.Result;

public class TaskAdd extends Closure<Integer>{

	public TaskAdd(Closure<Integer> target, int targetPort) {
		super("Add", target, targetPort, 2);
	}

	@Override
	public Result<Integer> execute() {
		return new ResultValue<Integer>( input[0] + input[1] );
	}

}
