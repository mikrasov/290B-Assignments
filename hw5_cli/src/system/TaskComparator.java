package system;

import java.util.Comparator;

import api.Task;

@SuppressWarnings("rawtypes")
public class TaskComparator implements Comparator<Task> {

	@Override
	public int compare(Task task1, Task task2) {
		return task1.getPriority() - task2.getPriority();
	}

}
