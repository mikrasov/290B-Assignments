package tasks;

import api.Task;

public class TaskMandelbrot implements Task<Integer[][]> {

	private static int ID_GENERATOR = 0;
	
	private final int id;
	public TaskMandelbrot() {
		// TODO Auto-generated constructor stub
		id = ID_GENERATOR++;
	}

	@Override
	public Integer[][] call() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public boolean equals(Object arg0) {
		return ((TaskMandelbrot)arg0).id == this.id;
	}

}
