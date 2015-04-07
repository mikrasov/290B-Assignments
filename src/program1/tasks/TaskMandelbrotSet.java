package program1.tasks;

import program1.api.Task;


public class TaskMandelbrotSet implements Task<Integer[][]> {

	private final double lowerLeftX, lowerLeftY, edgeLength;
	private final int nPixels, iterationLimit;
	
	/**
	 * Constructs a new Task that computes the Mandlebrot Set
	 * 
	 * @param lowerLeftX coordinate of square in complex plane
	 * @param lowerLeftY coordinate of square in complex plane
	 * @param edgeLength  of a square in the complex plane, whose sides are parallel to the axes.
	 * @param nPixels where n is number of subdivisions of square in complex plane that is parallel to axes 
	 * @param iterationLimit defines when the representative point of a region is considered to be in the Mandelbrot set.
	 */
	public TaskMandelbrotSet(double lowerLeftX, double lowerLeftY, double edgeLength, int nPixels, int iterationLimit) {
		this.lowerLeftX = lowerLeftX;
		this.lowerLeftY = lowerLeftY;
		this.edgeLength = edgeLength;
		this.nPixels = nPixels;
		this.iterationLimit = iterationLimit;
	}

	
	
	@Override
	public Integer[][] execute() {
            Integer[][] count = new Integer[nPixels][nPixels];
		
            double lowerX = this.lowerLeftX;
            double lowerY = this.lowerLeftY;
            
            //Shift will move the lower left corner by a constant amount of edgeLength/imageSize.
            double shift = edgeLength/nPixels;
            //Save the value of the lower left coordinate x-value, since this is the value you will reset to at the end of each row.
            double saveCornerX = lowerX;

            //For loop iterates over the 2D array
            for(int i = 0; i < count.length; i++)
            {
                    for(int j = 0; j < count.length; j++)
                    {
                            //Get the iteration count for the current coordinates and save them in the iterationCounts array.
                            int myIterationCount = getIterationCount(lowerX, lowerY);
                            count[i][j] = myIterationCount;

                            //shift x coordinate
                            lowerX += shift;

                    }

                    //shift the y coordinate down and reset the x coordinate
                    lowerY += shift;
                    lowerX = saveCornerX;
            }
		
	
            return count;
	}
        
        private int getIterationCount( double y0, double x0 )
        {
        // your code goes here.
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
