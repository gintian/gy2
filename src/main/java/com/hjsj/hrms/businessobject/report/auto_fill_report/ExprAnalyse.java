/*
 * Created on 2006-5-13
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.businessobject.report.auto_fill_report;

import com.hrms.frame.dao.RecordVo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author
 *
 * 语法分析器-得到规范的表达式
 * 表达式中..被替换 C保留
 */
public class ExprAnalyse {
	
	private String LExpr; //左表达式
	private String RExpr; //右表达式
	private String CLExpr; //规范的左表达式
	private String CRExpr; //规范的右表达式
	private String operator;//运算符
	private List eliminates;//排除信息(行或列)
	
/*	double lexprValue;//左表达式的值
	double rexprValue;//右表达式的值
*/	
	public ExprAnalyse(){		
	}
	public ExprAnalyse(String lexpr , String operator ,String rexpr){
		this.LExpr=lexpr;
		this.RExpr=rexpr;
		this.operator=this.getOperator(operator);
	}
	
	//获得规范的左表达式
	public String getCLExpr(){
		return this.getExpr(this.LExpr);
	}
	
	//获得规范的右表达式
	public String getCRExpr(){
		return this.getExpr(this.RExpr);
	}
	
	//获取排除列表
	public List getEliminates(){
		return this.getEliminate(this.LExpr);
	}
	
	//获取运算符
	public String getOperator(){
		return this.operator;
	}
	
	//获取运算符
	public String getOperator(String operator){
		String o= "";
		if("0".equals(operator)){
			o="=";
		}else if("1".equals(operator)){
			o=">";
		}else if("2".equals(operator)){
			o="<";
		}else if("3".equals(operator)){
			o="≠";
		}else if("4".equals(operator)){
			o=">=";
		}else if("5".equals(operator)){
			o="<=";
		}
		return o;
	}
	
	
	//获得..左边的数值
	private String getLNumber(String  expr){
		StringBuffer temp1 = new StringBuffer();
		for(int i=expr.length()-1; i>=0; i--){
			if(Character.isDigit(expr.charAt(i))){
				temp1.append(expr.charAt(i));
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
	
	//获得..右边的数值
	private String getRNumber(String expr){
		StringBuffer temp = new StringBuffer();
		for(int i=0; i< expr.length();i++){
			if(Character.isDigit(expr.charAt(i))){
				temp.append(expr.charAt(i));
			}else{
				break;
			}
		}
		return temp.toString();
	}
	
	//获得表达式中间值用于替换XX..XX
	private String getString(String left , String right){
		StringBuffer temp = new StringBuffer();
		temp.append("(");
		temp.append(left);
		int l = Integer.parseInt(left);
		int r = Integer.parseInt(right);
		
		for(int i = l+1 ; i< r ; i++){
			temp.append("+");
			temp.append(i);
		}
		temp.append("+");
		temp.append(right);
		temp.append(")");
		return temp.toString();
	}
	
	//获取规范的表达式
	private String replaceStr(String str ,String replace){
	    for(int i=str.lastIndexOf(replace); i>=0; i=str.lastIndexOf(replace, i-1)){
			String ln = this.getLNumber(str.substring(0,i));
			String rn = this.getRNumber(str.substring(i+replace.length(),str.length()));
			String t = this.getString(ln,rn);           
			str = str.substring(0, i-ln.length())+t+str.substring(i+replace.length()+rn.length(), str.length());		        
	    }//end for
	   // str=str.replaceAll("C","");
	    return str;
	}
	
	//获得不规范的左表达式
	public String getLexpr(String expr){
		if("".equals(expr)||expr == null){
			return null;
		}else{
			if(expr.indexOf("|")!=-1){
				return expr.substring(0,expr.indexOf("|"));		
			}else{
				return expr;
			}			
		}
	}
	public String getEliminates(String lexpr){
		if("".equals(lexpr)||lexpr == null){
			return null;
		}else{
			if(lexpr.indexOf("|")!=-1){
				return lexpr.substring(lexpr.indexOf("|")+1,lexpr.length());		
			}else{
				return null;
			}			
		}
	}
	//获得排除行或列
	private List getEliminate(String expr){
		if("".equals(expr)||expr == null){
			return null;
		}else{		
			if(expr.indexOf("|")!=-1){
				ArrayList list = new ArrayList();
				String temp = expr.substring(expr.indexOf("|")+1,expr.length());
				String [] s = temp.split(",");
				for(int i=0; i< s.length;i++){
					list.add(s[i]);
				}
				return list;
			}else{
				return null;
			}
		}
	}
	
	//判断一列是否是排除列
	//elist 排除列集合
	public boolean isEliminate(List elist , int num){
		boolean b = false;
		if(elist == null){
			return b;
		}
		for(int i = 0 ; i< elist.size(); i++){
			String temp = (String)elist.get(i);
			if(temp.equals(String.valueOf(num))){
				b=true;
			}
		}
		return b;
	}
	
	//获取规范的表达式参数为DB中左/右表达式
	private String getExpr(String expr){
		String temp = this.getLexpr(expr);
		return this.replaceStr(temp,"..");
	}
	
	


	
	/**
	 * 将表达式因子区分为 字段和临时变量
	 * @param statExpr
	 * @param midVariableList
	 * @author dengc
	 * @return
	 */
	public ArrayList statExprAnalyse(String statExpr,ArrayList midVariableList)

	{
		ArrayList list=new ArrayList();
		ArrayList fieldList=new ArrayList();      //指标集和
		ArrayList variableList=new ArrayList();   //临时变量集合
		
		ArrayList factorList=analyseStatExpr(statExpr);
		for(Iterator t=factorList.iterator();t.hasNext();)
		{
			String temp=(String)t.next();
			boolean isVariable=false;             //判断值是否是临时变量
			for(Iterator sub_t=midVariableList.iterator();sub_t.hasNext();)
			{
				RecordVo vo=(RecordVo)sub_t.next();
			//	System.out.println("temp="+temp+"  ---"+vo.getString("chz").trim());
				if(temp.equals(vo.getString("chz").trim()))
				{
					isVariable=true;
					variableList.add(vo.getString("cname"));
					break;
				}
			}
			if(!isVariable)
			{
				char c=temp.toLowerCase().charAt(0);
				if(Character.isLowerCase(c)) {
                    fieldList.add(temp);
                }
			}
		}
		list.add(fieldList);
		list.add(variableList);
		return list;
		
	}
	
	

	
	/**
	 * 分析出统计表达式中的因子
	 * @param statExpr  统计表达式  exp:(A0110+B0110*2)/32.56-(A0114)
	 * @author dengc
	 * @return 因子列表
	 */
	public ArrayList analyseStatExpr(String statExpr)
	{
		String a_statExpr="("+statExpr+")";
		ArrayList factorList=new ArrayList();
		int begIndex=0;
		for(int i=0;i<a_statExpr.length();i++)
		{
			char t=a_statExpr.charAt(i);			
			int endIndex=0;
			if(t=='('||t==')'||t=='+'||t=='-'||t=='*'||t=='/')
			{
				begIndex=i;
			}
			else
			{
				for(int b=i;b<a_statExpr.length();b++)
				{
					char tt=a_statExpr.charAt(b);
					if(tt=='('||tt==')'||tt=='+'||tt=='-'||tt=='*'||tt=='/')
					{
						endIndex=b;
						break;
					}
				}
			}
			if(endIndex>begIndex)
			{
				factorList.add(a_statExpr.substring(begIndex+1,endIndex).trim());
				i=endIndex-1;
			}
		}
		return factorList;
	}
	
	
	
	
	
	public static void main(String[] arg)
	{
		String aa="((A0110+B0110*2)/32.56-(A0114))";

		int begIndex=0;
		for(int i=0;i<aa.length();i++)
		{
			char t=aa.charAt(i);			
			int endIndex=0;
			if(t=='('||t==')'||t=='+'||t=='-'||t=='*'||t=='/')
			{
				begIndex=i;
			}
			else
			{
				for(int b=i;b<aa.length();b++)
				{
					char tt=aa.charAt(b);
					if(tt=='('||tt==')'||tt=='+'||tt=='-'||tt=='*'||tt=='/')
					{
						endIndex=b;
						break;
					}
				}
			}
			
			if(endIndex>begIndex)
			{
				//System.out.println(aa.substring(begIndex+1,endIndex));
				i=endIndex-1;
			}
		}
		
		//System.out.println("dsfasdfasdf");
		
	}
	
	
	
}
