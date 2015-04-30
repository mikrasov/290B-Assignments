package tasks;

import system.ResultValue;
import api.Result;

public class TaskAdd extends TaskClosure<Integer>{

	private static final long serialVersionUID = -401564542335493204L;

	public static final boolean CACHABLE = true;
	public static final boolean SHORT_RUNNING = true;

	public TaskAdd(long target, int targetPort) {
		super("Add", 2, CACHABLE, SHORT_RUNNING, target, targetPort);
	}

	@Override
	public Result<Integer> execute() {
		return new ResultValue<Integer>(this, (Integer)input[0] + (Integer)input[1] );
	}

}
