package tasks;

import system.ResultTasks;
import system.ResultValue;
import api.Closure;
import api.Result;

public class TaskFib extends Closure<Integer> {

	/** Serial ID */
	private static final long serialVersionUID = 5656498160577890305L;

	public TaskFib(long target, int targetPort, int itteration) {
		super("Fib", target, targetPort, 1);
		this.setInput(0, itteration);
	}
	
	public TaskFib(int itteration) {
		super("Fib_INIT", -1, -1, 1);
		this.setInput(0, itteration);
	}

	@Override
	public Result<Integer> execute() {
		int itteration = (Integer)input[0];
		
		if(itteration <2) { 	
			return new ResultValue<Integer>(1); // F(0), F(1) =1
		}
		
		@SuppressWarnings("unchecked")
		Closure<Integer>[] tasks = new Closure[]{
			new TaskAdd(targetUid, targetPort),	// Adder
			new TaskFib(-1, 0, itteration-1),	// f(i-1)
			new TaskFib(-1, 1, itteration-2)	// f(i-2)
		};
		
		return new ResultTasks<Integer>(tasks);
	}

}
