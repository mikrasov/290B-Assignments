package program1.tasks;

import java.util.List;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import program1.api.Task;

/**
 * An implementation of Task that solves the Traveling Salesman Problem
 * @author Michael, Roman
 *
 */
public class TaskEuclideanTsp implements Task<List<Integer>>{

	/** Internal representation of the cities*/
	private final double[][] cities;
	
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
    /**
     * Computes traveling salesman route 
     * @return order of cities to visit represented by their ID
     */
	public List<Integer> execute() {
		double[][] distances = new double[cities.length][cities.length];

		//Construct vector of all city IDs
		ICombinatoricsVector<Integer> originalVector = Factory.createVector();
		
		//Go through each city
		for(int src=0; src < cities.length; src++){

			//Add to list of all cities
			originalVector.addValue(src);
			
			//Compute distance to neighbors (that are not already computed)
			for(int dest=src+1; dest < cities.length; dest++){
				distances[src][dest] = distance(cities[src][0],cities[src][1],cities[dest][0],cities[dest][1]);
			}
		}
		
		// Create the permutation generator by calling the appropriate method in the Factory class
		Generator<Integer> generator = Factory.createPermutationGenerator(originalVector);


		//Go through all permutations
		double bestLength = Double.MAX_VALUE;
		ICombinatoricsVector<Integer> bestOrder = null;
		for (ICombinatoricsVector<Integer> perm : generator) {
	
			double currentLength = 0;
			
			//Sum Lengths
			for(int i=1; i<perm.getSize(); i++){
				int src = perm.getValue(i-1);
				int dest = perm.getValue(i);
				
				//Compensate for triangular matrix
				if(src < dest) 
					currentLength += distances[src][dest];
				else
					currentLength += distances[dest][src]; 
			}
			
			//if current permutation is better then what is on record
			if(currentLength <= bestLength){
				bestOrder = perm;
				bestLength = currentLength;
			}
		}
		return bestOrder.getVector();
	}
	
	/**
	 * Calculates the euclidian distance between two cities
	 * @param x1 x position of city 1
	 * @param y1 y position of city 1
	 * @param x2 x position of city 2
	 * @param y2 y position of city 2
	 * @return the euclidean distance
	 */
	private double distance(double x1, double y1, double x2, double y2){
		return Math.sqrt(Math.pow( (x1-x2), 2) + Math.pow( (y1-y2), 2));
	}
	
}
