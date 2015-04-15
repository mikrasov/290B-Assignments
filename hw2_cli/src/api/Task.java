package api;

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * Interface defining a task (which a job breaks down into)
 * @author Peter Cappello
 * @author Michael Nekrasov
 * @author Roman Kazarin
 * 
 * @param <V> the task return type.
 */
public interface Task<V> extends Serializable, Callable<V> 
{ 
    @Override
    V call();

}