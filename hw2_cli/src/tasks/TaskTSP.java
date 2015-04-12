package tasks;

import java.util.List;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import api.Task;

public class TaskTSP implements Task<ChunkTSP> {

	/** Generate Serial ID */
	private static final long serialVersionUID = -5028721366363840694L;
	private final double[][] cities;
	private final int from, to;
	
	public TaskTSP(double[][] cities, int from, int to) {
		this.cities = cities;
		this.from = from;
		this.to = to;
	}

	@Override
	public ChunkTSP call() {
		
		//Construct vector of all city IDs
		ICombinatoricsVector<Integer> originalVector = Factory.createVector();
		
		//Add all cities to original vector
		for(int src=0; src < cities.length; src++)
			originalVector.addValue(src);
		
		// Create the permutation generator by calling the appropriate method in the Factory class
		Generator<Integer> generator = Factory.createPermutationGenerator(originalVector);

		
		double bestLength = Double.MAX_VALUE;
		List<Integer> bestOrder = null;
		for(ICombinatoricsVector<Integer> perm : generator.generateObjectsRange(from, to)){
	
			double currentLength = 0;
			
			//Sum Lengths
			Integer src = null;
			for(Integer dest: perm){
				if(src != null) //skip first itteration
					currentLength += distance(src,dest);
				
				src = dest;
			}
			
			//if current permutation is better then what is on record
			if(currentLength <= bestLength){
				bestOrder = perm.getVector();
				bestLength = currentLength;
			}
		}
		return new ChunkTSP(bestOrder, bestLength);
	}


	/**
	 * Calculates the euclidian distance between two cities
	 * @param x1 x position of city 1
	 * @param y1 y position of city 1
	 * @param x2 x position of city 2
	 * @param y2 y position of city 2
	 * @return the euclidean distance
	 */
	private static double distance(double x1, double y1, double x2, double y2){
		return Math.sqrt(Math.pow( (x1-x2), 2) + Math.pow( (y1-y2), 2));
	}
	
	private double distance(int city1, int city2){
		return distance(cities[city1][0],cities[city1][1],cities[city2][0],cities[city2][1]);
	}
	
	@Override
	public String toString() {
		return "TSP_Task["+from+" to "+to+"]";
	}
}
