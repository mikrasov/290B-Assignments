package api;

import java.io.Serializable;

public interface Task<R> extends Serializable {

	boolean isReady();
	
	void setTarget(long targetUid, int targetPort);

	void setInput(int num, Object value);

	void setUid(long uid);

	long getUID();

	long getTargetUid();

	int getTargetPort();

	Result<R> call(SharedState currentState);
	
	void updateState(SharedState updatedState);
	
	boolean isCachable();
	
	boolean isShortRunning();
}