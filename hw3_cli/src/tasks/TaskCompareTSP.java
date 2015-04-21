package tasks;

import system.ResultValue;
import api.Closure;
import api.Result;
import tasks.ChunkTsp;

public class TaskAdd extends Closure<ChunkTSP>{

	/** Serial ID  */
	private static final long serialVersionUID = -401564542335493204L;

	public TaskAdd(long target, int targetPort, int num_inputs) {
		super("Compare TSP", target, targetPort, num_inputs);
	}

	@Override
	public Result<ChunkTsp> execute() {

		//find the shortest path and return that list of cities
		ChunkTsp bestChunk = input[0];
		for(int i = 1; i < input.length; i++) {
			ChunkTsp currChunk = input[1];
			if(currChunk.getBestLength() < bestChunk.getBestLength()){
				bestChunk = currChunk;
			}
		}
		
		return new ResultValue<ChunkTsp>( bestChunk );
	}

}
