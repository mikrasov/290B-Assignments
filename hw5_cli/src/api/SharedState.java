package api;

import java.io.Serializable;

public interface SharedState extends Serializable {

	SharedState update(SharedState newState);
	
}
