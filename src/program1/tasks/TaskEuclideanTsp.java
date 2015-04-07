package program1.tasks;

import java.util.LinkedList;
import java.util.List;

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
		
		LinkedList<Integer> order = new LinkedList<Integer>();
		LinkedList<City> candidates = new LinkedList<City>();
				
		//Add locations to candidates as City objects
		for(int c=0; c < cities.length; c++){
			candidates.add(new City(c, cities[c][0], cities[c][1]));
		}
		
		//Set starting point
		City current  = candidates.removeFirst();
		order.add(current.getID());
	
		// Do while candidates remain
		while(!candidates.isEmpty()){
			City next = current.closestCity(candidates);	// Get next city
			candidates.remove(next);						// Remove from candidates
			order.add(next.getID());						// Add to output
			current = next;									// Set as next hop
		}
		
		return order;
	}
	
}
