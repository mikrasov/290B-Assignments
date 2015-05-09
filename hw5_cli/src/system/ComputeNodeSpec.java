package system;


import api.Capabilities;

public class ComputeNodeSpec implements Capabilities{

	private static final long serialVersionUID = -4800920412924232081L;

	private static final int BUFFER_DEFAULT_SIZE = 5;
	
	private final int numThreads,prefetchBufferSize;
	
	public ComputeNodeSpec(int desiredNumThreads, int desiredPrefetchBufferSize) {
		numThreads = desiredNumThreads>0?desiredNumThreads:Runtime.getRuntime().availableProcessors();
		prefetchBufferSize = desiredPrefetchBufferSize>0?desiredPrefetchBufferSize:BUFFER_DEFAULT_SIZE;
	}

	@Override
	public int getNumberOfThreads() {
		return numThreads;
	}

	@Override
	public int getBufferSize() {
		return prefetchBufferSize;
	}

}
