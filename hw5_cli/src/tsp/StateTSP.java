package tsp;

import api.SharedState;

public class StateTSP implements SharedState{

	private static final long serialVersionUID = 8361224589256034886L;

	private final int bestLength;
	
	public StateTSP(int bestLength) {
		this.bestLength = bestLength;
	}
	
	public StateTSP() {
		this(Integer.MAX_VALUE);
	}

	// a negative integer		as this object is worse than the specified object.
	// zero, 					as this object is equal to the specified object.
	// or a positive integer 	as this object is better than the specified object.
	@Override
	public int compareTo(SharedState other) {
		if(other == null)
			return -1;
		else
			return ((StateTSP)other).bestLength - bestLength;
	}

	@Override
	public boolean isBetterThan(SharedState other) {
		return compareTo(other) > 1;
	}

}
