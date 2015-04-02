package program1;

import java.rmi.RemoteException;

public class TaskMandelbrotSet implements Task<Integer[][]> {

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
		// TODO Auto-generated constructor stub
	}

	
	
	@Override
	public Integer[][] execute() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
