package client;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import tasks.ChunkTSP;
import tasks.TaskTSP;
import api.Result;
import api.Space;

public class JobTSP implements Job<List<Integer>> {

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
		
		//Construct vector of all city IDs
		List<Integer> originalVector = new ArrayList<Integer>(cities.length);
		
		//Add all cities to original vector
		for(int city=0; city < cities.length; city++)
			originalVector.add(city);
		
		
		//Send to space 
		for(int city=0; city < cities.length; city++){
			List<Integer> subPermutation = new ArrayList<Integer>(originalVector);
			int fixedCity = subPermutation.remove(city);
			sendToSpace(space, fixedCity, subPermutation);
		}
	}

	private void sendToSpace(Space space, int fixedCity, List<Integer> subPermutation) throws RemoteException {

		TaskTSP task = new TaskTSP(cities, fixedCity, subPermutation);
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
