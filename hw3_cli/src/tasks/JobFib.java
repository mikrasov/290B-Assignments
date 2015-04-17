package tasks;

import api.Job;
import api.Space;

public class JobFib extends Job<Integer>{

	private final int itteration;
	
	public JobFib(Space space, int itteration) {
		super(space);
		this.itteration = itteration;
	}


	
	

	@Override
	public Integer execute() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
