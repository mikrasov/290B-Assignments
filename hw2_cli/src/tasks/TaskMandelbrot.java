package tasks;

import api.Task;

public class TaskMandelbrot implements Task<Integer[]> {
	
	private final int[] counts;
	private final int index;
	private double lowerX;
	private final double lowerY;
	private final double shift;
	public TaskMandelbrot(int[] counts, int index, double lowerX, double lowerY, double shift) {
		this.counts = counts;
		this.index = index;
		this.lowerX = lowerX;
		this.lowerY = lowerY;
		this.shift = shift;
	}

	@Override
	public ChunkMandelbrot call() {
		for(int j = 0; j < counts.length; j++){
			int myIterationCount = getIterationCount(lowerX, lowerY);
			counts[j] = myIterationCount;
			lowerX += shift;
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
	
	@Override
	public boolean equals(Object arg0) {
		return ((TaskMandelbrot)arg0).id == this.id;
	}
	
	@Override
	public int hashCode() {
		return id;
	}

}
