package tasks;

import system.ResultTasks;
import system.ResultValue;
import api.Result;
import api.Task;

public class TaskFib extends TaskClosure<Integer> {

	/** Serial ID */
	private static final long serialVersionUID = 5656498160577890305L;

	public TaskFib(long target, int targetPort, int itteration) {
		super("Fib", 1, target, targetPort);
		this.setInput(0, itteration);
	}
	
	public TaskFib(int itteration) {
		super("Fib_INIT", -1, -1, 1);
		this.setInput(0, itteration);
	}

	@Override
	public Result<Integer> execute() {
		int itteration = (Integer)input[0];
		
		if(itteration <1) { 	
			return new ResultValue<Integer>(this, 0); // F(0)=0
		}
		else if(itteration == 1) { 	
			return new ResultValue<Integer>(this, 1); // F(1)=1
		}
		
		@SuppressWarnings("unchecked")
		Task<Integer>[] tasks = new Task[]{
			new TaskAdd(targetUid, targetPort),	// Adder
			new TaskFib(-1, 0, itteration-1),	// f(i-1)
			new TaskFib(-1, 1, itteration-2)	// f(i-2)
		};
		
		return new ResultTasks<Integer>(tasks);
	}

}
