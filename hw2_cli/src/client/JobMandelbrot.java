package client;

import java.rmi.RemoteException;

import tasks.ChunkMandelbrot;
import tasks.TaskMandelbrot;
import api.Result;
import api.Space;

public class JobMandelbrot implements Job<Integer[][]> {

	public static final int CHUNK_SIZE = 1000000;
	public static final int RETRY_TIMER = 200;
	public static final int TAKE_TIMER = 50;
	
	private final double LOWER_LEFT_X, LOWER_LEFT_Y, EDGE_LENGTH;


	private final int N_PIXELS, ITERATION_LIMIT;
	private Integer[][] count;

	private long numBlocksReceived = 0;
	private long numBlocksSent = 0;


	public JobMandelbrot(double LOWER_LEFT_X, double LOWER_LEFT_Y,
		double EDGE_LENGTH, int N_PIXELS, int ITERATION_LIMIT) {
		
		this.LOWER_LEFT_X = LOWER_LEFT_X;
		this.LOWER_LEFT_Y = LOWER_LEFT_Y;
		this.EDGE_LENGTH = EDGE_LENGTH;
		this.N_PIXELS = N_PIXELS;
		this.ITERATION_LIMIT = ITERATION_LIMIT;
		count = new Integer[N_PIXELS][N_PIXELS];
		System.out.println("LOWER_LEFT_X = " + this.LOWER_LEFT_X);
		System.out.println("LOWER_LEFT_Y = " + this.LOWER_LEFT_Y);
		System.out.println("EDGE LENGTH = " + this.EDGE_LENGTH);
		System.out.println("N_PIXELS = " + this.N_PIXELS);
		System.out.println("ITERATION_LIMIT = " + this.ITERATION_LIMIT);
	}

	@Override
	public void generateTasks(Space space) {
		double lowerX = this.LOWER_LEFT_X;
		double lowerY = this.LOWER_LEFT_Y;

		double shift = EDGE_LENGTH/N_PIXELS;
		double saveCornerX = lowerX;

		//Now make each row a task to send to space
		for(int i = 0; i < count.length; i++){
			sendToSpace(space, count[i], i, lowerX, lowerY, shift);
			numBlocksSent++;
			lowerY += shift;
		}
	}

	private void sendToSpace(Space space, Integer[] countsToCompute, int index, double lowerX, double lowerY, double shift) {
		TaskMandelbrot task = new TaskMandelbrot(countsToCompute, index, ITERATION_LIMIT, lowerX, lowerY, shift);
		boolean success = false;

		while(!success) try {
			space.put(task);
		} catch (Exception e) {
			System.err.println("RMI Error when sending task! Rerying in "+RETRY_TIMER+" ...");
			try {Thread.sleep(RETRY_TIMER);} catch (InterruptedException e1) {}
		} finally {
			success = true;
		}
	}

	@Override
	public Integer[][]  collectResults(Space space) {
		while(!isJobComplete()) {
			try{
				Result<ChunkMandelbrot> result = space.take();
				numBlocksReceived++;
				
				System.out.println("<-- Recieved: "+numBlocksReceived+" of "+numBlocksSent);
				
				int indexOfResult = result.getTaskReturnValue().getRowID();
				Integer[] rowResult = result.getTaskReturnValue().getCounts();
				count[indexOfResult] = rowResult;
			}catch(RemoteException e){
				System.err.println("RMI Error when sending task! Rerying in "+RETRY_TIMER+" ...");
				try {Thread.sleep(RETRY_TIMER);} catch (InterruptedException e1) {}
				continue;
			}
			// Wait before trying to take next one
			try {Thread.sleep(TAKE_TIMER);} catch (InterruptedException e1) {}
		}

		System.out.println("Mandelbrot result collection complete.");

		return count;
	}

	@Override
	public boolean isJobComplete() {
		return numBlocksReceived >= numBlocksSent;
	}

	public double getLOWER_LEFT_X() {
		return LOWER_LEFT_X;
	}

	public double getLOWER_LEFT_Y() {
		return LOWER_LEFT_Y;
	}

	public double getEDGE_LENGTH() {
		return EDGE_LENGTH;
	}

	public int getN_PIXELS() {
		return N_PIXELS;
	}

	public int getITERATION_LIMIT() {
		return ITERATION_LIMIT;
	}
}
