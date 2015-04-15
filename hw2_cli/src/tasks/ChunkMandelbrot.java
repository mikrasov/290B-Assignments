package tasks;

import java.io.Serializable;

/**
 * Result of a task for Mandelbrot computation.
 * Contains a row count for each row id.
 * 
 * @author Michael Nekrasov
 * @author Roman Kazarin
 *
 */
public class ChunkMandelbrot  implements Serializable {

	/** Serial Version UID*/
	private static final long serialVersionUID = 7288785475371729862L;
	
	private final int rowId;
	private final Integer[] rowCounts;
	
	/**
	 * Construct new chunck
	 * @param rowId of result
	 * @param rowCounts resulting count
	 */
	public ChunkMandelbrot(int rowId, Integer[] rowCounts) {
		this.rowId = rowId;
		this.rowCounts = rowCounts;
	}

	/**
	 * Return ID of current chunk result
	 * @return row id
	 */
	public int getRowID() {
		return rowId;
	}

	/**
	 * Get count array of row
	 * @return count array
	 */
	public Integer[] getCounts() {
		return rowCounts;
	}

}
