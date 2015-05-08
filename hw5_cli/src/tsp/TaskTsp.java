package tsp;

import java.util.ArrayList;
import java.util.List;

import system.ResultTasks;
import system.ResultValue;
import system.TaskClosure;
import util.PermutationEnumerator;
import util.Distance;
import api.Result;
import api.SharedState;
import api.Task;
import api.UpdateStateCallback;

public class TaskTsp extends TaskClosure<ChunkTsp> {

	private static final long serialVersionUID = -2567928535294012341L;
	
	private static final int BASIC_TSP_PROBLEM_SIZE = 11;
	private static final int NUMBER_OF_INPUTS = 4;
	private static final boolean ENABLE_BOUNDING = true;
	
	
	private final StateTsp initialState;
	private StateTsp currentState;
	
	private TaskTsp(long target, int targetPort, List<Integer> fixedCities, double fixedCitiesLength, List<Integer> toPermute, double[][] cities) {
		super("TSP", fixedCities.size(), NUMBER_OF_INPUTS, LONG_RUNNING, target, targetPort);
		initialState = null;
		this.setInput(0, cities);
		this.setInput(1, fixedCities);
		this.setInput(2, toPermute);
		this.setInput(3, fixedCitiesLength);
	}

	public TaskTsp(double[][] cities){
		super("TSP-INIT", DEFAULT_PRIORITY, NUMBER_OF_INPUTS, SHORT_RUNNING);
		initialState = new StateTsp(cities);
		this.setInput(0, cities);
		this.setInput(1, new ArrayList<Integer>());
		List<Integer> toPermute = new ArrayList<Integer>();
		for(int i = 0; i < cities.length; i++){
			toPermute.add(i);
		}
		this.setInput(2, toPermute);
		this.setInput(3, 0.0);
	}

	private boolean shouldTerminateExecution(double bestPartialLength){
		boolean shortut = ENABLE_BOUNDING && currentState.isBetterThan(bestPartialLength);
		
		if(shortut)
			System.err.println("Can Shortcut execution "+currentState+" BT "+bestPartialLength);
		
		return shortut;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Result<ChunkTsp> execute(SharedState initialState, UpdateStateCallback callback) {
		double[][] cities = (double[][])input[0];
		List<Integer> fixedCities = (List<Integer>)input[1];
		List<Integer> toPermute = (List<Integer>)input[2];
		double fixedCitiesLength = (Double)input[3];
		
		currentState = (StateTsp)initialState;
		
		ResultValue<ChunkTsp> TERMINATION_VALUE = new ResultValue<ChunkTsp>(getUID(), new ChunkTsp(fixedCities, Double.MAX_VALUE));
		
		//Shortcut Computation
		if(shouldTerminateExecution(fixedCitiesLength)) return TERMINATION_VALUE;
		
		if(toPermute.size() <= BASIC_TSP_PROBLEM_SIZE){

			//Pre-compute distances
			double[][] distances = Distance.allDistances(cities);

			PermutationEnumerator<Integer> generator = new PermutationEnumerator<Integer>(toPermute);

			double bestLength = Double.MAX_VALUE;
			List<Integer> bestOrder = null;
			for(List<Integer> perm : generator){
				double currentLength = 0;
				perm.addAll(fixedCities);

				int src = fixedCities.get(fixedCities.size()-1);
				for(int dest: perm){
					if(src < dest) //Compensate for triangular matrix
						currentLength += distances[src][dest];
						
					else
						currentLength += distances[dest][src];
				
					src = dest;
				}
				
				//Shortcut Computation
				if(shouldTerminateExecution(fixedCitiesLength)) return TERMINATION_VALUE;
				
				//if current permutation is better then what is on record
				if(currentLength <= bestLength){
					bestOrder = perm;
					bestLength = currentLength;
					
					//Found a new candidate for better length
					callback.updateState(new StateTsp(bestLength) );
				}
			}
			return new ResultValue<ChunkTsp>(getUID(), new ChunkTsp(bestOrder, bestLength));
		}
		else {
			Task<ChunkTsp>[] tasks = new Task[toPermute.size()+1];
			tasks[0] = new TaskCompareTsp(targetUid, targetPort, toPermute.size());
			for(int i = 1; i <= toPermute.size(); i++){
				//clone the fixed cities list
				List<Integer> new_fixedCities = copyList(fixedCities);
				//clone the toPermute list
				List<Integer> new_toPermute = copyList(toPermute);
				
				Integer newCityToAdd = new_toPermute.get(i-1);
				
				double newfixedCitiesLength = fixedCitiesLength;
				if(fixedCities.size() > 0){
					newfixedCitiesLength -= Distance.euclideanDistance(fixedCities.get(0),fixedCities.get(fixedCities.size()-1),cities); //Remove start-end distance
					newfixedCitiesLength += Distance.euclideanDistance(fixedCities.size()-1,newCityToAdd,cities);						//Add end to element distance
					newfixedCitiesLength += Distance.euclideanDistance(newCityToAdd,fixedCities.get(0), cities);							//Add start to element distance
				}
				
				//add a new city to the fixed cities
				new_fixedCities.add(newCityToAdd);
				
				//now delete that city from the new toPermute list
				new_toPermute.remove(i-1);
				
				tasks[i] = new TaskTsp(-1, i-1, new_fixedCities, newfixedCitiesLength, new_toPermute, cities);
				
				//Shortcut Computation
				if(shouldTerminateExecution(fixedCitiesLength)) return TERMINATION_VALUE;
			}

			return new ResultTasks<ChunkTsp>(getUID(), tasks);
		}
	}
	
	@Override
	public void updateState(SharedState updatedState) {
		this.currentState = (StateTsp)updatedState;
	}

	@Override
	public SharedState getInitialState() {
		if(initialState == null)
			throw new UnsupportedOperationException("Initializing with with wrong constructor");
		return initialState;
	}
	
	private List<Integer> copyList(List<Integer> list){
		List<Integer> newList = new ArrayList<Integer>();
		for(int i = 0; i < list.size(); i++){
			newList.add(list.get(i));
		}
		return newList;
	}

	@Override
	public String toString() {
		String out = name +"_"+this.getUID()+"("+input[1]+" "+input[3]+" "+input[2]+")";
		return out+" >["+targetUid+"]";
	}


}
