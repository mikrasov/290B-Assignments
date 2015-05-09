package util;

import java.util.LinkedList;
import java.util.List;

public class Distance {

	public static double euclideanDistance(int city1, int city2, double[][] cities){
		double x1 = cities[city1][0];
		double y1 = cities[city1][1];
		double x2 = cities[city2][0];
		double y2 = cities[city2][1];
		return Math.sqrt(Math.pow( (x1-x2), 2) + Math.pow( (y1-y2), 2));
	}

	public static double[][] allDistances(double[][] cities){
	
		double[][] distances = new double[cities.length][cities.length];
		
		for(int src=0; src < cities.length; src++){
			//Compute distance to neighbors (that are not already computed)
			for(int dest=src+1; dest < cities.length; dest++){
				distances[src][dest] = Distance.euclideanDistance(src, dest, cities);
			}
		}
		return distances;
	}
	
	public static double greedyDistance(double[][] cities){
		double totalDistance = 0;
		
		List<Integer> unvisited = new LinkedList<Integer>();
		for(int i=0; i< cities.length; i++) unvisited.add(i); //populate
		
		int origin = unvisited.remove(0);
		int src = origin;
		while(!unvisited.isEmpty()){
			double bestDistance = Double.MAX_VALUE;
			int bestDestIndex = -1;
			for(int i=0; i<unvisited.size(); i++){
				double currentDistance = euclideanDistance(src, unvisited.get(i), cities);
				
				if(currentDistance < bestDistance){
					bestDistance = currentDistance;
					bestDestIndex = i;
				}
			}
			src = unvisited.remove(bestDestIndex);
			totalDistance += bestDistance;
		}
		return totalDistance + euclideanDistance(src, origin, cities);
	}
}
