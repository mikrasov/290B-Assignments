package tasks;

import java.io.Serializable;

public class ChunkMandelbrot  implements Serializable {

	/** Serial Version UID*/
	private static final long serialVersionUID = 7288785475371729862L;
	
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
