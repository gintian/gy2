package com.hjsj.hrms.businessobject.report.formulaAnalyse;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse.Constant;
import com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse.ExprUtil;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;

public class ReportSpaceRExprFormulaAnalyse {
	
	private Connection conn; //DB连接
	private int flag;        //行列分析标记
	private StringBuffer sql =null; //存放SQL片段
	private int c; //行计算公式中的传入的列参数
	private String reportPrefix;
	private String expr; //表间计算公式右表达式 C10.5 + [66:1..3+C100]
	
	private int tok;
	private String token;
	private int token_type;
	private int n;
	private int exprLen;
	private String whereSQL;
	private UserView userView;
	private String errors;//右表达式的片段分析的错误信息
	private int tabid;
	private TnameBo tbo;  //左表达式的TnameBo
	/**
	 * 表间计算公式分析
	 * @param conn 数据库连接
	 * @param list 表间行分析时：相关列信息 表间列分析时：相关行信息
	 * @param flag  1行/2列分析标识
	 * @param reportPrefix 操作报表的前缀tb / tt_
	 * @param whereSQL  操作表的限制条件 根据用户名或填报单位编码获得操作的数据
	 */
	public ReportSpaceRExprFormulaAnalyse(Connection conn  , int flag , String reportPrefix ,String whereSQL){
		this.conn = conn;
		this.flag = flag;
		this.reportPrefix = reportPrefix;
		this.whereSQL = whereSQL;
	}
	
	/**
	 * 获得一个因子
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
				case '[':
					this.tok = Constant.S_L;
					break;
				case ']':
					this.tok = Constant.S_R;
					break;
				
			}
			this.token_type = Constant.DELIMITER;
			n++;
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
	 * 加减
	 * @return
	 * @throws GeneralException 
	 */
	public boolean level0() throws GeneralException{
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
	 * 乘除
	 * @return
	 * @throws GeneralException 
	 */
	public boolean level1() throws GeneralException{
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
	 * 括号处理[]()
	 * @return
	 * @throws GeneralException 
	 */
	public boolean level2() throws GeneralException{
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
			
			//没有右括号
			if(this.tok != Constant.S_RPARENTHESIS){
				this.putBack();
				this.setErrors(Constant.E_LOSSRPARENTHESE);
				return result;
			}
			
			this.sql.append(this.token);
			
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
			
		}else if(this.tok == Constant.S_L && this.token_type == Constant.DELIMITER){//所取的字符是左中括号
			//[号之前必须有运算符
			//System.out.println("n=" + n);
			//String temp = this.expr.substring(0,this.n);
			//System.out.println("temp=" + temp);
			
		/*	if(this.n >1){				
				char c = this.expr.charAt(this.n-2);
				System.out.println("c= " + c);
				if("*+-/".indexOf(c)==-1 && c != '('){
					this.setErrors(Constant.E_SYNTAX);
					return result;
				}
				if(c == '('){
					int nn = this.n-2;
					while(nn >0){
						c = this.expr.charAt(nn);
						if(c != '('){
							break;
						}
						nn--;	
					}
					if("*+-/".indexOf(c)== -1){
						this.setErrors(Constant.E_SYNTAX);
						return result;
					}
				}
			}*/
			
			//截取[]之间的字符串
			int nn = this.expr.indexOf("]",this.n);
			if(nn==-1){
				//语法错误
				//this.errors="语法错误！";
				this.setErrors(Constant.E_SYNTAX);
				return result;
			}else{
				String rexpr = this.expr.substring(n,nn);

				if(rexpr.matches("\\d+\\:([^:\\[])+")){//是否格式合法
					
					int flag1 = rexpr.indexOf(":");
					
					String tabid = rexpr.substring(0,flag1);
					String r_expr = rexpr.substring(flag1+1,rexpr.length());
	
					if(this.isExistTable(this.reportPrefix,tabid)){	
						//分析报表是否生成统计表
						TnameBo tbo = new TnameBo(this.conn , tabid);
						
						ReportSpaceRExprSegmentAnalyse rsresa = new ReportSpaceRExprSegmentAnalyse(tbo , this.flag ,tabid ,this.reportPrefix,this.whereSQL);
						
						if(this.flag == 1){//行
							boolean b = rsresa.run(r_expr,tbo.getMaxRowNumber(tbo.getRowMap()), this.c);
							if(!b){
								this.errors = rexpr +rsresa.getErrors();
								return result;
							}else{
								this.sql.append(rsresa.getSql());
							}
						}else if(this.flag ==2){//列
							boolean b = rsresa.run(r_expr,tbo.getMaxRowNumber(tbo.getColMap()) , this.c);
							if(!b){
								this.errors = rexpr + rsresa.getErrors();
								return result;
							}else{
								this.sql.append(rsresa.getSql());
							}
						}
					}else{
						//未生成统计表错误！
						this.errors =this.reportPrefix + tabid + "未生成统计表错误！";
						return result;
						
					}//end 是否生成统计表
					
				}else{
					//语法错误
					//this.errors="语法错误！";
					this.setErrors(Constant.E_SYNTAX);
					return result;
				}//END 语法错误
						
				this.n = this.n + rexpr.length();

			}
			this.n = nn+1;
			if(! this.getToken()){
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
	 * 常量处理
	 * @return
	 */
	public boolean primitive(){
		boolean result = false;
		
		if(this.token_type == Constant.CONSTANT){
			
			if(this.token.length()==1){//只有C
				this.setErrors(Constant.E_LOSSCONSTANT);
				return result;
			}else if(this.token.endsWith(".")){//常量语法错误
				this.setErrors(Constant.E_SYNTAX);
				return result ;
			}else{
				String temp = this.token.substring(1,this.token.length());
				
				this.sql.append(temp);
				
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
	 * 重新定向下标
	 * @return
	 */
	public boolean putBack(){
		this.n -= token.length();
		return true;
	}
	
	/**
	 * 执行语法分析
	 * @param expr 表达式
	 * @param c    表间行分析时的列信息
	 * @return
	 * @throws GeneralException 
	 */
	public boolean run(String expr ,int c) throws GeneralException{
		
		this.sql = new StringBuffer();
		boolean result = false;
		expr = expr.replace("m", "M");
		expr = expr.replace("q", "Q");
		expr=expr.replace("M+", "CM+");
		expr=expr.replace("M-", "CM-");
		expr=expr.replace("M*", "CM*");
		expr=expr.replace("M/", "CM/");
		expr=expr.replace("+M", "+CM");
		expr=expr.replace("-M", "-CM");
		expr=expr.replace("*M", "*CM");
		expr=expr.replace("/M", "/CM");
		expr=expr.replace("CC", "C");
		expr=expr.replace("Q+", "CQ+");
		expr=expr.replace("Q-", "CQ-");
		expr=expr.replace("Q*", "CQ*");
		expr=expr.replace("Q/", "CQ/");
		expr=expr.replace("+Q", "+CQ");
		expr=expr.replace("-Q", "-CQ");
		expr=expr.replace("*Q", "*CQ");
		expr=expr.replace("/Q", "/CQ");
		expr=expr.replace("CC", "C");
		//System.out.println(expr);
		expr=parseFormula(expr);
		
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
	
	/**
	 * 设置错误信息
	 * @param num
	 */
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
			this.errors =ResourceFactory.getProperty("colcheckanalyse.row")+
				ResourceFactory.getProperty("constant.e_factornoexist");
		}else if(Constant.E_LOSSCONSTANT == num){
			this.errors =ResourceFactory.getProperty("constant.e_lossconstant");
		}else if(Constant.E_EliminatesError == num){
			this.errors =
				ResourceFactory.getProperty("colcheckanalyse.paichu")+
				ResourceFactory.getProperty("colcheckanalyse.row")+
				ResourceFactory.getProperty("constant.e_factornoexist");		
		}else if(Constant.E_ErrorNumber == num){
			this.errors =ResourceFactory.getProperty("constant.e_errornumber");
		}
		if(n <= 200){
			this.errors = "\'" + expr.substring(0,n) +"\' "+
			ResourceFactory.getProperty("colcheckanalyse.zhong")+ ExprUtil.getLNumber(expr.substring(0,n)) + this.errors;
		}else{
			this.errors = "\'" + expr.substring(n-200,n) +"\' "+
			ResourceFactory.getProperty("colcheckanalyse.zhong")+ ExprUtil.getLNumber(expr.substring(0,n)) + this.errors;			
		}
	}
	
	/**
	 * 获得语法错误信息
	 * @return
	 */
	public String getErrors() {
		return errors;
	}
	
	
	/**
	 * 获得语法正确后的SQL语句
	 * @return
	 */
	public String getSql() {
		return sql.toString();
	}
	
	
	/**
	 * 判断报表是否存在
	 * @param tbo
	 * @param tabid
	 * @return
	 * @throws GeneralException 
	 */
	public boolean isExistReport(TnameBo tbo , String tabid) throws GeneralException{
		boolean b = false;
		if("tb".equals(this.reportPrefix)){
			b = tbo.isExistTable(tabid);
		}else if("tt_".equals(this.reportPrefix)){
			//没有则创建表
			tbo.getTgridBo().execute_TT_table(tabid,tbo.getColInfoList().size());
			b=true;
		}
		return b;
	}
	
	/**
	 * 判断统计结果表是否存在
	 * @param reportPrefix 报表前缀如tb tt_
	 * @param tabid  报表ID
	 * @return
	 */
	public boolean isExistTable(String reportPrefix ,String tabid ){
		boolean b = false;
		DbWizard dbWizard=new DbWizard(this.conn);
		Table table=new Table(reportPrefix+tabid);
		if(dbWizard.isExistTable(table.getName(),false)){
			b= true;
		}
		return b;
	}
	// 处理字符
	public String  parseFormula(String expr) {
		RowSet rs= null;
		String temp1 ="";
		String temp2 ="";
		if (expr.indexOf("M")!=-1||expr.indexOf("Q")!=-1) {
		
					if(this.tbo!=null){
						this.tbo.setUserName(this.userView.getUserName());
						String appdate = this.tbo.getOwnerDate(""+tabid);//左表达式的tabid
					int app = 0;
					if (appdate != null && appdate.length() > 7) {
                        app = Integer.parseInt(appdate.substring(5, 7));
                    }
					if (expr.indexOf("M")!=-1&&app != 0) {
						temp1 = ""+app;
						expr=expr.replace("M", temp1);
					}
					if(expr.indexOf("Q")!=-1&&app != 0){
						if(0<app&&app<4) {
                            temp2="1";
                        }
						if(3<app&&app<7) {
                            temp2="2";
                        }
						if(6<app&&app<10) {
                            temp2="3";
                        }
						if(9<app&&app<13) {
                            temp2="4";
                        }
						expr=expr.replace("Q", temp2);
					}
					}
		}
		return expr;
	}
	public UserView getUserView() {
		return userView;
	}
	public void setUserView(UserView userView) {
		this.userView = userView;
	}

	public int getTabid() {
		return tabid;
	}

	public TnameBo getTbo() {
		return tbo;
	}

	public void setTbo(TnameBo tbo) {
		this.tbo = tbo;
	}

	public void setTabid(int tabid) {
		this.tabid = tabid;
	}
	
}
