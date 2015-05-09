package system;

import api.SharedState;

public class StateBlank implements SharedState {

	private static final long serialVersionUID = -448945541763944065L;

	@Override
	public SharedState update( SharedState newState) {
		return this;
	}

	@Override
	public String toString() {
		return "State: [BLANK]";
	}
}
