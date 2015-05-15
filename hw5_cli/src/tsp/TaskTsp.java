package tsp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import system.ResultTasks;
import system.ResultValue;
import system.TaskClosure;
import api.Result;
import api.SharedState;
import api.Task;
import api.UpdateStateCallback;

public class TaskTsp extends TaskClosure<ChunkTsp> {

	private static final long serialVersionUID = -2567928535294012341L;
	
	private static final int BASIC_TSP_PROBLEM_SIZE = 11;
	
	private StateTsp currentState;
	
	private final List<Integer> fixedCities;
	private final List<Integer> toPermute;
	private final double fixedCitiesLength;
	
	private TaskTsp(long target, int targetPort, List<Integer> fixedCities, double fixedCitiesLength, List<Integer> toPermute) {
		super("TSP", fixedCities.size(), NO_INPUTS, toPermute.size()>BASIC_TSP_PROBLEM_SIZE, target, targetPort);
		
		this.fixedCities = fixedCities;
		this.toPermute = toPermute;
		this.fixedCitiesLength = fixedCitiesLength;
	}

	public TaskTsp(double[][] cities){
		super("TSP-INIT", DEFAULT_PRIORITY, NO_INPUTS, cities.length>BASIC_TSP_PROBLEM_SIZE);
		
		toPermute = new ArrayList<Integer>();
		for(int i = 0; i < cities.length; i++){
			toPermute.add(i);
		}
		
		fixedCities = new ArrayList<Integer>();
		fixedCities.add( toPermute.remove(0));
		
		fixedCitiesLength = 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Result<ChunkTsp> execute(SharedState initialState, UpdateStateCallback callback) {	
		currentState = (StateTsp)initialState;
		
		//Shortcut Computation
		if(currentState.isBetterThan(fixedCitiesLength)) return new ResultValue<ChunkTsp>(getUID(), new ChunkTsp(fixedCities, Double.MAX_VALUE), this.getMaxCriticalLength());

		if(toPermute.size() <= BASIC_TSP_PROBLEM_SIZE){	
			ChunkTsp best = solve(fixedCities,fixedCitiesLength, toPermute, callback);
			return new ResultValue<ChunkTsp>(getUID(), best, this.getMaxCriticalLength());
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
					newfixedCitiesLength -= currentState.distanceBetween(fixedCities.get(0),fixedCities.get(fixedCities.size()-1)); //Remove start-end distance
				}
				if(fixedCities.size() > 0){
					newfixedCitiesLength += currentState.distanceBetween(fixedCities.get(fixedCities.size()-1),newCityToAdd);		//Add end to element distance
					newfixedCitiesLength += currentState.distanceBetween(newCityToAdd,fixedCities.get(0));							//Add start to element distance
				}
				
				//add a new city to the fixed cities
				new_fixedCities.add(newCityToAdd);
				
				//now delete that city from the new toPermute list
				new_toPermute.remove(i-1);
				
				tasks[i] = new TaskTsp(-1, i-1, new_fixedCities, newfixedCitiesLength, new_toPermute);
			}

			return new ResultTasks<ChunkTsp>(getUID(), tasks, this.getMaxCriticalLength());
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
		String out = name +"_"+this.getUID()+"("+fixedCities+":"+fixedCitiesLength+" | "+toPermute+")";
		return out+" >["+targetUid+"]";
	}
	
	private ChunkTsp solve(final List<Integer> fixedCities, final double fixedCitiesLength, final List<Integer> toPermute,  UpdateStateCallback callback){
	
		if(toPermute.size() == 0){
			
			return new ChunkTsp(fixedCities, fixedCitiesLength);
		}
		else{
			ChunkTsp best = new ChunkTsp(fixedCities, Double.MAX_VALUE);
			
			for(int i=0; i<toPermute.size(); i++){
				
				//Shortcut Computation
				if(currentState.isBetterThan(fixedCitiesLength)) return new ChunkTsp(fixedCities, Double.MAX_VALUE);
				
				int cityToAdd = toPermute.get(i);
				List<Integer> expandedFixedCities = new LinkedList<Integer>(fixedCities);
				expandedFixedCities.add(cityToAdd);
				
				List<Integer> expandedToPermute = new LinkedList<Integer>(toPermute);
				expandedToPermute.remove(i);
				
				double expandedfixedCitiesLength = fixedCitiesLength;
				if(fixedCities.size() > 1){
					expandedfixedCitiesLength -= currentState.distanceBetween(fixedCities.get(0),fixedCities.get(fixedCities.size()-1));//Remove start-end distance
				}
				if(fixedCities.size() > 0){
					expandedfixedCitiesLength += currentState.distanceBetween(cityToAdd, fixedCities.get(fixedCities.size()-1));		//Add end to element distance
					expandedfixedCitiesLength += currentState.distanceBetween(cityToAdd, fixedCities.get(0));							//Add start to element distance
				}
				
				ChunkTsp current = solve(expandedFixedCities, expandedfixedCitiesLength, expandedToPermute, callback);
				
				if(current.getBestLength() < best.getBestLength()){
					best = current;
					callback.updateState(new StateTsp(current.getBestLength()) );
				}	
			}
			return best;
		}
	}
}
