package api;

import java.io.Serializable;

/**
 * Interface for results of task execution
 * @author Peter Cappello
 * @author Michael Nekrasov
 * @author Roman Kazarin
 * @param <T> type of return value of corresponding Task.
 */
public class Result<T> implements Serializable
{
    /** Serializable UID */
	private static final long serialVersionUID = -1917024836619089922L;
	
	private final T taskReturnValue;
    private final long taskRunTime;

    /**
     * Construct a new Partial Result
     * @param taskReturnValue of partial result
     * @param taskRunTime of partial result
     */
    public Result( final T taskReturnValue, long taskRunTime )
    {
        assert taskReturnValue != null;
        assert taskRunTime >= 0;
        this.taskReturnValue = taskReturnValue;
        this.taskRunTime = taskRunTime;
    }

    /**
     * Result return value
     * @return value
     */
    public T getTaskReturnValue() { return taskReturnValue; }

    /**
     * Result run time on computer
     * @return runtime in nanoseconds
     */
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
