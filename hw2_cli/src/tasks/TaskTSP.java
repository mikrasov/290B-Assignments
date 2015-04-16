package tasks;

import java.util.List;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import client.ClientTSP;
import api.Task;
import api.Result;

/**
 * Task for traveling salesman problem
 * Piece of larger task that handles a batch of permutations of cities
 * to sort through. Looks through all permutations and returns best one.
 * 
 * @author Michael Nekrasov
 * @author Roman Kazarin
 */
public class TaskTSP implements Task<Result<ChunkTSP>> {

	/** Generate Serial ID */
	private static final long serialVersionUID = -5028721366363840694L;
	private final double[][] cities;
	private final int from, to;
	
	/**
	 * Construct a new task for TSP
	 * @param cities to travel on
	 * @param from index in permutation list
	 * @param to index in permutation list
	 */
	public TaskTSP(double[][] cities, int from, int to) {
		this.cities = cities;
		this.from = from;
		this.to = to;
	}

	/**
	 * Compute distance on patch of permutations
	 */
	@Override
	public Result<ChunkTSP> call() {
		long clientStartTime = System.nanoTime();
		double[][] distances = new double[cities.length][cities.length];
		ICombinatoricsVector<Integer> originalVector = Factory.createVector();
		
		//Go through each city
		for(int src=0; src < cities.length; src++){

			//Add each city to vector of all cities
			originalVector.addValue(src);
			
			//Compute distance to neighbors (that are not already computed)
			for(int dest=src+1; dest < cities.length; dest++){
				distances[src][dest] = euclideanDistance(src, dest);
			}
		}
		
		// Create the permutation generator by calling the appropriate method in the Factory class
		Generator<Integer> generator = Factory.createPermutationGenerator(originalVector);
	
		double bestLength = Double.MAX_VALUE;
		List<Integer> bestOrder = null;
		for(ICombinatoricsVector<Integer> perm : generator.generateObjectsRange(from, to)){
	
			double currentLength = 0;
			
			//Sum Lengths
			int src = perm.getValue(perm.getSize()-1); //Add length of returning to start!
			for(int dest: perm){
				if(src < dest) //Compensate for triangular matrix
					currentLength += distances[src][dest];
					
				else
					currentLength += distances[dest][src];
			
				src = dest;
			}
			
			//if current permutation is better then what is on record
			if(currentLength <= bestLength){
				bestOrder = perm.getVector();
				bestLength = currentLength;
			}
		}
		
		Result<ChunkTSP> result = new Result<ChunkTSP>(new ChunkTSP(bestOrder, bestLength),  System.nanoTime() - clientStartTime);
		return result;
	}

	/**
	 * Calculates the euclidian distance between two cities
	 * @param city1 position array of city 1
	 * @param city2 position array of city 2
	 * @return the euclidean distance
	 */
	private double euclideanDistance(int city1, int city2){
		double x1 = cities[city1][0];
		double y1 = cities[city1][1];
		double x2 = cities[city2][0];
		double y2 = cities[city2][1];
		return Math.sqrt(Math.pow( (x1-x2), 2) + Math.pow( (y1-y2), 2));
	}
	
	@Override
	public String toString() {
		return "TSP_Task["+from+" to "+to+"]";
	}
}
