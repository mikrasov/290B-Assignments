package tsp;

import system.ResultValue;
import system.TaskClosure;
import api.Result;
import api.SharedState;

public class TaskCompareTsp extends TaskClosure<ChunkTsp>{

	private static final long serialVersionUID = -2157191669367268779L;
	
	public static final boolean CACHABLE = false;
	public static final boolean SHORT_RUNNING = true;
	
	public TaskCompareTsp(long target, int targetPort, int num_inputs) {
		super("Compare TSP", num_inputs, CACHABLE, SHORT_RUNNING, target, targetPort);
	}

	@Override
	public Result<ChunkTsp> execute(SharedState currentState) {
		//find the shortest path and return that list of cities
		ChunkTsp bestChunk = (ChunkTsp) input[0];
		for(int i = 1; i < input.length; i++) {
			ChunkTsp currChunk = (ChunkTsp) input[1];
			if(currChunk.getBestLength() < bestChunk.getBestLength()){
				bestChunk = currChunk;
			}
		}
		return new ResultValue<ChunkTsp>(getUID(), bestChunk, new StateTsp(bestChunk.getBestLength()));
	}

	@Override
	public void updateState(SharedState updatedState) {
		// don't care about state update for comparing
	}

	@Override
	public SharedState getInitialState() {
		throw new UnsupportedOperationException("Should not initialize with comparator");
	}

}
