package program1;

import java.rmi.RemoteException;
import java.util.List;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

public class TaskEuclideanTsp implements Task<List<Integer>>{

	private double[][] cities;
	private double[][] distance;
	
	/**
	 * Creates a new task that solves the Traveling salesman problem
	 * 
	 * @param cities	codes the x and y coordinates of city[i]: cities[i][0] is the 
	 * 					x-coordinate of city[i] and cities[i][1] is the y-coordinate 
	 * 					of city[i].
	 */
	public TaskEuclideanTsp(double[][] cities) {
		this.cities = cities;
	}

	@Override
	public List<Integer> execute() throws RemoteException {
		
		for(int c1=0; c1<cities.length; c1++){
			for(int c2=0; c2<c1; c2++){
				distance[c1][c2]=euclidieanDistance(c1, c2);
			}
		}
		
		// TODO Auto-generated method stub
		return null;
	}

	
	
	/**
	 * Computes Euclidean distance between two points
	 * @param x1 x coordinate of city 1
	 * @param x2 x coordinate of city 2
	 * @param y1 y coordinate of city 1
	 * @param y2 y coordinate of city 2
	 * @return the distance between the points
	 */
	public static double euclidieanDistance(double x1, double x2, double y1, double y2){
		return Math.sqrt(Math.pow((x1-x2), 2) + Math.pow( (y1-y2), 2));
	}
	
	/**
	 * Computes euclidiean distance between 2 cities
	 * @param c1 the id in the city[] of city 1
	 * @param c2 the id in the city[] of city 2
	 * @return the distance between the cities
	 */
	private double euclidieanDistance(int c1, int c2){
		return euclidieanDistance(cities[c1][0], cities[c2][0],cities[c1][1], cities[c2][1]);
	}
}
