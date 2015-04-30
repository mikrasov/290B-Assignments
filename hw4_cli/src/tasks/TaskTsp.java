package tasks;

import java.util.List;
import java.util.ArrayList;

import util.PermutationEnumerator;
import system.ResultTasks;
import system.ResultValue;
import api.Result;
import api.Task;

public class TaskTsp extends TaskClosure<ChunkTsp> {

	/** Serial ID */
	private static final long serialVersionUID = 5656498160577890305L;

	public TaskTsp(long target, int targetPort, List<Integer> fixedCities, List<Integer> toPermute, double[][] cities) {
		super("TSP", 3, target, targetPort);
		this.setInput(0, cities);
		this.setInput(1, fixedCities);
		this.setInput(2, toPermute);
	}

	public TaskTsp(double[][] cities){
		super("TSP-INIT", 3);
		this.setInput(0, cities);
		this.setInput(1, new ArrayList<Integer>());
		List<Integer> toPermute = new ArrayList<Integer>();
		for(int i = 0; i < cities.length; i++){
			toPermute.add(i);
		}
		this.setInput(2, toPermute);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Result<ChunkTsp> execute() {
		double[][] cities = (double[][])input[0];
		List<Integer> fixedCities = (List<Integer>)input[1];
		List<Integer> toPermute = (List<Integer>)input[2];
		

		if(toPermute.size() <= 10){
			//compute the shortest distance here
			//Pre-compute distances
			double[][] distances = new double[cities.length][cities.length];
		
			for(int src=0; src < cities.length; src++){
				//Compute distance to neighbors (that are not already computed)
				for(int dest=src+1; dest < cities.length; dest++){
					distances[src][dest] = euclideanDistance(src, dest, cities);
				}
			}

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
			
				//if current permutation is better then what is on record
				if(currentLength <= bestLength){
					bestOrder = perm;
					bestLength = currentLength;
				}
			}

			return new ResultValue<ChunkTsp>(this, new ChunkTsp(bestOrder, bestLength));
		}
		else {
			Task<ChunkTsp>[] tasks = new Task[toPermute.size()+1];
			tasks[0] = new TaskCompareTsp(targetUid, targetPort, toPermute.size());
			for(int i = 1; i <= toPermute.size(); i++){
				//clone the fixed cities list
				List<Integer> new_fixedCities = copyList(fixedCities);
				//clone the toPermute list
				List<Integer> new_toPermute = copyList(toPermute);
				//add a new city to the fixed cities
				new_fixedCities.add(new_toPermute.get(i-1));
				//now delete that city from the new toPermute list
				new_toPermute.remove(i-1);
				tasks[i] = new TaskTsp(-1, i-1, new_fixedCities, new_toPermute, cities);
			}

			return new ResultTasks<ChunkTsp>(this, tasks);
		}
	}

	private double euclideanDistance(int city1, int city2, double[][] cities){
		double x1 = cities[city1][0];
		double y1 = cities[city1][1];
		double x2 = cities[city2][0];
		double y2 = cities[city2][1];
		return Math.sqrt(Math.pow( (x1-x2), 2) + Math.pow( (y1-y2), 2));
	}

	private List<Integer> copyList(List<Integer> list){
		List<Integer> newList = new ArrayList<Integer>();
		for(int i = 0; i < list.size(); i++){
			newList.add(list.get(i));
		}
		return newList;
	}

}
