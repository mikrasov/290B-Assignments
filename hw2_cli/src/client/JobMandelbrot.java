package client;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import tasks.ChunkMandelbrot;
import tasks.TaskMandelbrot;
import tasks.TaskTSP;
import api.Result;
import api.Space;

public class JobMandelbrot implements Job<Integer[][]> {

	public static final int CHUNK_SIZE = 1000000;
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
		//System.out.println("Lower X: " + LOWER_LEFT_X);
		//System.out.println("Lower Y: " + LOWER_LEFT_Y);
		//System.out.println("Iteration Limit: " + ITERATION_LIMIT);
		//System.out.println("Edge Length: " + EDGE_LENGTH);
		//System.out.println("N Pixels: " + N_PIXELS);
	}

	@Override
	public void generateTasks(Space space) throws RemoteException {
		double lowerX = this.LOWER_LEFT_X;
		double lowerY = this.LOWER_LEFT_Y;

		double shift = EDGE_LENGTH/N_PIXELS;
		double saveCornerX = lowerX;

		//Now make each row a task to send to space
		for(int i = 0; i < count.length; i++){
			sendToSpace(space, count[i], i, lowerX, lowerY, shift);
		}
	}

	private void sendToSpace(Space space, Integer[] countsToCompute, int index, double lowerX, double lowerY, double shift) throws RemoteException {
		TaskMandelbrot task = new TaskMandelbrot(countsToCompute, index, ITERATION_LIMIT, lowerX, lowerY, shift);
		System.out.println("--> Sending Task: "+task);
		space.put(task);
		numBlocksSent++;
	}

	@Override
	public Integer[][]  collectResults(Space space) throws RemoteException {
		while(!isJobComplete()) {
			Result<ChunkMandelbrot> result = space.take();
			numBlocksReceived++;
			
			System.out.println("<-- Recieved: "+numBlocksReceived+" of "+numBlocksSent);
			Logger.getLogger( Client.class.getCanonicalName() )
            .log(Level.INFO, "Task time: {0} ms.", ( result.getTaskRunTime() / 1000000 ));

			int indexOfResult = result.getTaskReturnValue().getRowID();
			Integer[] rowResult = result.getTaskReturnValue().getCounts();
			count[indexOfResult] = rowResult;
		
			// Wait before trying to take next one
			try {Thread.sleep(TAKE_TIMER);} catch (InterruptedException e1) {}
		}
		
		System.out.println("-- DONE --");
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
