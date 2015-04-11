package client;

import api.Space;

public class JobMandelbrot implements Job<Integer[][]> {

	private final double LOWER_LEFT_X, double LOWER_LEFT_Y, double EDGE_LENGTH;
	private final int N_PIXELS, ITERATION_LIMIT;

	public JobMandelbrot(double LOWER_LEFT_X, double LOWER_LEFT_Y,
		double EDGE_LENGTH, int N_PIXELS, int ITERATION_LIMIT) {
		
		this.LOWER_LEFT_X = LOWER_LEFT_X;
		this.LOWER_LEFT_Y = LOWER_LEFT_Y;
		this.EDGE_LENGTH = EDGE_LENGTH;
		this.N_PIXELS = N_PIXELS;
		this.ITERATION_LIMIT = ITERATION_LIMIT;
	}

	@Override
	public void generateTasks(Space space) {
		// TODO Auto-generated method stub
	}

	@Override
	public Integer[][]  collectResults(Space space) {
		// TODO Auto-generated method stub
		return null;
		
	}

	@Override
	public boolean isJobComplete() {
		// TODO Auto-generated method stub
		return false;
	}



}
