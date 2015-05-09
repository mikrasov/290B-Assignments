package tsp;

import api.SharedState;
import util.Distance;

public class StateTsp implements SharedState{

	private static final long serialVersionUID = 8361224589256034886L;

	private final double bestLength;
	
	public StateTsp(double[][] cities) {
		this( Distance.greedyDistance(cities) );
	}
	
	public StateTsp(double bestLength) {
		this.bestLength = bestLength;
	}

	public boolean isBetterThan(StateTsp other) {
			return other.bestLength > bestLength;		
	}
	
	public boolean isBetterThan(double other) {
		return other > bestLength;
	}
	
	@Override
	public String toString() {
		return "State: ["+bestLength+"]";
	}

	@Override
	public SharedState update(SharedState newState) {
		if( ((StateTsp)newState).isBetterThan(bestLength) )
			return newState;
		else 
			return this;
	}
	
}