package tasks;

import java.util.List;

import api.Task;

public class TaskTSP implements Task<ChunkTSP> {

	private static int ID_GENERATOR = 0;
	
	private final int id;
	private final double[][] cities;
	private List<List<Integer>> permutations;
	
	public TaskTSP(double[][] cities, List<List<Integer>> permutations) {
		this.permutations = permutations;
		this.cities = cities;
		
		id = ID_GENERATOR++;
	}

	@Override
	public ChunkTSP call() {
		
		double bestLength = Double.MAX_VALUE;
		List<Integer> bestOrder = null;
		for(List<Integer> perm : permutations){
	
			double currentLength = 0;
			
			//Sum Lengths
			Integer src = null;
			for(Integer dest: perm){
				if(src != null) //skip first itteration
					currentLength += distance(src,dest);
				
				src = dest;
			}
			
			//if current permutation is better then what is on record
			if(currentLength <= bestLength){
				bestOrder = perm;
				bestLength = currentLength;
			}
		}
		return new ChunkTSP(bestOrder, bestLength);
	}


	/**
	 * Calculates the euclidian distance between two cities
	 * @param x1 x position of city 1
	 * @param y1 y position of city 1
	 * @param x2 x position of city 2
	 * @param y2 y position of city 2
	 * @return the euclidean distance
	 */
	private static double distance(double x1, double y1, double x2, double y2){
		return Math.sqrt(Math.pow( (x1-x2), 2) + Math.pow( (y1-y2), 2));
	}
	
	private double distance(int city1, int city2){
		return distance(cities[city1][0],cities[city1][1],cities[city2][0],cities[city2][1]);
	}
	
	@Override
	public boolean equals(Object arg0) {
		return ((TaskTSP)arg0).id == this.id;
	}
}
