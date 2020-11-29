
package com.hjsj.hrms.businessobject.report.auto_fill_report;

import java.util.ArrayList;


//字符串表达式的计算
//仅限于+-*/().
// 前提条件：输入正确的参数（表达式）程序未提供表达式合法性的验证

public class StringExprOperation {
	
	public StringExprOperation(){
		
	}
	
	//表达式计算函数
	public double getExprOperValue(double le ,char o , double re){
		double result = 0.0;
		if(o == '+'){
			result = le+re;
		}else if(o == '-'){
			result = le-re;
		}else if(o == '*'){
			result = le*re;
		}else if(o == '/'){
			result = le/re;
		}
		return result;
	}
		
	//获取字符串表达式的计算值，不包括括号
	public String getTempValue(String temp){
		//传入负数 
		if(temp.charAt(0)=='-'&& temp.indexOf('+')== -1 &&
				temp.indexOf('*')==-1 &&  temp.indexOf('/')==-1	&& temp.indexOf("-" ,1) == -1){
			return temp;
		}else{
			while(true){
				for(int i=0 ;i<temp.length(); i++){
					if(temp.charAt(i)=='*' || temp.charAt(i)=='/'){						
						String sld = this.getLNumber(temp,i);
						String srd = this.getRNumber(temp,i);
						double ld = Double.parseDouble(sld);
						char c = temp.charAt(i);
						double rd = Double.parseDouble(srd);															
						double d = this.getExprOperValue(ld,c,rd);					
						int start = i - sld.length();
						int end = i+srd.length();
						temp = this.getTempString(temp, start , end , String.valueOf(d));						
						break;
					}
				}//end for
			
				//不包含任何运算符
				if(temp.indexOf('*')==-1 &&  temp.indexOf('/')==-1 ){
					break;
				}
			}//end while
			
			//加减处理
			while(true){
				int n = 0;
				if(temp.charAt(0)=='-'){
					n=1;
				}
				for(int i=n ;i<temp.length(); i++){
					if(temp.charAt(i)=='+' || temp.charAt(i)=='-' && Character.isDigit(temp.charAt(i-1))== true ){
						String sld = this.getLNumber(temp,i);
						String srd = this.getRNumber(temp,i);
						double ld = Double.parseDouble(sld);
						char c = temp.charAt(i);
						double rd = Double.parseDouble(srd);
						double d = this.getExprOperValue(ld,c,rd);
						int start = i - sld.length();
						int end = i+srd.length();
						temp = this.getTempString(temp, start , end , String.valueOf(d));
						break;
					}//end if 
				}//end for
			
				if(temp.charAt(0)=='-' && temp.indexOf('+',1)== -1 && temp.indexOf('-',1)== -1){
					break;
				}
				if(temp.charAt(0)!='-' && temp.indexOf('+')== -1 && temp.indexOf('-')== -1){
					break;
				}
			}//end whild
			return temp;
		}//end if
		
	}
	
	//获得计算时的中间字符串
	public String getTempString(String expr ,int start , int end ,String t){
		StringBuffer temp = new StringBuffer();
		if(start == 0){
			temp.append(t);
			temp.append(expr.substring(end+1 , expr.length()));
		}else{
			if(t.charAt(0)=='-'){//结果为负数
				//得到负号前一个字符
				char c = expr.charAt(start-1);
				if(c=='+'){
					temp.append(expr.substring(0,start-1));
					temp.append(t);
					temp.append(expr.substring(end+1 , expr.length()));
				}else if(c == '-'){
					temp.append(expr.substring(0,start-1));
					temp.append('+');
					temp.append(t.substring(1,t.length()));
					temp.append(expr.substring(end+1 , expr.length()));
				}else{//（*/）
					temp.append(expr.substring(0,start));
					temp.append(t);
					temp.append(expr.substring(end+1 , expr.length()));
				}
			}else{
				temp.append(expr.substring(0,start));
				temp.append(t);
				temp.append(expr.substring(end+1 , expr.length()));
			}
		}
		return temp.toString();
	}
	
	//获得运算符左边的数值
	public String getLNumber(String  expr , int flag){
		String str = expr.substring(0,flag);
		StringBuffer temp1 = new StringBuffer();
		for(int i=str.length()-1; i>=0; i--){
			if(Character.isDigit(str.charAt(i))|| str.charAt(i)=='.'){
				temp1.append(str.charAt(i));
			}
			else if(str.charAt(i)=='-' ){ 
				if(i==0){
					temp1.append(str.charAt(i));
				}else{
					if("+-*/".indexOf(str.charAt(i-1))!= -1){
						temp1.append(str.charAt(i));
					}else{
						break;
					}
				}
				
			}else{
				break;
			}
		}

		StringBuffer temp = new StringBuffer();
		for(int j = temp1.length()-1; j>=0 ; j--){
			temp.append(temp1.charAt(j));
		}
		return temp.toString();
	}
	
	//获得运算符右边的数值
	public String getRNumber(String expr ,int flag ){
		String str = expr.substring(flag+1,expr.length());
		StringBuffer temp = new StringBuffer();
		if(str.charAt(0)=='-'){
			temp.append(str.charAt(0));
			for(int i=1; i< str.length();i++){
				if(Character.isDigit(str.charAt(i))|| str.charAt(i)=='.'){
					temp.append(str.charAt(i));
				}else{
					break;
				}
			}
		}else{
			for(int i=0; i< str.length();i++){
				if(Character.isDigit(str.charAt(i))|| str.charAt(i)=='.'){
					temp.append(str.charAt(i));
				}else{
					break;
				}
			}
		}
		
		return temp.toString();
	}
	
	//获取表达式
	public String getExprValue(String expr){
		StringBuffer temp = new StringBuffer();
		//传入数值无运算符如(12,(12))
		if(expr.indexOf('+')==-1 && expr.indexOf('-')==-1 && expr.indexOf('*')==-1 && expr.indexOf('/')==-1){		
			for(int i=0 ; i<expr.length();i++){
				if(expr.charAt(i)== '(' || expr.charAt(i) ==')'){	
				}else{
					temp.append(expr.charAt(i));
				}
			}
			return temp.toString();
		}
		
		while(true){
			ArrayList list = new ArrayList();
			for(int i=0 ; i< expr.length(); i++){
				if(expr.charAt(i)=='('){	
					list.add(String.valueOf(i));
				}
			}
			if(list.size()== 0){//如果没有括号
				return this.getTempValue(expr);//计算
			}else{
				for(int j=list.size(); j>0; j--){
					//ln是（的位置,rn是）括号的位置
					int ln = Integer.parseInt((String)list.get(j-1));
					int rn = expr.indexOf(')',ln);	
					
					//获取最内层括号里的表达式，不含括号本身
					String  temp1 = expr.substring(ln+1,rn);
					//得到括号内表达式的计算值
					String tt = this.getTempValue(temp1);
					//替换字符串
					expr = this.getTempString(expr,ln,rn,tt);
					break;
				}
			}
			
		}//end while
	}
	
	
	//校验表达式是否正确
	public boolean checkExpr(String lexprvalue , String operator , String rexprvalue){
		boolean b = false;
		double le = Double.parseDouble(lexprvalue);
		double re = Double.parseDouble(rexprvalue);
		if("=".equals(operator)){
			b=(le == re);
		}else if(">".equals(operator)){
			b=(le > re);
		}else if("<".equals(operator)){
			b=(le < re);
		}else if("≠".equals(operator)){
			b=(le != re);
		}else if(">=".equals(operator)){
			b=(le >= re);
		}else if("<=".equals(operator)){
			b=(le <= re);
		}
		return b;
	}
}
