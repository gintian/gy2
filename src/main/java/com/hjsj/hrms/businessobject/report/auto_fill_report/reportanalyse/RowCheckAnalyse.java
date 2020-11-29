
package com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse;

import com.hjsj.hrms.businessobject.report.SQL_Util;
import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;

/**
 * <p>Title:表内校验-行校验</p>
 * <p>Description:分析一列中的数据</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-5-23</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class RowCheckAnalyse {
	
	private TnameBo tbo;	 //报表类
	private String reportPrefix;//报表前缀
	private String lExpr;	 //左表达式
	private String operator; //运算符
	private String rExpr;    //右表达式
	private String pcMessage;//排除行 
	private String rowCheckErrors; //行校验错误信息
	private String whereSQL;
	
	private String fSource;  //表达式
	private String cError;   //错误信息
	private int nMaxFactorNum;//最大因子
	private String SQL_SUM;       //表达式结果	
	private int tok;          //因子具体类型
	private String token;     //因子
	private int token_type;   //因子基本类型
	private int nCurPos;      //字符定位标识
	private int nFSourceLen;  //表达式长度
	
	/**
	 * 构造器
	 * @param lExpr
	 * @param operator
	 * @param rExpr
	 * @param tbo
	 */
	public RowCheckAnalyse(String lExpr ,String operator ,String rExpr ,TnameBo tbo ,String reportPrefix , String whereSQL){
		this.lExpr = lExpr;
		this.operator = operator;
		this.rExpr = rExpr;
		this.pcMessage = ExprUtil.getEliminates(lExpr);
		this.tbo = tbo;
		this.reportPrefix = reportPrefix;
		this.whereSQL = whereSQL;

		
	}

	/**
	 * 获得行校验的SQL语句
	 */
	public String getRowCheckSQL(){
		SQL_Util su = new SQL_Util();
		
		StringBuffer sql = new StringBuffer();
		StringBuffer errors = new StringBuffer();
		
		int maxRowNum = this.tbo.getMaxRowNumber(this.tbo.getRowMap());
		
		boolean b = this.run(ExprUtil.getExpr(lExpr) , maxRowNum);
		String lesql = this.SQL_SUM;
		
		//System.out.println("lesql=" + lesql);
		
		if(lesql == null || "".equals(lesql)){
		}else{
			lesql = su.sqlSwitch(lesql);
		}
		
		String lerror = this.getCError();	
		
		boolean bb = this.run(ExprUtil.getExpr(rExpr), maxRowNum);
		
		//System.out.println("resql=" + this.getSQL_Sum());
		
		String resql = su.sqlSwitch(this.getSQL_Sum());
		String rerror = this.getCError();
		
		//错误语句合成
		if(lerror != null && !"".equals(lerror) && rerror != null && !"".equals(rerror)){
			errors.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ResourceFactory.getProperty("rowcheckanalyse.lexpr")+lerror);
			errors.append("<br>");
			errors.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			errors.append(ResourceFactory.getProperty("rowcheckanalyse.rexpr") + rerror);
		}else{
			if(lerror == null || "".equals(lerror) && rerror != null && !"".equals(rerror)){
				errors.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+ ResourceFactory.getProperty("rowcheckanalyse.rexpr")+rerror );
			}else if(lerror != null || !"".equals(lerror)&& rerror == null && "".equals(rerror)){
				errors.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+ResourceFactory.getProperty("rowcheckanalyse.lexpr") +lerror );
			}
		}
		
		//如果表达式语法错误
		if(b == false || bb == false){
			this.rowCheckErrors = errors.toString();
		}else{
			sql.append("select (");
			sql.append(lesql);
			sql.append(")as lexpr , (");
			sql.append(resql);
			sql.append(")as rexpr from ");
			sql.append(this.reportPrefix);
			sql.append(tbo.getTabid());
			sql.append(" where ");
			sql.append("( ");
			sql.append(lesql);
			sql.append(" ) ");
			sql.append(ExprUtil.getReverseOperatorToSQL(ExprUtil.getOperator(operator)));
			sql.append("( ");
			sql.append(resql);
			sql.append(" ) ");
			sql.append(this.whereSQL);
		}//end if
		
		if(sql == null || "".equals(sql)){
			return null;
		}else{
			return sql.toString(); 
		}
	}

	/**
	 * 获得行校验分析表达式的描述
	 * @return 
	 */
	public String getExprInfo(){
		String temp = "";
		if(this.pcMessage == null || "".equals(this.pcMessage)){
			temp = "&nbsp;&nbsp;&nbsp;&nbsp;"+
			ResourceFactory.getProperty("rowcheckanalyse.rowcheck")+":" + ExprUtil.getLexpr(lExpr) + ExprUtil.getOperator(operator) + rExpr ;
		}else{
			temp = "&nbsp;&nbsp;&nbsp;&nbsp;"+
					ResourceFactory.getProperty("rowcheckanalyse.rowcheck")+":" 
					+ ExprUtil.getLexpr(lExpr) + ExprUtil.getOperator(operator) + rExpr
					+ResourceFactory.getProperty("rowcheckanalyse.paichu")+"("+this.pcMessage+")" +
					ResourceFactory.getProperty("rowcheckanalyse.col");
		}		
		return temp;
	}
	
	

	//获得一个基本因子
	public boolean getToken(){
		boolean result = true;
		tok = 0;
		token = "";
		token_type = 0;
		
		//如果表达式长度为0 即：空表达式
		if(nFSourceLen == 0 ){
			result = false;
			return result;
		}
		
		//如果当前 字符位=表达式长度那么结束即：字符已取完
		if(nCurPos == nFSourceLen ){
			tok = Constant.S_FINISHED;
			token_type = Constant.DELIMITER;
			return result;
		}
		
		//空白字符处理
		while(Character.isSpaceChar(fSource.charAt(nCurPos))){
			nCurPos++;
		}
		
		//如果当前 字符位=表达式长度那么结束即：字符已取完
		if(nCurPos == nFSourceLen){
			tok = Constant.S_FINISHED;
			token_type = Constant.DELIMITER;
			return result;
		}
		
		//分割符处理
		if("+-*/()".indexOf(fSource.charAt(nCurPos))!=-1){
			char c = fSource.charAt(nCurPos);
			token=String.valueOf(c);
			switch (c){
				case '+':
					tok = Constant.S_PLUS;
					break;
				case '-':
					tok = Constant.S_MINUS;
					break;
				case '*':
					tok = Constant.S_TIMES;
					break;
				case '/':
					tok = Constant.S_DIVISION;
					break;
				case '(':
					tok = Constant.S_LPARENTHESIS;
					break;
				case ')':
					tok = Constant.S_RPARENTHESIS;
					break;
			}
			token_type = Constant.DELIMITER;
			nCurPos++;
			return result;
		}
		
		//数值处理
		if(Character.isDigit(fSource.charAt(nCurPos))){
			char c = fSource.charAt(nCurPos);
			token = String.valueOf(c);
			nCurPos++;
			//需要位置标识验证
			if(nCurPos == nFSourceLen){
				
			}else{
				while(Character.isDigit(fSource.charAt(nCurPos))){
					token += String.valueOf(fSource.charAt(nCurPos));
					nCurPos++;
					if(nCurPos == nFSourceLen){
						break;
					}
				}
			}
			token_type = Constant.INT;
			return result;
		}
		
		//处理常量
		if(fSource.charAt(nCurPos)=='C' || fSource.charAt(nCurPos)=='c'){
			token = String.valueOf(fSource.charAt(nCurPos));
			nCurPos++;
			if(nCurPos == nFSourceLen){
				
			}else{
				while(Character.isDigit(fSource.charAt(nCurPos))|| fSource.charAt(nCurPos) == '.'){
					token += String.valueOf(fSource.charAt(nCurPos));
					nCurPos++;
					if(nCurPos == nFSourceLen){
						break;
					}
					
				}
				
			}
			
			token_type=Constant.CONSTANT;
			return result;
		}
		
		result = false;
		nCurPos++;
		this.setError(Constant.E_ErrorNumber);//非法字符出现
		return result;
	}
	
	public boolean level0(){
		boolean result = false;
		
		if(!level1()){
			return result;
		}
		
		//如果因子是加或减
		while(tok == Constant.S_PLUS || tok == Constant.S_MINUS){
			SQL_SUM += token;
			if(!this.getToken()){
				return result;
			}
			if(!level1()){
				return result;
			}
		}
		
		result = true;
		return result;
	}
	
	public boolean level1(){
		boolean result = false;
		
		if(!level2()){
			return result;
		}
		
		//如果因子是乘或除
		while(tok == Constant.S_TIMES|| tok == Constant.S_DIVISION){
			SQL_SUM += token;
			if(!this.getToken()){
				return result;
			}
			if(!level2()){
				return result;
			}
		}
		result = true;
		return result;
	}
	
	public boolean level2(){
		boolean result = false;
		
		//如果因子是左括号 并且因子类型是分割符
		if(tok == Constant.S_LPARENTHESIS && token_type == Constant.DELIMITER){
			SQL_SUM += token;
			if(!this.getToken()){
				return result;
			}
			if(!level0()){
				return result;
			}
			SQL_SUM += token;
			if(tok != Constant.S_RPARENTHESIS){
				this.putBack();
				this.setError(Constant.E_LOSSRPARENTHESE);
				return result;
			}
			if(!this.getToken()){
				return result;
			}
			
		}else if(tok == Constant.S_MINUS && token_type == Constant.DELIMITER){//负号处理
			//负号后面只能是常量，括号,数值
			SQL_SUM += token;
			if(SQL_SUM.endsWith("--")){
				SQL_SUM = SQL_SUM.substring(0,SQL_SUM.length()-2);
				SQL_SUM += "+";
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
				this.setError(Constant.E_SYNTAX);
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
	
	public boolean primitive(){
		boolean result = false;
		int nToken = 0;
		if(token_type == Constant.INT){
			nToken = Integer.parseInt(token);
			//因子合法性检测
			if(nToken > nMaxFactorNum || nToken < 1){
				this.setError(Constant.E_FACTORNOEXIST);
				return result;
			}	
			//secid从1开始所以加1
			int row =Integer.parseInt((String)(tbo.getRowMap().get(token)))+1 ;
			
			StringBuffer sql = new StringBuffer();
			sql.append("( select CN from ");
			sql.append(this.reportPrefix);
			sql.append(tbo.getTabid());
			sql.append(" where secid = ");
			sql.append(row);
			sql.append(this.whereSQL);
			sql.append(" )");
			
			SQL_SUM += Sql_switcher.isnull(sql.toString() ,"0");
			
			if(!this.getToken()){
				return result;
			}
			
		}else if(token_type == Constant.CONSTANT){
			//如果因子长度等于1那么元素只有‘C’
			if(token.length() == 1  ){
				this.setError(Constant.E_LOSSCONSTANT);
				return result;
			}else if(token.endsWith(".")){
				this.setError(Constant.E_CONSTANT);
				return result;
			}else{
				//nToken = Integer.parseInt(token.substring(1,token.length()));
				//DB中字段名
				SQL_SUM += token.substring(1,token.length());
				if(!this.getToken()){
					return result;
				}
			}
		}else{
			this.putBack();
			this.setError(Constant.E_SYNTAX);
			return result;
		}
	
		result = true;
		return result;
	}
	
	public boolean putBack(){
		nCurPos -= token.length();
		return true;
	}
	
	/**
	 * 验证公式是否正确
	 * 获取SQL_SUM
	 * @param fSource       表达式
	 * @param nMaxFactorNum 最大因子
	 * @return true/false
	 */
	public boolean run(String fSource , int nMaxFactorNum){
		boolean result = false;
		
		this.fSource = fSource.trim();
		this.nMaxFactorNum = nMaxFactorNum;
		this.nFSourceLen = fSource.length();
		this.nCurPos = 0;
		this.SQL_SUM = "";
		this.cError = "";
				
		if(!this.getToken()){ //如果取因子失败
			this.setError(Constant.E_NOTEMPTY); //空表达式
			return result;
		}
		
		if(! this.level0()){
			return result;
		}
		
		this.putBack();
		
		if(nCurPos != nFSourceLen ){
			this.setError(Constant.E_SYNTAX);
			return result;
		}
		result = true;
		return result;
	}
	
	
	public void setError(int num){
		
/*		if(Constant.E_NOTEMPTY == num){
			this.cError ="表达式不能为空!";
		}else if(Constant.E_LOSSLPARENTHESE == num){
			this.cError ="此处缺少左括号!";
		}else if(Constant.E_LOSSRPARENTHESE == num){
			this.cError ="此处缺少右括号!";
		}else if(Constant.E_SYNTAX == num){
			this.cError ="此处语法错!";
		}else if(Constant.E_MUSTBEINTEGER == num){
			this.cError ="此处必须是整型!";
		}else if(Constant.E_FACTORNOEXIST == num){
			this.cError ="列"+"不存在!";
		}else if(Constant.E_LOSSCONSTANT == num){
			this.cError ="未定义常数!";
		}else if(Constant.E_EliminatesError == num){
			this.cError ="排除"+"列"+"不存在!";
		}else if(Constant.E_ErrorNumber == num){
			this.cError ="无效字符!";
		}
		*/
		if(Constant.E_NOTEMPTY == num){
			this.cError =ResourceFactory.getProperty("constant.e_notempty");
		}else if(Constant.E_LOSSLPARENTHESE == num){
			this.cError =ResourceFactory.getProperty("constant.e_losslparenthese");
		}else if(Constant.E_LOSSRPARENTHESE == num){
			this.cError =ResourceFactory.getProperty("constant.e_lossrparenthese");
		}else if(Constant.E_SYNTAX == num){
			this.cError =ResourceFactory.getProperty("constant.e_syntax");
		}else if(Constant.E_MUSTBEINTEGER == num){
			this.cError =ResourceFactory.getProperty("constant.e_mustbeinteger");
		}else if(Constant.E_FACTORNOEXIST == num){
			this.cError ="行" + //ResourceFactory.getProperty("rowcheckanalyse.col")+
				ResourceFactory.getProperty("constant.e_factornoexist");
		}else if(Constant.E_LOSSCONSTANT == num){
			this.cError =ResourceFactory.getProperty("constant.e_lossconstant");
		}else if(Constant.E_CONSTANT == num){
			this.cError = "定义常量错误!";
		}else if(Constant.E_EliminatesError == num){
			this.cError =
				ResourceFactory.getProperty("rowcheckanalyse.paichu")+
				ResourceFactory.getProperty("rowcheckanalyse.col")+
				ResourceFactory.getProperty("constant.e_factornoexist");		
		}else if(Constant.E_ErrorNumber == num){
			this.cError =ResourceFactory.getProperty("constant.e_errornumber");
		}
		
		if(nCurPos <= 200){
			if(fSource.substring(0,nCurPos)==null || "".equals(fSource.substring(0,nCurPos))){
				this.cError = ResourceFactory.getProperty("colcheckanalyse.zhong")+ ExprUtil.getLNumber(fSource.substring(0,nCurPos)) + this.cError;
			}else{
				this.cError = "\'" + fSource.substring(0,nCurPos) +"\' "+
				ResourceFactory.getProperty("colcheckanalyse.zhong")+ ExprUtil.getLNumber(fSource.substring(0,nCurPos)) + this.cError;
			}
		}else{
			this.cError = "\"" + fSource.substring(nCurPos-200,nCurPos) +"\" "+
			ResourceFactory.getProperty("rowcheckanalyse.zhong")+ ExprUtil.getLNumber(fSource.substring(0,nCurPos)) + this.cError;			
		}
	}
	
	
	
	public String getCError() {
		return cError;
	}


	public String getSQL_Sum() {
		return SQL_SUM;
	}
	

	public String getRowCheckErrors() {
		return rowCheckErrors;
	}
}
