package tasks;

import api.Task;

public class TaskMandelbrot implements Task<ChunkMandelbrot> {
	
	/** Serial ID */
	private static final long serialVersionUID = -3791740955684740328L;
	
	private final Integer[] counts;
	private final int index;
	private final double lowerX;
	private final double lowerY;
	private final double shift;
	private final int iterationLimit;
	public TaskMandelbrot(Integer[] counts, int index, int iterationLimit, double lowerX, double lowerY, double shift) {
		this.counts = counts;
		this.index = index;
		this.iterationLimit = iterationLimit;
		this.lowerX = lowerX;
		this.lowerY = lowerY;
		this.shift = shift;
	}

	@Override
	public ChunkMandelbrot call() {
		
		double x = lowerX;
		double y = lowerY;
		
		for(int j = 0; j < counts.length; j++){
			int myIterationCount = getIterationCount(x, y);
			counts[j] = myIterationCount;
			x += shift;
		}

		return new ChunkMandelbrot(index, counts);
	}
	
	private int getIterationCount( double y0, double x0 )
    {
        double x = 0;
        double y = 0;
        int iteration = 0;

        
        while ((x*x + y*y <= 4) && iteration < iterationLimit )
        {
                double xTemp = x*x - y*y + x0;
                y = 2*x*y + y0;

                x = xTemp;

                iteration = iteration + 1;
        }
		
        return iteration;

    }
}
