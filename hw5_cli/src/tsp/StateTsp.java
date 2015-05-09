package tsp;

import api.SharedState;
import util.Distances;

public class StateTsp implements SharedState{

	private static final long serialVersionUID = 8361224589256034886L;

	protected Distances distances;
	protected final double bestLength;
	
	public StateTsp(double[][] cities) {
		this.distances = new Distances(cities);
		this.bestLength = distances.greedyDistance(cities);
	}
	
	public StateTsp(double bestLength) {
		this.bestLength = bestLength;
	}
	
	public boolean isBetterThan(double other) {
		return other > bestLength;
	}
	
	public double distanceBetween(int src, int dest){
		return distances.between(src, dest);
	}
	
	@Override
	public String toString() {
		return "State: ["+bestLength+"]";
	}

	@Override
	public SharedState update(SharedState newState) {
		StateTsp newTspState = (StateTsp)newState;
		if( newTspState.isBetterThan(bestLength) ){
			if(newTspState.distances == null) 
				newTspState.distances = distances;
			return newState;
		}
		else 
			return this;
	}
	
	
	
}