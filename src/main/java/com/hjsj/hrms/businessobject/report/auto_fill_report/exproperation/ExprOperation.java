

package com.hjsj.hrms.businessobject.report.auto_fill_report.exproperation;

/**
 * 字符串表达式运算
 * @author 
 *
 */
public class ExprOperation {
	
	private StackOperator stackOperator; //
	private StackValue stackValue;
	private String expr;			//字符串表达式
	private StringBuffer tempexpr = new StringBuffer(); //后缀表达式
	
	/**
	 * 
	 * @param in 合法的字符串表达式
	 */
	public ExprOperation(){

	}
	
	public double getExprValue(String expr){
		this.expr = expr;
		int stackSize = expr.length();
		stackOperator = new StackOperator(stackSize);
		this.doTrans();
		return this.doParse();
	}
	
	/**
	 * 	生成后缀表达式
	 * @return
	 */
	public String doTrans(){
		for(int j=0; j<expr.length(); j++){
			char ch = expr.charAt(j);
			
			if(Character.isDigit(ch)){
				tempexpr.append(ch);
				for(int i = j+1; i < expr.length() ; i++){
					if(Character.isDigit(expr.charAt(i)) || expr.charAt(i)=='.'){
						tempexpr.append(expr.charAt(i));
					}else{
						j = i-1;
						break;
					}
				}
				tempexpr.append(",");
			}else{
				switch(ch){
					case '+':
					case '-':
						this.getOper(ch ,1);
						break;
					case '*':
					case '/':
						this.getOper(ch,2);
						break;
					case '(':
						stackOperator.push(ch);
						break;
					case ')':
						this.getParen(ch);
						break;
					default:
					//	output.append(ch); 
						break;
				}//end switch
			}
			
			
		}//end for
		
		while(! stackOperator.isEmpty()){
			tempexpr.append(stackOperator.pop());
		}
		return tempexpr.toString();
	}
	
	/**
	 * 得到后缀表达式的结果
	 * @return
	 */
	public double doParse(){
		stackValue = new StackValue(20);
		char ch;
		int j;
		double num1 , num2, interans;
		
		for(j = 0 ; j < tempexpr.length() ; j++){
			ch = tempexpr.charAt(j);
			if(Character.isDigit(ch)){
				StringBuffer temp = new StringBuffer();
				temp.append(ch);
				for(int i = j+1; i < tempexpr.length() ; i++){
					if(Character.isDigit(tempexpr.charAt(i)) || tempexpr.charAt(i)=='.'){
						temp.append(tempexpr.charAt(i));
					}else if(tempexpr.charAt(i)== ','){
						j = i;
						break;
					}
				}
				stackValue.push(Double.parseDouble(temp.toString()));
			}
			/*if(ch >= 0 && ch <= 9){
				theStack.push((int)(ch-'0'));			
			}*/
			else{
				num2=stackValue.pop();
				num1=stackValue.pop();
				switch(ch){
					case'+':
						interans = num1+num2;
						break;
					case'-':
						interans = num1-num2;
						break;
					case'*':
						interans = num1*num2;
						break;
					case'/':
						interans = num1/num2;
						break;
					default:
						interans = 0;
				}//end swith
				stackValue.push(interans);
			}//end if
		}//end for
		interans = stackValue.pop();
		return interans;
	}//end doParse
	
	
	

	
	private void getOper(char opThis , int prec1){
		while(! stackOperator.isEmpty()){
			char opTop = stackOperator.pop();
			if(opTop == '('){
				stackOperator.push(opTop);
				break;
			}else{
				int prec2;
				if(opTop == '+' || opTop =='-'){
					prec2 = 1;
				}else{
					prec2 = 2;
				}
				
				if(prec2 < prec1){
					stackOperator.push(opTop);
					break;
				}else{
					tempexpr.append(opTop);
				}			
			}
			
		}//end while
		stackOperator.push(opThis);
	}
	
	private void getParen(char ch){
		while(!stackOperator.isEmpty()){
			char chx = stackOperator.pop();
			if(chx == '('){
				break;
			}else{
				tempexpr.append(chx);
			}
		}
	}

	
	public  static void main(String [] args){
		ExprOperation eo = new ExprOperation();
		System.out.print(eo.getExprValue("0.0+1.5+0.1"));
		
	} 
}
