package client;

import api.Space;

public class JobMandelbrot implements Job<Integer[][]> {

	private final double LOWER_LEFT_X, double LOWER_LEFT_Y, double EDGE_LENGTH;
	private final int N_PIXELS, ITERATION_LIMIT;
	private Integer[][] count;

	private long numTotalTasksReceived = 0;
	private long numTotalTasksSent = 0;


	public JobMandelbrot(double LOWER_LEFT_X, double LOWER_LEFT_Y,
		double EDGE_LENGTH, int N_PIXELS, int ITERATION_LIMIT) {
		
		this.LOWER_LEFT_X = LOWER_LEFT_X;
		this.LOWER_LEFT_Y = LOWER_LEFT_Y;
		this.EDGE_LENGTH = EDGE_LENGTH;
		this.N_PIXELS = N_PIXELS;
		this.ITERATION_LIMIT = ITERATION_LIMIT;
		count = new Integer[N_PIXELS][N_PIXELS];
	}

	@Override
	public void generateTasks(Space space) {
		
		double lowerX = this.LOWER_LEFT_X;
		double lowerY = this.LOWER_LEFT_Y;

		double shift = EDGE_LENGTH/N_PIXELS;
		double saveCornerX = lowerX;

		//Now make each row a task to send to space
		for(int i = 0; i < count.length; i++){
			sendToSpace(count[i], i, lowerX, lowerY, shift);
			numTotalTasksSent++;
			lowerY += shift;
			lowerX = saveCornerX;
		}
	}

	private void sendToSpace(int[] countsToCompute, int index, double lowerX, double lowerY, double shift) {
		TaskMandelbrot task = new TaskMandelbrot(countsToCompute, index, lowerX, lowerY, shift);
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
		while(!isJobComplete) {
			try{
				Result<ChunkMandelbrot> result = space.take();
				numTotalTasksReceived++;
				int indexOfResult = result.getTaskReturnValue().getRowID();
				int[] rowResult = result.getTaskReturnValue().getCounts();
				count[indexOfResult] = rowResult;
			}catch(RemoteException e){
				System.err.println("RMI Error when sending task! Rerying in "+RETRY_TIMER+" ...");
				try {Thread.sleep(RETRY_TIMER);} catch (InterruptedException e1) {}
				continue;
			}
			// Wait before trying to take next one
			try {Thread.sleep(TAKE_TIMER);} catch (InterruptedException e1) {}
		}

		return count;
		
	}

	@Override
	public boolean isJobComplete() {
		return numTotalTasksReceived >= numTotalTasksSent;
	}



}
