package program1.tasks;

import java.util.List;

/**
 * An object that represents a city with euclidiean coordinates
 * 
 * @author Michael, Roman
 *
 */
public class City {

	/** ID of the city in an ordering */
	private int id;
	
	/** Euclidean coordinates */
	private double x, y;
	
	/**
	 * Create a new object that represents a city
	 * @param id Index in original array
	 * @param x	 coordinate of city
	 * @param y  coordinate of city
	 */
	public City(int id, double x, double y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}

	/**
	 * Computes the euclidiean distance to target city
	 * @param target city
	 * @return the Euclidian distance
	 */
	public double distanceTo(City target){
		return Math.sqrt(Math.pow((this.x-target.x), 2) + Math.pow( (this.y-target.y), 2));
	}
	
	/**
	 * Looks through list of target cities and finds closest one
	 * @param targets cities
	 * @return the closests city
	 */
	public City closestCity(List<City> targets){
		City closest = targets.get(0);
		for(City target: targets){
		
			if( distanceTo(target) <= distanceTo(closest) )
				closest = target;
		}
		return closest;
	}
	
	/**
	 * Gets the ID of city in original input
	 * @return the id
	 */
	public int getID(){
		return id;
	}
	
	@Override
	public boolean equals(Object o) {
		City c = (City) o;
		return c.x == x && c.y == y; 
	}
}
