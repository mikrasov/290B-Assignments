package tasks;

public class ChunkMandelbrot {

	private final int row_id;
	private final Integer[] row_counts;
	
	public ChunkMandelbrot(int row_id, Integer[] row_counts) {
		this.row_id = row_id;
		this.row_counts = row_counts;
	}

	public int getRowID() {
		return row_id;
	}

	public Integer[] getCounts() {
		return row_counts;
	}

}
