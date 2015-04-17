package api;

import java.util.concurrent.Callable;

public abstract class Closure<R> implements Callable<Result<R>>{
	
	protected final R[] input; 
	protected final Closure<R> target;
	protected final int targetPort;
	
	private final String name;
	private int joinCounter;
	
	@SuppressWarnings("unchecked")
	public Closure(String name, Closure<R> target, int targetPort, int numInputs){
		this.name = name;
		this.target = target;
		this.targetPort = targetPort;
		this.input = (R[]) new Object[numInputs];
		this.joinCounter = numInputs;
	}
	
	public Closure(String name, int numInputs){
		this(name,null,-1,numInputs);
	}
	
	public void setInput(int num, R value){
		input[num] = value;
		joinCounter--;
	}
	
	public boolean isReady(){
		return joinCounter == 0;
	}
	
	public boolean isTerminal(){
		return target == null;
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
