package api;

import java.io.Serializable;
import java.util.concurrent.Callable;

public interface Task<R> extends Callable<Result<R>>, Serializable {

	boolean isReady();
	
	void setTarget(long targetUid, int targetPort);

	void setInput(int num, Object value);

	void setUid(long uid);

	long getUID();

	long getTargetUid();

	int getTargetPort();

	Result<R> call();
	
	boolean isCachable();
	
	boolean isShortRunning();
}