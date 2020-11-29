/**
 * 
 */
package com.hjsj.hrms.businessobject.report.formulaAnalyse;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse.Constant;
import com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse.ExprUtil;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;

/**
 * <p>Title:特定表间公式校验右表达式片段分析</p>
 * <p>Description:如:c100 + [60:c100+2..5]+c200 分析c100+2..5</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 14, 2006:5:12:21 PM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportSpaceRExprSegmentAnalyse {
	
	private TnameBo tbo;
	private int flag;
	private int maxNum;
	private int c; //表间行计算公式中的一个列信息
	private String tabid;
	private String reportPrefix;
	private String whereSQL;
	//右表达式分析
	private String expr; //表间计算公式右表达式 C10.5 + [66:1..3+C100]
	private int tok;
	private String token;
	private int token_type;
	private int n;
	private int exprLen;
	
	private String errors;
	private StringBuffer  sql = new StringBuffer();
	
	/**
	 * 表间右表达式片段分析
	 * @param tbo
	 * @param flag 1行2列-标识
	 * @param reportPrefix 操作报表的前缀tb / tt_
	 * @param whereSQL  操作表的限制条件 根据用户名或填报单位编码获得操作的数据
	 */
	public  ReportSpaceRExprSegmentAnalyse( TnameBo tbo , int flag ,String tabid ,String reportPrefix,String whereSQL){
		this.tbo = tbo;
		this.flag = flag;
		this.tabid = tabid;
		this.reportPrefix = reportPrefix;
		this.whereSQL = whereSQL;
	}
	
	
	/**
	 * 获得一个因子
	 * @return
	 */
	public boolean getToken(){
		boolean result = true;
		
		this.tok = 0;
		this.token = "";
		this.token_type = 0;
		
		//空表达式
		if(this.exprLen == 0){
			result = false;
			return result;
		}
		
		//字符已经取完
		if(n == this.exprLen){
			this.tok = Constant.S_FINISHED;
			this.token_type = Constant.DELIMITER;
			return result;
		}
		
		//空白字符处理
		while(Character.isSpaceChar(this.expr.charAt(n))){
			n++;
		}
		
		//字符已经取完
		if(n == this.exprLen){
			this.tok = Constant.S_FINISHED;
			this.token_type = Constant.DELIMITER;
			return result;
		}
		
		//连接符处理
		if("+-*/()[]".indexOf(this.expr.charAt(n))!=-1){
			char c = this.expr.charAt(n);
			this.token = String.valueOf(c);
			switch(c){
				case '+':
					this.tok = Constant.S_PLUS;
					break;
				case '-':
					this.tok = Constant.S_MINUS;
					break;
				case '*':
					this.tok = Constant.S_TIMES;
					break;
				case '/':
					this.tok = Constant.S_DIVISION;
					break;
				case '(':
					this.tok = Constant.S_LPARENTHESIS;
					break;
				case ')':
					this.tok = Constant.S_RPARENTHESIS;
					break;
				
			}
			this.token_type = Constant.DELIMITER;
			n++;
			return result;
			
		}
		
		//数值处理
		if(Character.isDigit(this.expr.charAt(n))){
			char c = this.expr.charAt(n);
			token = String.valueOf(c);
			n++;
			if(n == this.exprLen){//因子取完
			}else{
				while(Character.isDigit(this.expr.charAt(n))){
					token += String.valueOf(this.expr.charAt(n));
					n++;
					if(n == this.exprLen){
						break;
					}
				}
			}
			token_type = Constant.INT;
			return result;
		}
		
		//常量处理
		if(this.expr.charAt(n)=='c' || this.expr.charAt(n)=='C'){
			this.token = String.valueOf(this.expr.charAt(n));
			n++;
			if(n == this.exprLen){			
			}else{
				while(Character.isDigit(this.expr.charAt(n)) ||this.expr.charAt(n)=='.'){
					token += String.valueOf(this.expr.charAt(n));
					n++;
					if(n == this.exprLen){
						break;
					}
					
				}				
			}	
			this.token_type=Constant.CONSTANT;
			return result;
			
		}
		
		result = false;
		//到此处的字符都是非法字符
		this.setErrors(Constant.E_ErrorNumber);
		return result;
	}
	
	/**
	 * 加减处理
	 * @return
	 */
	public boolean level0(){
		boolean result = false;
		
		if(!level1()){
			return result;
		}
		
		//取出的字符是加减
		while(this.tok == Constant.S_PLUS || this.tok == Constant.S_MINUS){
			this.sql.append(this.token);
			//取下一个因子
			if(!this.getToken()){
				return result;
			}
			//下沉
			if(!level1()){
				return result;
			}
			
		}
		
		result = true;
		return result;
	}
	
	/**
	 * 处理乘除
	 * @return
	 */
	public boolean level1(){
		boolean result = false;
		
		if(!level2()){
			return result;
		}
		
		//如果取出的字符是乘除符号
		while(this.tok == Constant.S_TIMES || this.tok == Constant.S_DIVISION){
			this.sql.append(this.token);
			//取出下一个字符
			if(!this.getToken()){
				return result;
			}
			//下沉
			if(!level2()){
				return result;
			}
		}
		
		result = true;
		return result;
	}
	
	/**
	 * 括号处理
	 * @return
	 */
	public boolean level2(){
		boolean result = false;
			
		if(this.tok == Constant.S_LPARENTHESIS && this.token_type == Constant.DELIMITER){//所取因子是左括号
			this.sql.append(this.token);
			//取下一个因子
			if(!this.getToken()){
				return result;
			}
			
			if(!level0()){
				return result;
			}
			this.sql.append(this.token);
			//没有右括号
			if(this.tok != Constant.S_RPARENTHESIS){
				this.putBack();
				this.setErrors(Constant.E_LOSSRPARENTHESE);
				return result;
			}
			
			if(! this.getToken()){
				return result;
			}
			
		}else if(tok == Constant.S_MINUS && token_type == Constant.DELIMITER){//负号处理
			//负号后面只能是常量，括号,数值
			this.sql.append(token);
			if(this.sql.toString().endsWith("--")){
				this.sql.substring(0,this.sql.toString().length()-2);
				this.sql.append("+");
			}
			if(!this.getToken()){
				return result;
			}	
			if(tok == Constant.S_LPARENTHESIS || token_type==Constant.CONSTANT || token_type==Constant.INT){
				if(!level0()){
					return result;
				}
			}else{
				this.putBack();
				this.setErrors(Constant.E_SYNTAX);
				return result;
			}
			
		}else{
			if(!this.primitive()){
				return result;
			}
		}
		
		
		result = true;
		return result;
	}
	
	/**
	 * 处理常量与数值
	 * @return
	 */
	public boolean primitive(){

		boolean result = false;
		int nToken = 0;
		if(token_type == Constant.INT){
			nToken = Integer.parseInt(token);
			
			//因子合法性检测
			if(nToken > this.maxNum){
				this.setErrors(Constant.E_FACTORNOEXIST);
				return result;
			}
			
			//合成SQL语句片段
			StringBuffer tempsql = new StringBuffer();	
			tempsql.append("( ");
			if(this.flag == 1){//行

				//判断左表达式中的列在右表达式中是否存在
				if(this.tbo.getColMap().get(String.valueOf(this.c))!= null){	
					
					//当前表的当前行
					int row = Integer.parseInt((String)this.tbo.getRowMap().get(this.token))+1;				
					
					StringBuffer s = new StringBuffer();					
					s.append("( select C");
					s.append(Integer.parseInt((String)this.tbo.getColMap().get(String.valueOf(this.c)))+1);
					s.append(" from ");
					s.append(this.reportPrefix);
					s.append(this.tabid);
					s.append(" where secid=");
					s.append(row);	
					s.append(" " +this.whereSQL);
					s.append(" )");
					
					tempsql.append(Sql_switcher.isnull(s.toString(),"0"));		
					
				}else{//不存在
					tempsql.append("0");
				}
			}else if(this.flag == 2){//列
				//当前表的行
				if(this.tbo.getRowMap().get(String.valueOf(this.c))==null){
					tempsql.append("0");
				}else{
					
				int row = Integer.parseInt((String)this.tbo.getRowMap().get(String.valueOf(this.c)))+1;		
				StringBuffer s = new StringBuffer();
				s.append("( select C");
				s.append(Integer.parseInt((String)this.tbo.getColMap().get(this.token))+1);
				s.append(" from ");
				s.append(this.reportPrefix);
				s.append(this.tabid);
				s.append(" where secid=");	
				s.append(row);
				s.append(" " +this.whereSQL);
				s.append(" )");
				
				tempsql.append(Sql_switcher.isnull(s.toString(),"0"));	
				}
			}
			tempsql.append(" )");
			
			this.sql.append(tempsql.toString());
			
			if(!this.getToken()){
				return result;
			}
			
		}else if(this.token_type == Constant.CONSTANT){
			
			if(this.token.length()==1){//只有C
				this.setErrors(Constant.E_LOSSCONSTANT);
				return result;
			}else if(this.token.endsWith(".")){//常量语法错误
				this.setErrors(Constant.E_SYNTAX);
				return result ;
			}else{
				if(!this.getToken()){//取下一个因子
					//如果没有取出则返回false
					return result;
				}
			}
		}else{//非法字符出现
			this.putBack();
			this.setErrors(Constant.E_SYNTAX);
			return result ;
		}
		
		result= true;
		return result;
	}
	
	/**
	 * 语法错误后下标定位
	 * @return
	 */
	public boolean putBack(){
		this.n -= token.length();
		return true;
	}
	
	/**
	 * 
	 * @param expr
	 * @param maxNum
	 * @param c
	 * @return
	 */
	public boolean run(String expr , int maxNum , int c){
		boolean result = false;
		
		this.maxNum = maxNum;
		this.expr = expr;
		this.exprLen = expr.length();
		this.n = 0;
		this.c = c;
		
		if(!this.getToken()){
			//空表达式
			this.setErrors(Constant.E_NOTEMPTY);
			return result;
		}
		
		if(!level0()){
			return result;
		}
				
		result = true;
		return result;
	}
	
	public void setErrors(int num){
		if(Constant.E_NOTEMPTY == num){
			this.errors =ResourceFactory.getProperty("constant.e_notempty");
		}else if(Constant.E_LOSSLPARENTHESE == num){
			this.errors =ResourceFactory.getProperty("constant.e_losslparenthese");
		}else if(Constant.E_LOSSRPARENTHESE == num){
			this.errors =ResourceFactory.getProperty("constant.e_lossrparenthese");
		}else if(Constant.E_SYNTAX == num){
			this.errors =ResourceFactory.getProperty("constant.e_syntax");
		}else if(Constant.E_MUSTBEINTEGER == num){
			this.errors =ResourceFactory.getProperty("constant.e_mustbeinteger");
		}else if(Constant.E_FACTORNOEXIST == num){
			this.errors =ResourceFactory.getProperty("constant.e_factornoexist");
		}else if(Constant.E_LOSSCONSTANT == num){
			this.errors =ResourceFactory.getProperty("constant.e_lossconstant");
		}
		
		if(n <= 200){	
			this.errors = ResourceFactory.getProperty("rowcheckanalyse.zhong")+
			ExprUtil.getLNumber(expr.substring(0,n)) + this.errors;	
		}else{
			this.errors = ResourceFactory.getProperty("rowcheckanalyse.zhong")+ 
			ExprUtil.getLNumber(expr.substring(0,n)) + this.errors;			
		}
	}


	public String getErrors() {
		return errors;
	}


	public String getSql() {
		return sql.toString();
	}
	
}
