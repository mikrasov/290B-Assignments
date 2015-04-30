package tasks;

import system.ResultValue;
import api.Result;

public class TaskAdd extends TaskClosure<Integer>{

	/** Serial ID  */
	private static final long serialVersionUID = -401564542335493204L;

	public TaskAdd(long target, int targetPort) {
		super("Add", 2, target, targetPort);
	}

	@Override
	public Result<Integer> execute() {
		return new ResultValue<Integer>(this, (Integer)input[0] + (Integer)input[1] );
	}

}
