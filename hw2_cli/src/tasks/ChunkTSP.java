package tasks;

import java.io.Serializable;
import java.util.List;

public class ChunkTSP implements Serializable{

	/** Serial Version UID*/
	private static final long serialVersionUID = -4883401114192734278L;
	
	private final List<Integer> bestOrder;
	private final double bestLength;
	
	/**
	 * @param bestOrder
	 * @param bestLength
	 */
	public ChunkTSP(List<Integer> bestOrder, double bestLength) {
		this.bestOrder = bestOrder;
		this.bestLength = bestLength;
	}

	public List<Integer> getBestOrder() {
		return bestOrder;
	}

	public double getBestLength() {
		return bestLength;
	}

}
