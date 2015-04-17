package api;

import java.io.Serializable;

public interface Result<R> extends Serializable {
	
	public boolean isValue();
	
	public R getValue();
	
	public Closure<R>[] getTasks();
	
}
