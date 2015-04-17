package api;

import java.io.Serializable;

public abstract class Job<R> implements Serializable{

	protected final Space space;
	
	public Job(Space space){
		this.space = space;
	}
	
	public abstract R execute();
	
	
}
