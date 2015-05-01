package tasks;

import system.ResultTasks;
import system.ResultValue;
import api.Result;
import api.Task;

public class TaskFib extends TaskClosure<Integer> {

	private static final long serialVersionUID = 696763855946823023L;

	public static final boolean CACHABLE = true;
	public static final boolean SHORT_RUNNING = true;
	
	public TaskFib(long target, int targetPort, int itteration) {
		super("Fib", 1, CACHABLE, SHORT_RUNNING, target, targetPort);
		this.setInput(0, itteration);
	}
	
	public TaskFib(int itteration) {
		super("Fib_INIT", 1, CACHABLE, SHORT_RUNNING);
		this.setInput(0, itteration);
	}

	@Override
	public Result<Integer> execute() {
		int itteration = (Integer)input[0];
		
		if(itteration <1) { 	
			return new ResultValue<Integer>(getUID(), 0); // F(0)=0
		}
		else if(itteration == 1) { 	
			return new ResultValue<Integer>(getUID(), 1); // F(1)=1
		}
		
		@SuppressWarnings("unchecked")
		Task<Integer>[] tasks = new Task[]{
			new TaskAdd(targetUid, targetPort),	// Adder
			new TaskFib(-1, 0, itteration-1),	// f(i-1)
			new TaskFib(-1, 1, itteration-2)	// f(i-2)
		};
		
		return new ResultTasks<Integer>(getUID(), tasks);
	}

}
