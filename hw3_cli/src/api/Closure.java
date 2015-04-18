package api;

import java.io.Serializable;
import java.util.concurrent.Callable;

public abstract class Closure<R> implements Callable<Result<R>>, Serializable{
	
	/** Serial ID*/
	private static final long serialVersionUID = 1894632443394847590L;
	
	protected Closure<R> target;
	protected int targetPort;
	
	protected final Object[] input; 
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
		System.out.println("Assigned: " +target);
	}
	
	public void setInput(final int num, final R value){
		input[num] = value;
		joinCounter--;
	}
	
	public boolean isReady(){
		return joinCounter == 0;
	}
	
	@Override
	public final Result<R> call(){
		return execute();
	}
 	
	
	public String toVerboseString(){
		return this+" -> "+target;
	}
	
	@Override
	public String toString() {
		String out = name +"_"+hashCode()+" (";
		for(Object in: input){
			out+=in+",";
		}
		out = out.substring(0,out.length()-1)+")";
		return out;
	}
	
	protected abstract Result<R> execute();

}
