package program1;

import java.util.LinkedList;
import java.util.List;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import program1.api.Task;

/**
 * An implementation of Task that solves the Traveling Salesman PRoblem
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
     * Computes traveling salesman route (not guranteed to be optimal) 
     * @return an aproximate order of cities to visit represented by their ID
     */
	public List<Integer> execute() {
		LinkedList<City> candidates = new LinkedList<City>();
				
		//Add locations to candidates as City objects
		for(int c=0; c < cities.length; c++){
			candidates.add(new City(c, cities[c][0], cities[c][1]));
		}
		
		
		ICombinatoricsVector<City> originalVector = Factory.createVector(candidates);

		// Create the permutation generator by calling the appropriate method in the Factory class
		Generator<City> generator = Factory.createPermutationGenerator(originalVector);

		double bestLength = Double.MAX_VALUE;
		ICombinatoricsVector<City> bestOrder = null;
		for (ICombinatoricsVector<City> perm : generator) {
			
			City current =null;
			double currentLength = 0;
			for(City next: perm){
				
				//Skip first city
				if(current != null)
					currentLength += current.distanceTo(next);
		
				current = next;
			}
			
			//if current perm is better then what is on record
			if(currentLength <= bestLength)
				bestOrder = perm;
		}

		//Translate city list to ordering
		LinkedList<Integer> order = new LinkedList<Integer>();
		for(City c: bestOrder) order.add(c.getID());
		
		return order;
	}
	
}
