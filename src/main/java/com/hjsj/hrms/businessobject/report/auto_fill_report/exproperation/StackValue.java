package com.hjsj.hrms.businessobject.report.auto_fill_report.exproperation;


/**
 * 
 * 字符串表达式运算栈
 * @author 
 *
 */
public class StackValue {

	private int maxSize;
	private double [] stackArray;
	private int top;
	
	public StackValue(int size){
		maxSize = size;
		stackArray = new double [maxSize];
		top = -1;
	}
	
	public void push(double j){
		stackArray [++top]=j;
	}
	
	public double pop(){
		return stackArray[top--];
	}
	
	public boolean isEmpty(){
		return top ==  -1;
	}

	public boolean isFull(){
		return top == maxSize -1;
	}
	
}
