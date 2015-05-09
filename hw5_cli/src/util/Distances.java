package util;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Distances implements Serializable{

	private static final long serialVersionUID = 3273068850640944591L;

	private double[][] distances;
	
	public Distances(double[][] cities){
		distances = new double[cities.length][cities.length];
		
		for(int src=0; src < cities.length; src++){
			//Compute distance to neighbors (that are not already computed)
			for(int dest=src+1; dest < cities.length; dest++){
				
				double x1 = cities[src][0];
				double y1 = cities[src][1];
				double x2 = cities[dest][0];
				double y2 = cities[dest][1];
				distances[src][dest] =  Math.sqrt(Math.pow( (x1-x2), 2) + Math.pow( (y1-y2), 2));
				
			}
		}
	}
	
	public double between(int src, int dest ){
		if(src < dest)
			return distances[src][dest];
		else
			return distances[dest][src];
	}
	

	public double greedyDistance(double[][] cities){
		double totalDistance = 0;
		
		List<Integer> unvisited = new LinkedList<Integer>();
		for(int i=0; i< cities.length; i++) unvisited.add(i); //populate
		
		int origin = unvisited.remove(0);
		int src = origin;
		while(!unvisited.isEmpty()){
			double bestDistance = Double.MAX_VALUE;
			int bestDestIndex = -1;
			for(int i=0; i<unvisited.size(); i++){
				double currentDistance = between(src, unvisited.get(i));
				
				if(currentDistance < bestDistance){
					bestDistance = currentDistance;
					bestDestIndex = i;
				}
			}
			src = unvisited.remove(bestDestIndex);
			totalDistance += bestDistance;
		}
		return totalDistance + between(src, origin);
	}
}
