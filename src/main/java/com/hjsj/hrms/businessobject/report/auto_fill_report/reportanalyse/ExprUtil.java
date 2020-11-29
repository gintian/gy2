/*
 * Created on 2006-5-23
 *
 */
package com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse;

import com.hjsj.hrms.businessobject.report.auto_fill_report.ExprAnalyse;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;

import java.util.*;

/**
 * 表达式实用方法
 * 规范化表达式
 * @author 
 */
public class ExprUtil {
	/**
	 * 获取运算符
	 * @param operator
	 * @return
	 */
	public static String getOperator(String operator){
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
	
	/**
	 * 获取相反的运算符-页面显示使用
	 * @param operator
	 * @return
	 */
	public static String getReverseOperator(String operator){
		String o= "";
		if("=".equals(operator)){
			o="≠";
		}else if(">".equals(operator)){
			o="<=";
		}else if("<".equals(operator)){
			o=">=";
		}else if("≠".equals(operator)){
			o="=";
		}else if(">=".equals(operator)){
			o="<";
		}else if("<=".equals(operator)){
			o=">";
		}
		return o;
	}
	
	/**
	 * 获取相反的运算符-SQL使用
	 * @param operator
	 * @return
	 */
	public static String getReverseOperatorToSQL(String operator){
		String o= "";
		if("=".equals(operator)){
			o="<>";
		}else if(">".equals(operator)){
			o="<=";
		}else if("<".equals(operator)){
			o=">=";
		}else if("≠".equals(operator)){
			o="=";
		}else if(">=".equals(operator)){
			o="<";
		}else if("<=".equals(operator)){
			o=">";
		}
		return o;
	}
	
	/**
	 * 获取排除信息-字符串表示形式
	 * @param lexpr 校验公式中的左表达式
	 * @return
	 */
	public static String getEliminates(String lexpr){
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
	
	/**
	 * 获取排除信息-集合表示形式
	 * @param lexpr
	 * @return
	 */
	public static List getEliminatesList(String lexpr){
		ArrayList list = new ArrayList();
		if("".equals(lexpr)||lexpr == null){
			return null;
		}else{
			if(lexpr.indexOf("|")!=-1){
				String t = lexpr.substring(lexpr.indexOf("|")+1,lexpr.length());
				String [] temp =  t.split(",");
				for(int i =0 ; i< temp.length ; i++){
					if(temp[i] != null){
						list.add(temp[i]);
					}
				}				
			}else{
				return null;
			}			
		}
		return list;	
	}
	/**
	 * 判断某数值是否是排除信息
	 * @param list
	 * @param num
	 * @return
	 */
	public static boolean isEliminates(List list , int num){
		boolean b = false;
		if(list == null){
			return b;
		}else{
			for(int i =0; i< list.size(); i++){
				int temp = Integer.parseInt((String)list.get(i));
				if(temp == num){
					b = true;
					break;
				}
			}
		}
		return b;
	}
	/**
	 * 获取规范的表达式参数为DB中左/右表达式
	 * @param expr
	 * @return
	 */
	public static String getExpr(String expr){
		String temp = ExprUtil.getLexpr(expr);
		String result = ExprUtil.replaceStr(temp,"..");
		return result;
	}
	
	
	//获得..左边的数值
	public static String getLNumber(String  expr){
		StringBuffer temp1 = new StringBuffer();
		for(int i=expr.length()-1; i>=0; i--){
			char c = expr.charAt(i);
			if(Character.isDigit(c)){
				temp1.append(expr.charAt(i));
			}else{
				break;
			}
			/*else if(c == 'C' || c =='c'){ //如果出现C5..6
			 	return "error";
			}*/
		}

		StringBuffer temp = new StringBuffer();
		for(int j = temp1.length()-1; j>=0 ; j--){
			temp.append(temp1.charAt(j));
		}
		return temp.toString();
	}
	
	//获得..右边的数值
	private static String getRNumber(String expr){
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
	private static String getString(String left , String right){
		StringBuffer temp = new StringBuffer();
		temp.append(left);
		int l = Integer.parseInt(left);
		int r = Integer.parseInt(right);
		
		for(int i = l+1 ; i< r ; i++){
			temp.append("+");
			temp.append(i);
		}
		if(l>r) {
            for(int i = r+1 ; i< l ; i++){
                temp.append("+");
                temp.append(i);
            }
        }
		temp.append("+");
		temp.append(right);
		return temp.toString();
	}
	
	//规范表达式
	private static String replaceStr(String str ,String replace){
	    for(int i=str.lastIndexOf(replace); i>=0; i=str.lastIndexOf(replace, i-1)){
			String ln = ExprUtil.getLNumber(str.substring(0,i));
			/*if(ln.equals("error")){
				return "error";
			}*/
			if("".equals(ln)){
				String temp =str.substring(0,i);
				for(int a=0; a< temp.length();a++){
					if(Character.isDigit(temp.charAt(a))||".".equals(""+temp.charAt(a))){
					}else{
						return ""+temp.charAt(a);
					}
			}
				continue;
			}
			String rn = ExprUtil.getRNumber(str.substring(i+replace.length(),str.length()));
			if("".equals(rn)){
				String temp =str.substring(i+replace.length(),str.length());
				for(int a=0; a< temp.length();a++){
					if(Character.isDigit(temp.charAt(a))||".".equals(""+temp.charAt(a))){
					}else{
						return ""+temp.charAt(a);
					}
			}
				continue;
			}
			String t = ExprUtil.getString(ln,rn);           
			str = str.substring(0, i-ln.length())+t+str.substring(i+replace.length()+rn.length(), str.length());		        
	    }//end for
	   // str=str.replaceAll("C","");
	    return str;
	}
	
	
	//获得不规范的左表达式
	public static String getLexpr(String expr){
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
				if(temp.equals(vo.getString("cname").trim()))
				{
					isVariable=true;
					variableList.add(vo.getString("cname"));
					break;
				}
			}
			if(!isVariable)
			{
				char c=temp.toLowerCase().charAt(0);
				if(Character.isLowerCase(c))
				{
					if(DataDictionary.getFieldItem(temp.toLowerCase())!=null) {
                        fieldList.add(temp);
                    }
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
	
	
	
	private int decimal=0;  //临时变量中的小数位
	
	/**
	 * 将统计表达式转换为sql可执行的正规表达式
	 * @param statExpr         统计表达式     
	 * @param midVariableList  临时变量集合
	 * @param fieldType		   列数据类型 map
	 * @param appdate		   截止日期
	 * @return
	 */
	public String tranNormalExpr(String statExpr,ArrayList midVariableList,HashMap fieldType,String appdate)
	{
		ExprAnalyse exprAnalyse=new ExprAnalyse();
		String a_statExpr=statExpr;
		ArrayList factorList=exprAnalyse.analyseStatExpr(statExpr);
		int year=1900;
		int month=0;
		int day=0;
		if(appdate!=null&&appdate.indexOf("-")!=-1)
		{
			String[] date=appdate.split("-");
			year=Integer.parseInt(date[0]);
			month=Integer.parseInt(date[1]);
			day=Integer.parseInt(date[2]);
		}
		else
		{
			GregorianCalendar now=new GregorianCalendar();
			year=now.get(Calendar.YEAR);
			month=now.get(Calendar.MONTH)+1;
			day=now.get(Calendar.DAY_OF_MONTH);

		}
		for(Iterator t=factorList.iterator();t.hasNext();)
		{
			String temp=(String)t.next();
			boolean isVariable=false;             //判断值是否是临时变量
			for(Iterator sub_t=midVariableList.iterator();sub_t.hasNext();)
			{
				RecordVo vo=(RecordVo)sub_t.next();							
				if(temp.equals(vo.getString("cname").trim()))
				{
					if(vo.getInt("ntype")==3)
					{
						/*
						StringBuffer tempstr=new StringBuffer(" (( "+year+"-"+Sql_switcher.year(vo.getString("cname"))+" )+");
						tempstr.append("("+month+"-"+Sql_switcher.month(vo.getString("cname"))+")*0.01+");
						tempstr.append("("+day+"-"+Sql_switcher.day(vo.getString("cname"))+")*0.0001)");			
						*/
						String a_tempstr="("+Sql_switcher.diffDays(Sql_switcher.charToDate("'"+year+"-"+month+"-"+day+"'"),vo.getString("cname"))+")/365.00000000";
						a_statExpr=a_statExpr.replaceAll(temp,a_tempstr);
					
					}
					else if(vo.getInt("ntype")==1)
					{
						if(vo.getInt("flddec")>this.decimal) {
                            this.decimal=vo.getInt("flddec");
                        }
						a_statExpr=a_statExpr.replaceAll(temp,Sql_switcher.isnull(vo.getString("cname"),"0"));
					}
					isVariable=true;
					break;
				}
			}
			if(!isVariable)
			{
				char c=temp.toLowerCase().charAt(0);
				if(Character.isLowerCase(c))
				{
					if(DataDictionary.getFieldItem(temp.toLowerCase())==null)
					{
						a_statExpr="0";
						break;
					}
					String a_type=(String)fieldType.get(temp);
					if("D".equals(a_type))
					{												
						/*
						StringBuffer tempstr=new StringBuffer(" (( "+year+"-"+Sql_switcher.year(temp)+" )+");
						tempstr.append("("+month+"-"+Sql_switcher.month(temp)+")*0.01+");
						tempstr.append("("+day+"-"+Sql_switcher.day(temp)+")*0.0001)");	
						*/
						
						String a_tempstr="("+Sql_switcher.diffDays(Sql_switcher.charToDate("'"+year+"-"+month+"-"+day+"'"),temp)+")/365.00000000";
						a_statExpr=a_statExpr.replaceAll(temp,a_tempstr.toString());	
					}else if("N".equals(a_type)){//考虑指标为空的情况 xieguiquan2010 0519
						String a_tempstr=Sql_switcher.sqlNull(temp,0);
						a_statExpr=a_statExpr.replaceAll(temp,a_tempstr.toString());
					}
				}
			}

		}	
	//	System.out.println("a_statExpr="+a_statExpr);
		return a_statExpr;
	}
	
	
	public String tranNormalExpr(String statExpr,ArrayList midVariableList,HashMap fieldType,String appdate,String tableName)
	{
		ExprAnalyse exprAnalyse=new ExprAnalyse();
		String a_statExpr=statExpr;
		ArrayList factorList=exprAnalyse.analyseStatExpr(statExpr);
		int year=1900;
		int month=0;
		int day=0;
		if(appdate!=null&&appdate.indexOf("-")!=-1)
		{
			String[] date=appdate.split("-");
			year=Integer.parseInt(date[0]);
			month=Integer.parseInt(date[1]);
			day=Integer.parseInt(date[2]);
		}
		else
		{
			GregorianCalendar now=new GregorianCalendar();
			year=now.get(Calendar.YEAR);
			month=now.get(Calendar.MONTH)+1;
			day=now.get(Calendar.DAY_OF_MONTH);

		}
		for(Iterator t=factorList.iterator();t.hasNext();)
		{
			String temp=(String)t.next();
			boolean isVariable=false;             //判断值是否是临时变量
			for(Iterator sub_t=midVariableList.iterator();sub_t.hasNext();)
			{
				RecordVo vo=(RecordVo)sub_t.next();							
				if(temp.equals(vo.getString("cname").trim()))
				{
					if(vo.getInt("ntype")==3)
					{
						/*
						StringBuffer tempstr=new StringBuffer(" (( "+year+"-"+Sql_switcher.year(vo.getString("cname"))+" )+");
						tempstr.append("("+month+"-"+Sql_switcher.month(vo.getString("cname"))+")*0.01+");
						tempstr.append("("+day+"-"+Sql_switcher.day(vo.getString("cname"))+")*0.0001)");			
						*/
						String a_tempstr="("+Sql_switcher.diffDays(Sql_switcher.charToDate("'"+year+"-"+month+"-"+day+"'"),vo.getString("cname"))+")/365.00000000";
						a_statExpr=a_statExpr.replaceAll(temp,a_tempstr);
					
					}
					else if(vo.getInt("ntype")==1)
					{
						if(vo.getInt("flddec")>this.decimal) {
                            this.decimal=vo.getInt("flddec");
                        }
						a_statExpr=a_statExpr.replaceAll(temp,Sql_switcher.isnull(tableName + "."+vo.getString("cname"),"0"));
					}
					isVariable=true;
					break;
				}
			}
			if(!isVariable)
			{
				char c=temp.toLowerCase().charAt(0);
				if(Character.isLowerCase(c))
				{
					if(DataDictionary.getFieldItem(temp.toLowerCase())==null)
					{
						a_statExpr="0";
						break;
					}
					String a_type=(String)fieldType.get(temp);
					if("D".equals(a_type))
					{												
						/*
						StringBuffer tempstr=new StringBuffer(" (( "+year+"-"+Sql_switcher.year(temp)+" )+");
						tempstr.append("("+month+"-"+Sql_switcher.month(temp)+")*0.01+");
						tempstr.append("("+day+"-"+Sql_switcher.day(temp)+")*0.0001)");	
						*/
						
						String a_tempstr="("+Sql_switcher.diffDays(Sql_switcher.charToDate("'"+year+"-"+month+"-"+day+"'"),temp)+")/365.00000000";
						a_statExpr=a_statExpr.replaceAll(temp,a_tempstr.toString());	
					}else if("N".equals(a_type)){//考虑指标为空的情况 xieguiquan2010 0519
						String a_tempstr=Sql_switcher.sqlNull(temp,0);
						a_statExpr=a_statExpr.replaceAll(temp,a_tempstr.toString());
					}
				}
			}

		}	
	//	System.out.println("a_statExpr="+a_statExpr);
		return a_statExpr;
	}
	
	

	
	

	/**
	 * 获取一个规范的表达式包含的因子-字符串表现形式
	 * @param expr
	 * @return
	 */
	public static String  getExprNumber(String expr){
		if(expr == null){
			return null;
		}
		StringBuffer exprNumber = new StringBuffer();
		for(int i=0; i<expr.length(); i++){
			StringBuffer temp = new StringBuffer();
			if(Character.isDigit(expr.charAt(i))){
				temp.append(expr.charAt(i));
				for(int j = i+1; j<expr.length(); j++){
					if(Character.isDigit(expr.charAt(j))){
						temp.append(expr.charAt(j));
						if(j == expr.length()-1){
							i = j;
						}
					}else{						
						i = j-1;											
						break;
					}				
				}//end for
				exprNumber.append(temp);
				exprNumber.append(",");
			}else if(expr.charAt(i) =='C' || expr.charAt(i) == 'c'){
				
				for(int j = i+1; j<expr.length(); j++){
					if(Character.isDigit(expr.charAt(j)) || expr.charAt(j)=='.'){	
						if(j == expr.length()-1){
							i=j;
						}
					}else{
						i = j;
						break;
					}
				}//end for
				
			}
			
		}//end for
		
		return exprNumber.toString();
	}
	
	/**
	 * 获得表达式的因子-集合表现形式
	 * @param expr
	 * @return
	 */
	public static List getExprNumbers(String expr){
		if(expr == null){
			return null;
		}
		String temp = ExprUtil.getExprNumber(ExprUtil.getExpr(expr));
		if(temp == null || "".equals(temp)){
			return null;
		}
		String [] t = temp.split(",");
		
		ArrayList list = new ArrayList();
		for(int i = 0 ; i< t.length ; i++){
			list.add(t[i]);
		}
		
		return list;		
	}



	
	public static void main(String [] args){
		Float[] a=new Float[2];
		a[0]=new Float(1.22);
		a[1]=new Float(1.33);
		
		Float[] a1=new Float[2];
		a1[0]=new Float(2.22);
		a1[1]=new Float(3.33);
		
		ArrayList list=new ArrayList();
		list.add(a);
		list.add(a1);
		
		for(int i=0;i<2;i++)
		{
			Float[] s=(Float[])list.get(i);
		//	System.out.println(s[0]+"----------"+s[1]);
			
			
		}
		
		
	}

	public int getDecimal() {
		return decimal;
	}

	public void setDecimal(int decimal) {
		this.decimal = decimal;
	}
	
}
