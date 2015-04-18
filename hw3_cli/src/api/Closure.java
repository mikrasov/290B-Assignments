package api;

import java.io.Serializable;
import java.util.concurrent.Callable;

public abstract class Closure<R> implements Callable<Result<R>>, Serializable{
	
	/** Serial ID*/
	private static final long serialVersionUID = 1894632443394847590L;
	
	protected transient Closure<R> target;
	protected transient int targetPort;
	
	protected Object[] input; 
	protected final String name;
	protected int joinCounter;
	
	public Closure(String name, Closure<R> target, int targetPort, int numInputs){
		this.name = name;
		this.target = target;
		this.targetPort = targetPort;
		this.input = new Object[numInputs];
		this.joinCounter = numInputs;
	}
	
	public void setTarget(Closure<R> target, int targetPort){
		this.target = target;
		this.targetPort = targetPort;
	}
	
	public void assignValueToTarget(Result<R> value){
		target.setInput(targetPort, value.getValue());
	}
	
	public void setInput(int num, R value){
		input[num] = value;
		joinCounter--;
	}
	
	public boolean isReady(){
		return joinCounter == 0;
	}
 	
	@Override
	public String toString() {
		return name + "("+input.length+")";
	}
	
	@Override
	public final Result<R> call(){
		return execute();
	}

	protected abstract Result<R> execute();

}
