package api;
import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 *
 * @author Peter Cappello
 * @param <V> the task return type.
 */
public interface Task<V> extends Serializable, Callable<V> 
{ 
    @Override
    V call(); 
}