package tsp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import system.ResultTasks;
import system.ResultValue;
import system.TaskClosure;
import util.Distance;
import api.Result;
import api.SharedState;
import api.Task;
import api.UpdateStateCallback;

public class TaskTsp extends TaskClosure<ChunkTsp> {

	private static final long serialVersionUID = -2567928535294012341L;
	
	private static final int BASIC_TSP_PROBLEM_SIZE = 13;
	private static final int NUMBER_OF_INPUTS = 4;
	private static final boolean ENABLE_BOUNDING = true;

	private StateTsp currentState;
	
	private TaskTsp(long target, int targetPort, List<Integer> fixedCities, double fixedCitiesLength, List<Integer> toPermute, double[][] cities) {
		super("TSP", fixedCities.size(), NUMBER_OF_INPUTS, toPermute.size()>BASIC_TSP_PROBLEM_SIZE, target, targetPort);
		this.setInput(0, cities);
		this.setInput(1, fixedCities);
		this.setInput(2, toPermute);
		this.setInput(3, fixedCitiesLength);
	}

	public TaskTsp(double[][] cities){
		super("TSP-INIT", DEFAULT_PRIORITY, NUMBER_OF_INPUTS, SHORT_RUNNING);
		
		List<Integer> toPermute = new ArrayList<Integer>();
		for(int i = 0; i < cities.length; i++){
			toPermute.add(i);
		}
		
		List<Integer> fixedList =new ArrayList<Integer>();
		fixedList.add( toPermute.remove(0));
		
		this.setInput(0, cities);
		this.setInput(1, fixedList);
		this.setInput(2, toPermute);
		this.setInput(3, 0.0);
	}

	private boolean shouldTerminateExecution(double bestPartialLength){
		boolean shortut = ENABLE_BOUNDING && currentState.isBetterThan(bestPartialLength);
		
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
		
		//Shortcut Computation
		if(shouldTerminateExecution(fixedCitiesLength)) return new ResultValue<ChunkTsp>(getUID(), new ChunkTsp(fixedCities, Double.MAX_VALUE));

		if(toPermute.size() <= BASIC_TSP_PROBLEM_SIZE){	
			ChunkTsp best = solve(fixedCities,fixedCitiesLength, toPermute,cities, callback);
			return new ResultValue<ChunkTsp>(getUID(), best);
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
				if(fixedCities.size() > 1){
					newfixedCitiesLength -= Distance.euclideanDistance(fixedCities.get(0),fixedCities.get(fixedCities.size()-1),cities); 	//Remove start-end distance
				}
				if(fixedCities.size() > 0){
					newfixedCitiesLength += Distance.euclideanDistance(fixedCities.get(fixedCities.size()-1),newCityToAdd,cities);			//Add end to element distance
					newfixedCitiesLength += Distance.euclideanDistance(newCityToAdd,fixedCities.get(0), cities);							//Add start to element distance
				}
				
				//add a new city to the fixed cities
				new_fixedCities.add(newCityToAdd);
				
				//now delete that city from the new toPermute list
				new_toPermute.remove(i-1);
				
				tasks[i] = new TaskTsp(-1, i-1, new_fixedCities, newfixedCitiesLength, new_toPermute, cities);
				
			}

			return new ResultTasks<ChunkTsp>(getUID(), tasks);
		}
	}
	
	@Override
	public void updateState(SharedState updatedState) {
		this.currentState = (StateTsp)updatedState;
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
	
	private ChunkTsp solve(final List<Integer> fixedCities, final double fixedCitiesLength, final List<Integer> toPermute, final double[][] cities, UpdateStateCallback callback){
	
		if(toPermute.size() == 0){
			
			return new ChunkTsp(fixedCities, fixedCitiesLength);
		}
		else{
			ChunkTsp best = new ChunkTsp(fixedCities, Double.MAX_VALUE);
			
			for(int i=0; i<toPermute.size(); i++){
				
				//Shortcut Computation
				if(shouldTerminateExecution(fixedCitiesLength)) return new ChunkTsp(fixedCities, Double.MAX_VALUE);
				
				int cityToAdd = toPermute.get(i);
				List<Integer> expandedFixedCities = new LinkedList<Integer>(fixedCities);
				expandedFixedCities.add(cityToAdd);
				
				List<Integer> expandedToPermute = new LinkedList<Integer>(toPermute);
				expandedToPermute.remove(i);
				
				double expandedfixedCitiesLength = fixedCitiesLength;
				if(fixedCities.size() > 1){
					expandedfixedCitiesLength -= Distance.euclideanDistance(fixedCities.get(0),fixedCities.get(fixedCities.size()-1),cities);	//Remove start-end distance
				}
				if(fixedCities.size() > 0){
					expandedfixedCitiesLength += Distance.euclideanDistance(cityToAdd, fixedCities.get(fixedCities.size()-1),cities);			//Add end to element distance
					expandedfixedCitiesLength += Distance.euclideanDistance(cityToAdd, fixedCities.get(0), cities);								//Add start to element distance
				}
				
				ChunkTsp current = solve(expandedFixedCities, expandedfixedCitiesLength, expandedToPermute, cities, callback);
				
				if(current.getBestLength() < best.getBestLength()){
					best = current;
					callback.updateState(new StateTsp(current.getBestLength()) );
				}	
			}
			return best;
		}
		
	}
}
