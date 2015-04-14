package program2;

import java.util.List;

public class ChunkTSP {

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
