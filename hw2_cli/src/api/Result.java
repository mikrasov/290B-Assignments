package api;
import java.io.Serializable;

/**
 *
 * @author Peter Cappello
 * @param <T> type of return value of corresponding Task.
 */
public class Result<T> implements Serializable
{
    private final T taskReturnValue;
    private final long taskRunTime;

    public Result( T taskReturnValue, long taskRunTime )
    {
        assert taskReturnValue != null;
        assert taskRunTime >= 0;
        this.taskReturnValue = taskReturnValue;
        this.taskRunTime = taskRunTime;
    }

    public T getTaskReturnValue() { return taskReturnValue; }

    public long getTaskRunTime() { return taskRunTime; }
    
    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append( getClass() );
        stringBuilder.append( "\n\tExecution time:\n\t" ).append( taskRunTime );
        stringBuilder.append( "\n\tReturn value:\n\t" ).append( taskReturnValue.toString() );
        return stringBuilder.toString();
    }
}
