package program1;

import java.rmi.RemoteException;

public class TaskEuclideanTsp implements Task{

	public TaskEuclideanTsp(double[][] cities) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object execute() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param x1 x coordinate of city 1
	 * @param x2 x coordinate of city 2
	 * @param y1 y coordinate of city 1
	 * @param y2 y coordinate of city 2
	 * @return
	 */
	public static double euclidieanDistance(int x1, int x2, int y1, int y2){
		return Math.sqrt(Math.pow((x1-x2), 2) + Math.pow( (y1-y2), 2));
	}
}
