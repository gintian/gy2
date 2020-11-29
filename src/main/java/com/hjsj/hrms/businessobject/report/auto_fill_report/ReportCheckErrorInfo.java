/*
 * Created on 2006-5-26
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.businessobject.report.auto_fill_report;

/**
 * 封装报表校验错误信息
 * 
 * @author 
 *
 */
public class ReportCheckErrorInfo {
	
	private String expr = "null";           //校验错误表达式
	private String checkErrorInfo = "null"; //校验错误信息
	private String exprSolecism = "null" ;   //校验公式语法错误信息
	
	private String lExprNumErrorInfo = "null"; //左表达式错误(逗号分割)
	private String rExprNumErrorInfo = "null"; //右表达式错误
	
	private String rows = "null";         //列校验中错误的行号信息(逗号分割)
	private String cols = "null";        //行校验中错误的列号信息(逗号分割)

	/**
	 * 语法分析错误
	 * @param expr
	 * @param exprSolecism
	 */
	public ReportCheckErrorInfo(String expr , String exprSolecism){
		this.expr = expr;
		this.exprSolecism = exprSolecism;
	}
	
	/**
	 * 表达式校验错误
	 * @param expr 	校验错误表达式
	 * @param checkErrorInfo 错误信息
	 * @param lexpr  不规范的左表达式
	 * @param rexpr  不规范的右表达式
	 * @param es   校验错误的行号或列号信息
	 * @param flag  0/1  0:列校验  1：行校验
	 */
	public ReportCheckErrorInfo(String expr , String checkErrorInfo ,
			      String lexpr , String rexpr , String es , int flag){
		this.expr = expr;
		this.checkErrorInfo = checkErrorInfo;
		this.lExprNumErrorInfo = lexpr;
		this.rExprNumErrorInfo = rexpr;
		if(flag == 0){
			this.rows = es;
			this.cols = "null";
		}else{
			this.cols = es;
			this.rows = "null";
		}
		
	}
	
	
	
	public String getCols() {
		return cols;
	}

	public String getRows() {
		return rows;
	}

	public String getExprSolecism() {
		return exprSolecism;
	}
	
	public String getCheckErrorInfo() {
		return checkErrorInfo;
	}



	public String getExpr() {
		return expr;
	}

	public String getLExprNumErrorInfo() {
		return lExprNumErrorInfo;
	}

	public String getRExprNumErrorInfo() {
		return rExprNumErrorInfo;
	}


	
	/**
	 * 格式化输出
	 */
	@Override
    public String toString(){
		//格式化操作
		this.expr = this.expr.replaceAll("&nbsp;" , "");
		this.checkErrorInfo = this.checkErrorInfo.replaceAll("&nbsp;","");
		this.checkErrorInfo = this.checkErrorInfo.replaceAll("<br>","\\\n");
		this.exprSolecism = this.exprSolecism.replaceAll("\"" , "'");
		return this.expr+"@" + this.checkErrorInfo +"@" + this.exprSolecism +"@" + 
			   this.lExprNumErrorInfo +"@" +this.rExprNumErrorInfo +"@" +this.rows +"@" + this.cols;
		/*
		返回值样式
			校验错误表达式@校验错误信息@校验公式语法错误信息@左表达式错误(逗号分割)@右表达式错误@列校验中错误的行号集合@行校验中错误的列号集合#	
		@一条内部分割
		#条目之间分割
		 */
	}
}
