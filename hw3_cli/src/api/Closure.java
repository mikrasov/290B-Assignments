package api;

import java.io.Serializable;
import java.util.concurrent.Callable;

public abstract class Closure<R> implements Callable<Result<R>>, Serializable{
	
	/** Serial ID*/
	private static final long serialVersionUID = 1894632443394847590L;
	
	private long uid;
	
	protected long targetUid;
	protected int targetPort;

	protected final Object[] input; 
	protected final String name;
	protected int joinCounter;
	
	public Closure(String name, long targetUid, int targetPort, int numInputs){
		this.name = name;
		this.targetUid = targetUid;
		this.targetPort = targetPort;
		this.input = new Object[numInputs];
		this.joinCounter = numInputs;
	}
	
	public void setTarget(long targetUid, int targetPort){
		this.targetUid = targetUid;
		this.targetPort = targetPort;
	}
	
	public void setInput(final int num, final R value){
		input[num] = value;
		joinCounter--;
	}
	
	public void setUid(long uid){
		this.uid = uid;
	}
	
	public boolean isReady(){
		return joinCounter == 0;
	}
	
	@Override
	public final Result<R> call(){
		return execute();
	}
 	
	
	public long getUID(){
		return uid;
	}
	
	public long getTargetUid(){
		return targetUid;
	}
	
	public int getTargetPort(){
		return targetPort;
	}
	
	@Override
	public String toString() {
		String out = name +"_"+hashCode()+"(";
		for(Object in: input){
			out+=in+",";
		}
		out = out.substring(0,out.length()-1)+")";
		return out+" >["+targetUid+"]";
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object arg0) {
		return this.uid == ((Closure) arg0).uid;
	}
	
	@Override
	public int hashCode() {
		return (int) uid;
	}
	
	protected abstract Result<R> execute();

}
