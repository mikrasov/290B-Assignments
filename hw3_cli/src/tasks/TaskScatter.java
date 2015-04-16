package tasks;

import java.util.List;

import api.Task;

public class TaskScatter<T> implements Task<T> {

	/** Serial ID*/
	private static final long serialVersionUID = 2386007736052779472L;


	@Override
	public boolean isComposer() { return false; }

	@Override
	public List<Task<T>> decompose() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T compose(List<Task<T>> tasks) {
		throw new UnsupportedOperationException();
	}
	

	

}
