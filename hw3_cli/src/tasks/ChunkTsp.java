package tasks;

import java.io.Serializable;
import java.util.List;

/**
 * Result of a Task for TSP. Contains both the best order of cities
 * found in this permutation and the computed distance
 * 
 * @author Michael Nekrasov
 * @author Roman Kazarin
 */
public class ChunkTSP implements Serializable{

	/** Serial Version UID*/
	private static final long serialVersionUID = -4883401114192734278L;
	
	private final List<Integer> bestOrder;
	private final double bestLength;
	
	/**
	 * Construct chunk to represent results
	 * @param bestOrder of cities to visit
	 * @param bestLength total distance of space between cities
	 */
	public ChunkTSP(final List<Integer> bestOrder, final double bestLength) {
		this.bestOrder = bestOrder;
		this.bestLength = bestLength;
	}

	/**
	 * Get best order of cities
	 * @return order
	 */
	public List<Integer> getBestOrder() {
		return bestOrder;
	}

	/**
	 * Get shortest distance between cities found so far 
	 * @return distance
	 */
	public double getBestLength() {
		return bestLength;
	}

}
