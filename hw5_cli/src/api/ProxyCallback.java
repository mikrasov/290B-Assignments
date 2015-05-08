package api;

import java.util.Collection;

public interface ProxyCallback<R> {

	void processResult(Result<R> result);
	
	void doOnError(int proxyId, Collection<Task<R>> leftoverTasks);
}
