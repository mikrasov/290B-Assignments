package client;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import tasks.ChunkTSP;
import tasks.TaskTSP;
import api.Result;
import api.Space;

public class JobTSP implements Job {

	public static final int CHUNK_SIZE = 1000000;
	public static final int RETRY_TIMER = 1000;
	public static final int TAKE_TIMER = 500;
	
	private final double[][] cities;
	private long numTotalPermutationsSent = 0;
	private long numTotalPermutationsRecieved = 0;
	
	private List<Integer> bestOrder;
	private double bestLength;
	
	public JobTSP(double[][] cities) {
		this.cities = cities;
	}

	@Override
	public void generateTasks(Space space) {
		
		//Lock number permutations sent 
		numTotalPermutationsSent = Long.MAX_VALUE;
		
		//Construct vector of all city IDs
		ICombinatoricsVector<Integer> originalVector = Factory.createVector();
		
		//Add all cities to original vector
		for(int src=0; src < cities.length; src++)
			originalVector.addValue(src);
		
		// Create the permutation generator by calling the appropriate method in the Factory class
		Generator<Integer> generator = Factory.createPermutationGenerator(originalVector);


		//Send to space 
		List<List<Integer>> permutationChunk = new LinkedList< List<Integer> >();
		long numSent = 0;
		for(ICombinatoricsVector<Integer> perm : generator){
				
			permutationChunk.add( perm.getVector() );
			numSent++;
			
			if(numSent % CHUNK_SIZE  == 0){
				sendToSpace(permutationChunk, space);
				permutationChunk.clear();
			}
		}	
		sendToSpace(permutationChunk, space); //Send remainder
		
		numTotalPermutationsSent = numSent;
	}

	private void sendToSpace(List<List<Integer>> permutations, Space space) {
		if(permutations.size() == 0) return; //Don't send empty list
		
		TaskTSP task = new TaskTSP(cities, permutations);
		boolean success = false;
		
		while(!success) try {
			space.put(task);
		} catch (Exception e) {
			System.err.println("RMI Error when sending task! Rerying in "+RETRY_TIMER+" ...");
			try {Thread.sleep(RETRY_TIMER);} catch (InterruptedException e1) {}
		} finally {
			success = true;
		}
		
			
	}
	
	@Override
	public void collectResults(Space space) {
	
		//Reset results
		bestOrder = null;
		bestLength = Double.MAX_VALUE;
		
		while(!isJobComplete()){
			try{
				Result<ChunkTSP> result = space.take();
				
				//If the resulting chunk is better then previous chunk use that
				if(result.getTaskReturnValue().getBestLength() <= bestLength)
					bestOrder = result.getTaskReturnValue().getBestOrder();
				
			}catch(RemoteException e){
				System.err.println("RMI Error when sending task! Rerying in "+RETRY_TIMER+" ...");
				try {Thread.sleep(RETRY_TIMER);} catch (InterruptedException e1) {}
				continue;
			}
			
			// Wait before trying to take next one
			try {Thread.sleep(TAKE_TIMER);} catch (InterruptedException e1) {}
		}
		
	}

	@Override
	public boolean isJobComplete() {
		return numTotalPermutationsRecieved >= numTotalPermutationsSent;
	}

	@Override
	public Object getResult() {
		// TODO Auto-generated method stub
		return null;
	}

}
