package com.hjsj.hrms.businessobject.report.auto_fill_report.exproperation;

/**
 * 生成后缀表达式所需的栈
 *
 */
public class StackOperator {

	private int maxSize;
	private char[] stackArray;
	private int top;
	
	public StackOperator(int s){
		this.maxSize = s;
		stackArray = new char[this.maxSize];
		top = -1;
	}
	
	//向栈中压入数据，个数加一
	public void push(char j){
		stackArray[++top]= j;
	}
	
	//取出栈中数值，序列个数相应减一
	public char pop(){
		return stackArray[top--];
	}
	
	//判断栈内是否为空
	public boolean isEmpty(){
		return top== -1;
	}
	

}
