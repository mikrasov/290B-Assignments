package program2;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

public class JobTSP implements Job<List<Integer>> {

	public static final int CHUNK_SIZE = 10000000;
	public static final int RETRY_TIMER = 1000;
	public static final int TAKE_TIMER = 500;
	
	private final double[][] cities;
	private int numTotalPermutationsSent = 0;
	private int numTotalPermutationsRecieved = 0;
	
	public JobTSP(double[][] cities) {
		this.cities = cities;
	}

	@Override
	public void generateTasks(Space space) throws RemoteException {
		
		//Construct vector of all city IDs
		ICombinatoricsVector<Integer> originalVector = Factory.createVector();
		
		//Add all cities to original vector
		for(int src=0; src < cities.length; src++)
			originalVector.addValue(src);
		
		// Create the permutation generator by calling the appropriate method in the Factory class
		Generator<Integer> generator = Factory.createPermutationGenerator(originalVector);

		//Total number of expected objects
		numTotalPermutationsSent = (int) generator.getNumberOfGeneratedObjects();

		//Send to space 
		
		int from = 0;
		
		for(int to=CHUNK_SIZE; to<numTotalPermutationsSent; to+=CHUNK_SIZE){			
			sendToSpace(space, from, to);
			from = to;
	
		}	
		
		//Send remainder
		int to = numTotalPermutationsSent;
		sendToSpace(space, from, to); 
	}

	private void sendToSpace(Space space, int from, int to) throws RemoteException {

		TaskTSP task = new TaskTSP(cities, from, to);
		System.out.println("Sending Task: "+task);
		space.put(task);
		
	}
	
	@Override
	public List<Integer> collectResults(Space space) throws RemoteException {
	
		List<Integer> bestOrder = null;
		double bestLength  = Double.MAX_VALUE;

		while(!isJobComplete()){
			Result<ChunkTSP> result = space.take();
			numTotalPermutationsRecieved++;
			
			//If the resulting chunk is better then previous chunk use that
			if(result.getTaskReturnValue().getBestLength() <= bestLength)
				bestOrder = result.getTaskReturnValue().getBestOrder();
			
		
			// Wait before trying to take next one
			try {Thread.sleep(TAKE_TIMER);} catch (InterruptedException e1) {}
		}
		
		return bestOrder;
	}

	@Override
	public boolean isJobComplete() {
		return numTotalPermutationsRecieved >= numTotalPermutationsSent;
	}

}
