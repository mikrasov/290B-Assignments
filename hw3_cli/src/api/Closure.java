package api;

import java.util.concurrent.Callable;

public abstract class Closure<R> implements Callable<Result<R>>{
	
	protected transient Closure<R> target;
	protected transient int targetPort;
	
	protected R[] input; 
	protected final String name;
	protected int joinCounter;
	
	@SuppressWarnings("unchecked")
	public Closure(String name, Closure<R> target, int targetPort, int numInputs){
		this.name = name;
		this.target = target;
		this.targetPort = targetPort;
		this.input = (R[]) new Object[numInputs];
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
