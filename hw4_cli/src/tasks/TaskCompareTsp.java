package tasks;

import system.ResultValue;
import api.Result;
import tasks.ChunkTsp;

public class TaskCompareTsp extends TaskClosure<ChunkTsp>{

	/** Serial ID  */
	private static final long serialVersionUID = -401564542335493204L;

	public TaskCompareTsp(long target, int targetPort, int num_inputs) {
		super("Compare TSP", num_inputs, target, targetPort);
	}

	@Override
	public Result<ChunkTsp> execute() {

		//find the shortest path and return that list of cities
		ChunkTsp bestChunk = (ChunkTsp) input[0];
		for(int i = 1; i < input.length; i++) {
			ChunkTsp currChunk = (ChunkTsp) input[1];
			if(currChunk.getBestLength() < bestChunk.getBestLength()){
				bestChunk = currChunk;
			}
		}
		return new ResultValue<ChunkTsp>(this, bestChunk );
	}

}
