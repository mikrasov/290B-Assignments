package tsp;

import api.SharedState;

public class StateTsp implements SharedState{

	private static final long serialVersionUID = 8361224589256034886L;

	private final double bestLength;
	
	
	public StateTsp() {
		this(Double.MAX_VALUE);
	}
	
	public StateTsp(double bestLength) {
		this.bestLength = bestLength;
	}

	// a negative integer		as this object is worse than the specified object.
	// zero, 					as this object is equal to the specified object.
	// or a positive integer 	as this object is better than the specified object.
	@Override
	public int compareTo(SharedState other) {
		if(other == null)
			return -1;
		else
			return (int) (((StateTsp)other).bestLength - bestLength);
	}

	@Override
	public boolean isBetterThan(SharedState other) {
		return compareTo(other) > 0;
	}
	
	
	public boolean isBetterThan(double other) {
		return other - bestLength > 0;
	}
	
	@Override
	public String toString() {
		return "State: ["+bestLength+"]";
	}
	
}
