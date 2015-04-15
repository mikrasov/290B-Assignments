package tasks;

import api.Task;
import api.Result;

/**
 * Task for deconstructing the Mandlebrot job. 
 * Each task corresponds to a single row.
 * 
 * @author Michael Nekrasov
 * @author Roman Kazarin
 * 
 */
public class TaskMandelbrot implements Task< Result<ChunkMandelbrot> > {
	
	/** Serial ID */
	private static final long serialVersionUID = -3791740955684740328L;
	
	private final Integer[] counts;
	private final int index;
	private final double lowerX;
	private final double lowerY;
	private final double shift;
	private final int iterationLimit;
	
	/**
	 * Construct Mandlebrot task
	 * @param counts single row of count array
	 * @param index of row
	 * @param iterationLimit of Set
	 * @param lowerX of set
	 * @param lowerY of set
	 * @param shift of row
	 */
	public TaskMandelbrot(Integer[] counts, int index, int iterationLimit, double lowerX, double lowerY, double shift) {
		this.counts = counts;
		this.index = index;
		this.iterationLimit = iterationLimit;
		this.lowerX = lowerX;
		this.lowerY = lowerY;
		this.shift = shift;
	}

	/**
	 * Execute Task
	 */
	@Override
	public Result<ChunkMandelbrot> call() {
		
		double x = lowerX;
		double y = lowerY;
		
		for(int j = 0; j < counts.length; j++){
			int myIterationCount = getIterationCount(x, y);
			counts[j] = myIterationCount;
			x += shift;
		}

		Result<ChunkMandelbrot> result = new Result<ChunkMandelbrot>(new ChunkMandelbrot(index, counts), 0);
		return result;
	}
	
	/**
	 * Compute Itteration count
	 * @param y0 cord
	 * @param x0 cord
	 * @return count
	 */
	private int getIterationCount( double y0, double x0 ){
        double x = 0;
        double y = 0;
        int iteration = 0;

        while ((x*x + y*y <= 4) && iteration < iterationLimit ) {
            double xTemp = x*x - y*y + x0;
            y = 2*x*y + y0;

            x = xTemp;

            iteration = iteration + 1;
        }
        return iteration;
    }
	
	@Override
	public String toString() {
		return "TaskMandelbrot[" + index + "]";
	}
}
