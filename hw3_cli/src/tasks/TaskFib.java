package tasks;

import system.ResultTasks;
import system.ResultValue;
import api.Closure;
import api.Result;

public class TaskFib extends Closure<Integer> {

	public TaskFib(Closure<Integer> target, int targetPort, int itteration) {
		super("Fib", target, targetPort, 1);
		this.setInput(0, itteration);
	}
	
	public TaskFib(int itteration) {
		super("Fib INIT", null, -1, 1);
		this.setInput(0, itteration);
	}

	@Override
	public Result<Integer> execute() {
		int itteration = input[0];
		
		if(itteration <2) { 	
			return new ResultValue<Integer>(1); // F(0), F(1) =1
		}
		
		@SuppressWarnings("unchecked")
		Closure<Integer>[] tasks = new Closure[3];
		tasks[0] = new TaskAdd(target, targetPort);			// Adder
		tasks[1] = new TaskFib(tasks[0], 0, itteration-1);	// f(i-1)
		tasks[2] = new TaskFib(tasks[0], 1, itteration-2);	// f(i-2)

		return new ResultTasks<Integer>(tasks);
	}

}
