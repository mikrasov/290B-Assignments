package tasks;

import java.util.List;

public class ChunkMandelbrot {

	private final int row_id;
	private final int[] row_counts;
	
	public ChunkMandelbrot(int row_id, int[] row_counts) {
		this.row_id = row_id;
		this.row_counts = row_counts;
	}

	public int getRowID() {
		return row_id;
	}

	public int[] getCounts() {
		return row_counts;
	}

}
