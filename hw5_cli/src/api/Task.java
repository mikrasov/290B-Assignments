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

	Result<R> call(SharedState currentState, UpdateStateCallback callback);
	
	void updateState(SharedState updatedState);
	
	SharedState getInitialState();
	
	boolean isCachable();
	
	boolean isShortRunning();
}