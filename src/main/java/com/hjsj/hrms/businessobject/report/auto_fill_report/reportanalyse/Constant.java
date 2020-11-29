package com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse;

public class Constant {
	
	//表达式的基本元素类别
	public static int INT = 5; //数值   
	public static int CONSTANT=6;//常数
	public static int DELIMITER=1;//分割符
	
	//分割符类别
	public static int S_PLUS = 4;       //加
	public static int S_MINUS = 5;      //减
	public static int S_TIMES = 6;      //乘
	public static int S_DIVISION = 7;   //除
	public static int S_LPARENTHESIS = 20;//左括号
	public static int S_RPARENTHESIS = 21;//右括号
	public static int S_FINISHED = 24;    //结束符
	//表间校验分割符
	public static int S_L = 26; //左中括号
	public static int S_R = 27; //右中括号
	
	//异常类型
	public static int E_NOTEMPTY=1;        //表达式为空
	public static int E_FACTORNOEXIST=2;   //列不存在
	public static int E_LOSSLPARENTHESE=4; //缺少坐括号
	public static int E_LOSSRPARENTHESE=5;//缺少右括号
	public static int E_SYNTAX=6;           //语法错误
	public static int E_LOSSCONSTANT=7;   //未定义常数
	public static int E_CONSTANT=8;   //未定义常数
	public static int E_MUSTBEINTEGER=9;  //必须是整数
	public static int E_EliminatesError=10;  //排除信息错误
	public static int E_ErrorNumber=11;  //非法字符
}
