package program1.api;

import java.io.Serializable;

/**
 * A generic immutable task to send to Computer. 
 * @author Michael, Roman
 *
 * @param <T> the type that the task expects to return
 */
public interface Task<T> extends Serializable {

	/**
	 * Execute the task
	 * @return the result as type T
	 */
    T execute();

}
