package tsp;

import api.SharedState;

public class StateTspStatic extends StateTsp {

	private static final long serialVersionUID = -282423071866310163L;

	public StateTspStatic() {
		super(Double.MAX_VALUE);
	}

	@Override
	public SharedState update(SharedState newState) {
		return this;
	}

}
