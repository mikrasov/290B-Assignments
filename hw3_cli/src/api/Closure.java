package api;

import java.util.ArrayList;
import java.util.List;

public class Closure<I> {

	private int joinCounter;
	private I[] inputs; 
	private Task<I> task;
	private Closure<I> target;
	
	@SuppressWarnings("unchecked")
	public Closure( Task<I> task, int numImputs, Closure<I> target){
		this.joinCounter = numImputs;
		this.inputs = (I[]) new Object[numImputs];
		this.task = task;
		this.target = target;
	}
	
	public boolean isReady(){
		return joinCounter == 0;
	}
	
	public void setInput(int num, I input){
		inputs[num] = input;
		joinCounter--;
	}
	
	public Task<I> getTask(){
		return task;
	}
	
	
	private I getInput(int num){
		return inputs[num];
	}
	/*
	 CLOSURE:
	 
	 code
	 join counter (# inputs waiting for)
	 input 1
	 input 2
	 input 3
	 
	 
	 Pointer Where to send output
	  
	 
	 */
}
