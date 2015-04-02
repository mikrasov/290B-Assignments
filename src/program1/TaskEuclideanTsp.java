package program1;

import java.rmi.RemoteException;
import java.util.List;

public class TaskEuclideanTsp implements Task<List<Integer>>{

	/**
	 * Creates a new task that solves the Traveling salesman problem
	 * 
	 * @param cities	codes the x and y coordinates of city[i]: cities[i][0] is the 
	 * 					x-coordinate of city[i] and cities[i][1] is the y-coordinate 
	 * 					of city[i].
	 */
	public TaskEuclideanTsp(double[][] cities) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Integer> execute() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Computes Euclidean distance between two points
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
