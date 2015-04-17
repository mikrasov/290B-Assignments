package client;

import java.rmi.RemoteException;
import java.util.List;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import tasks.ChunkTSP;
import tasks.TaskTSP;
import api.Result;
import api.Space;

public class JobTSP implements Job<List<Integer>> {

	public static final int CHUNK_SIZE = 20000000;
	public static final int TAKE_TIMER = 50;
	
	private final double[][] cities;
	private int numBlocksSent = 0;
	private int numBlocksRecieved = 0;
	
	private Log log = new Log();
	public JobTSP(double[][] cities) {
		this.cities = cities;
	}

	@Override
	public void generateTasks(Space space) throws RemoteException {
		
		int numTotalPermutationsSent = 0;
		
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
		System.out.println("--> Sending Task: "+task);
		space.put(task);
		numBlocksSent++;
	}
	
	@Override
	public List<Integer> collectResults(Space space) throws RemoteException {
	
		List<Integer> bestOrder = null;
		double bestLength  = Double.MAX_VALUE;

		while(!isJobComplete()){
			Result<ChunkTSP> result = space.take();
			numBlocksRecieved++;
			
			System.out.println("<-- Recieved: "+numBlocksRecieved+" of "+numBlocksSent);
		
			log.log("Task time, "+result.getTaskRunTime() / 1000000.0);
			//If the resulting chunk is better then previous chunk use that
			if(result.getTaskReturnValue().getBestLength() <= bestLength){
				bestOrder = result.getTaskReturnValue().getBestOrder();
				bestLength = result.getTaskReturnValue().getBestLength();
			}
			
		
			// Wait before trying to take next one
			try {Thread.sleep(TAKE_TIMER);} catch (InterruptedException e1) {}
		}
		
		System.out.println("-- DONE --");
		log.log(tourToString( bestOrder) + " Length: "+bestLength );
		
		return bestOrder;
	}
	
    private String tourToString( List<Integer> cities )
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append( "Tour: " );
        for ( Integer city : cities )
        {
            stringBuilder.append( city ).append( ' ' );
        }
        return stringBuilder.toString();
    }

	@Override
	public boolean isJobComplete() {
		return numBlocksRecieved >= numBlocksSent;
	}

	public double[][] getCities(){
		return cities;
	}

	@Override
	public void setLog(Log log) {
		this.log = log;
	}
}
