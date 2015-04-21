package tasks;

import java.util.List;

import util.PermutationEnumerator;
import system.ResultTasks;
import system.ResultValue;
import api.Closure;
import api.Result;

public class TaskTsp extends Closure<ChunkTsp> {

	/** Serial ID */
	private static final long serialVersionUID = 5656498160577890305L;

	public TaskTsp(long target, int targetPort, List<Integer> fixedCities, List<Integer> toPermute, double[] cities) {
		super("TSP", target, targetPort, 3);
		this.setInput(0, cities);
		this.setInput(1, fixedCities);
		this.setInput(2, toPermute);
	}

	public TaskTsp(double[] cities){
		super("TSP", target, targetPort, 1);
		this.setInput(0, cities);
		this.setInput(1, new List<Integer>());
		List<Integer> toPermute = new List<Integer>();
		for(int i = 0; i < cities.length; i++){
			toPermute.add(i);
		}
		this.setInput(2, toPermute);
	}

	@Override
	public Result<Integer> execute() {
		long clientStartTime = System.nanoTime();

		List<Integer> fixedCities = (List<Integer>)input[1];
		List<Integer> toPermute = (List<Integer>)input[2];
		double[] cities = (double[])input[0];

		if(toPermute.length <= 10){
			//compute the shortest distance here
			//Pre-compute distances
			double[][] distances = new double[cities.length][cities.length];
		
			for(int src=0; src < cities.length; src++){
				//Compute distance to neighbors (that are not already computed)
				for(int dest=src+1; dest < cities.length; dest++){
					distances[src][dest] = euclideanDistance(src, dest);
				}
			}

			PermutationEnumerator<Integer> generator = new PermutationEnumerator(toPermute);

			double bestLength = Doube.MAX_VALUE;
			List<Integer> bestOrder = null;
			for(List<Integer> perm : generator){
				double currentLength = 0;
				perm.addAll(fixedCities);

				int src = fixedCitie.get(0);
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

		ResultValue<ChunkTsp> result = new Result<ChunkTsp>(new ChunkTsp(bestOrder, bestLength));
		return result;

		}
		else {
			@SuppressWarnings("unchecked")
			Closure<ChunkTsp>[] tasks = new Closure<ChunkTSP>[toPermute.length+1];
			tasks[0] = new TaskComparareTSP(target, targetPort, toPermute.length);
			for(int i = 1; i <= toPermute.length; i++){
				//clone the fixed cities list
				List new_fixedCities = (List) fixedCities.clone();
				//clone the toPermute list
				List new_toPermute = (List) toPermute.clone();
				//add a new city to the fixed cities
				new_fixedCities.add(new_toPermute.get(i-1));
				//now delete that city from the new toPermute list
				new_toPermute.remove(i-1);
				tasks[i] = new TaskTsp(-1, i-1, new_fixedCities, new_toPermute, cities);
			}

			return ResultTasks<ChunkTsp>(tasks);

		}
	}

	private double euclideanDistance(int city1, int city2){
		double x1 = cities[city1][0];
		double y1 = cities[city1][1];
		double x2 = cities[city2][0];
		double y2 = cities[city2][1];
		return Math.sqrt(Math.pow( (x1-x2), 2) + Math.pow( (y1-y2), 2));
	}

}
