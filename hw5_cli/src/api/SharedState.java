package api;

import java.io.Serializable;

public interface SharedState extends Serializable {

	// Matches the comparable method that
	// Has a compare to method that accepts null
	// a negative integer		as this object is worse than the specified object.
	// zero, 					as this object is equal to the specified object.
	// or a positive integer 	as this object is better than the specified object.
	
	boolean isBetterThan(SharedState other);
	
	
}
