package tsp;

import util.Distances;
import api.SharedState;

public class StateTspStatic extends StateTsp {

	private static final long serialVersionUID = -282423071866310163L;

	public StateTspStatic(double[][] cities) {
		super(Double.MAX_VALUE);
		this.distances = new Distances(cities);
	}

	@Override
	public SharedState update(SharedState newState) {
		return this;
	}

}
