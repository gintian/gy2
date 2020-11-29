package com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;

import java.util.List;


/**
 * <p>Title:表间表达式语法分析器</p>
 * <p>Description:表间校验-表达式片段分析 </p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 6, 2006:8:51:16 AM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportSpaceExprAnalyse {

	private TnameBo tbo;	 //报表类
	int flag ;               //表间校验中的行/列标识
	private String rowCheckSQL;//行校验时返回的SQL
	private String reportPrefix;
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
	 * @param 报表信息类 
	 */
	public ReportSpaceExprAnalyse(TnameBo tbo ,String reportPrefix ,String whereSQL){
		this.tbo = tbo;
		this.reportPrefix = reportPrefix;
		this.whereSQL = whereSQL;
	}
	
	
	/**
	 * 获得表达式的一个因子
	 */
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
			while(Character.isDigit(fSource.charAt(nCurPos))){
				token += String.valueOf(fSource.charAt(nCurPos));
				nCurPos++;
				if(nCurPos == nFSourceLen){
					break;
				}
				
			}
			token_type=Constant.CONSTANT;
			return result;
		}
		
		result = false;
		return result;
	}
	
	/**
	 * 递归-加减下沉
	 * @return
	 */
	public boolean level0(){
		boolean result = false;
		
		if(!level1()){
			return result;
		}
		
		//如果因子是加或减
		while(tok == Constant.S_PLUS || tok == Constant.S_MINUS){
			SQL_SUM += token;
			//取出下一个因子
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
	
	/**
	 * 递归-乘除下沉
	 * @return
	 */
	public boolean level1(){
		boolean result = false;
		
		if(!level2()){
			return result;
		}
		
		//如果因子是乘或除
		while(tok == Constant.S_TIMES|| tok == Constant.S_DIVISION){
			SQL_SUM += token;
			//取出下一个因子
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
	
	/**
	 * 括号或数值或常量
	 * @return
	 */
	public boolean level2(){
		boolean result = false;
		
		//如果因子是左括号 并且因子类型是分割符
		if(tok == Constant.S_LPARENTHESIS && token_type == Constant.DELIMITER){ //括号
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
			//
			if(!this.primitive()){
				return result;
			}
		}
		result = true;
		return result;
	}
	
	/**
	 * 数值或常量 否则为非法字符
	 * @return
	 */
	public boolean primitive(){
		boolean result = false;
		int nToken = 0;
		if(token_type == Constant.INT){ //数值
			nToken = Integer.parseInt(token);
			//因子合法性检测
			if(nToken > nMaxFactorNum || nToken <1){
				this.setError(Constant.E_FACTORNOEXIST);
				return result;
			}
			if(flag == 2){ //列
				int col =Integer.parseInt((String)(tbo.getColMap().get(token))) +1 ;
				String temp = "C" + col;
				SQL_SUM += this.rowCheckSQL.replaceAll("CN" ,temp) ;
				
			}else if(flag == 1){//行
				int row =Integer.parseInt((String)(tbo.getRowMap().get(token))) +1 ;
				
				StringBuffer sql = new StringBuffer();
				sql.append("( select CN from ");
				sql.append(this.reportPrefix);
				sql.append(tbo.getTabid());
				sql.append(" where secid = ");
				sql.append(row);
				sql.append(this.whereSQL);
				sql.append(" )");
			
				SQL_SUM += Sql_switcher.isnull(sql.toString(),"0"); 
			}

			if(!this.getToken()){
				return result;
			}
			
		}else if(token_type == Constant.CONSTANT){//常量
			//如果因子长度等于1那么元素只有‘C’
			if(token.length() == 1){
				this.setError(Constant.E_LOSSCONSTANT);
				return result;
			}else if(token.endsWith(".")){
				this.setError(Constant.E_CONSTANT);
				return result;
			}else{
				nToken = Integer.parseInt(token.substring(1,token.length()));
				//DB中字段名
				SQL_SUM += token.substring(1,token.length());
				if(!this.getToken()){
					return result;
				}
			}
		}else{
			//非法字符
			this.putBack();
			this.setError(Constant.E_SYNTAX);
			return result;
		}
	
		result = true;
		return result;
	}
	
	/**
	 * 重设遍例索引,便于错误输出
	 * @return
	 */
	public boolean putBack(){
		nCurPos -= token.length();
		return true;
	}
	
	
	/**
	 * 
	 * @param  fSource       规范化的表达式
	 * @param  nMaxFactorNum 最大因子
	 * @param  flag          行列标记 1 行 2 列
	 * @param  rowCheckSQL   行校验后生成的SQL 模板
	 * 		                 行分析时传入""
	 * 		                 列分析时传入行分析结果 
	 * @return 表间校验所需要的SQL片段
	 */
	public boolean run(String fSource , int nMaxFactorNum , int flag ,String rowCheckSQL){
		boolean result = false;
		
		this.flag = flag;
		this.rowCheckSQL = rowCheckSQL;
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
	
	/**
	 * 判断排除行信息是否正确
	 * @param lexpr 表达式
	 * @param maxRow 最大行数
	 * @return
	 */
	public boolean checkPCMessage(String lexpr ,int maxRow){
		boolean b = true;
		if(ExprUtil.getEliminatesList(lexpr)== null){
			return true;
		}else{
			List list = ExprUtil.getEliminatesList(lexpr);
			for(int i=0; i< list.size(); i++){
				int num = Integer.parseInt((String)list.get(i));
				if(num > maxRow || num <= 0){
					b=false;
				}
			}
		}
		return b;
	}
	
	/**
	 * 设置错误信息
	 * @param num
	 */
	public void setError(int num){
		/*if(Constant.E_NOTEMPTY == num){
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
			this.cError ="不存在!";
		}else if(Constant.E_LOSSCONSTANT == num){
			this.cError ="未定义常数!";
		}*/
		
		
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
			this.cError =ResourceFactory.getProperty("constant.e_factornoexist");
		}else if(Constant.E_LOSSCONSTANT == num){
			this.cError =ResourceFactory.getProperty("constant.e_lossconstant");
		}else if(Constant.E_CONSTANT == num){
			this.cError = "定义常量错误!";
		}
		
		if(nCurPos <= 200){	
			this.cError = ResourceFactory.getProperty("rowcheckanalyse.zhong")+
			ExprUtil.getLNumber(fSource.substring(0,nCurPos)) + this.cError;	
		}else{
			this.cError = ResourceFactory.getProperty("rowcheckanalyse.zhong")+ 
			ExprUtil.getLNumber(fSource.substring(0,nCurPos)) + this.cError;			
		}
	}
	

	
	public String getCError() {
		return cError;
	}


	public String getSQL_Sum() {
		return SQL_SUM;
	}
}
