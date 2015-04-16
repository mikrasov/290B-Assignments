package api;

import java.io.Serializable;
import java.util.List;

/**
 * Interface defining a task (which a job breaks down into)
 * @author Peter Cappello
 * @author Michael Nekrasov
 * @author Roman Kazarin
 * 
 * @param <V> the task return type.
 */
public interface Task<V> extends Serializable { 
    
	public boolean isComposer();
	
	public List< Task<V> > decompose();
	
	public V compose(List<Task <V>> tasks);

}