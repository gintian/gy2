
package com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse;

import com.hjsj.hrms.businessobject.report.SQL_Util;
import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import org.apache.commons.lang.StringUtils;

/**
 * <p>Title:表内校验-列校验</p>
 * <p>Description:分析一行中的数据</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-5-23</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ColCheckAnalyse {
	
	private TnameBo tbo;	 //报表类	
	private String reportPrefix;//报表前缀	
	private String lExpr;	 //左表达式
	private String operator; //运算符
	private String rExpr;    //右表达式
	private String pcMessage;//排除行 
	private String colCheckErrors;//列校验中表达式错误信息
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
	 * @param lExpr    左表达式
	 * @param operator 运算符
	 * @param rExpr    右表达式
	 * @param tbo 	   报表封装类
	 * @param reportPrefix 报表前缀 tb/tt_
	 * @param whereSQL     统计表数据限制条件
	 */
	public ColCheckAnalyse(String lExpr ,String operator ,String rExpr ,TnameBo tbo 
			                                 ,String reportPrefix ,String whereSQL){
		this.lExpr = lExpr;
		this.operator = operator;
		this.rExpr = rExpr;
		//排除信息：如1,2, 没有则为null
		this.pcMessage = ExprUtil.getEliminates(lExpr); 
		this.tbo = tbo;
		this.reportPrefix = reportPrefix;
		this.whereSQL = whereSQL;
	}

	
	
	/**
	 * 获得列校验分析表达式的描述
	 * @return 
	 */
	public String getExprInfo(){
		String temp = "";
		//如果没有排除列
		if(this.pcMessage == null || "".equals(this.pcMessage)){
			temp = "&nbsp;&nbsp;&nbsp;"+
			ResourceFactory.getProperty("colcheckanalyse.colcheck")+":" 
			+ ExprUtil.getLexpr(lExpr) + ExprUtil.getOperator(operator) + rExpr ;
		}else{
			temp = "&nbsp;&nbsp;&nbsp;"+
			ResourceFactory.getProperty("colcheckanalyse.colcheck")+":" + ExprUtil.getLexpr(lExpr) + 
			ExprUtil.getOperator(operator) + rExpr +
			ResourceFactory.getProperty("colcheckanalyse.paichu")+"("+this.pcMessage+")" 
			+ResourceFactory.getProperty("colcheckanalyse.row");
		}		
		return temp;
	}
	
	

	/**
	 * 获取表的列校验错误信息的SQL语句
	 * @param lExpr 
	 * @param operator
	 * @param rExpr
	 * @return 列校验所需的SQL
	 */
	public String getColCheckSQL(){

		/*	
		 列校验SQL语句：分析行
		 secid是排除行号包含甲
		 比如15=2+3+4
			SELECT secid , C15 AS 'LEXPR'  , C2+C3+C4  AS 'REXPR' FROM TB1  
			WHERE C15 <> C2+C3+C4 AND SECID  NOT IN( 3 ,4,5)
		 */	
		
		SQL_Util su = new SQL_Util();
		
		StringBuffer sql = new StringBuffer();//SQL语句
		StringBuffer errors = new StringBuffer();//错误信息
		
		
		//最大列
		int maxColNum = this.tbo.getMaxRowNumber(this.tbo.getColMap());
		
		//校验左表达式语法是否正确，并且得到左表达式SQL语句片段
		boolean b = this.run(ExprUtil.getExpr(this.lExpr),maxColNum);
		
		String le = this.SQL_SUM;
		if(le == null || "".equals(le)){
		}else{
			le = su.sqlSwitch(this.getSQL_Sum());//SQL
		}
		
		String lerror = this.getCError();//错误
		
		//校验右表达式语法是否正确，并且得到右表达式SQL语句片段
		boolean bb = this.run(ExprUtil.getExpr(this.rExpr),maxColNum);
		
		String re = this.SQL_SUM;
		if(le == null || "".equals(le)){
		}else{
			re = su.sqlSwitch(this.getSQL_Sum());//SQL
		}
		String rerror = this.getCError();//错误
		
		
		//错误信息合成
		if(lerror != null && !"".equals(lerror) && rerror != null && !"".equals(rerror)){
			errors.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ResourceFactory.getProperty("colcheckanalyse.lexpr")+lerror);
			errors.append("<br>");
			errors.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			errors.append(ResourceFactory.getProperty("colcheckanalyse.rexpr") + rerror);
		}else{
			if(lerror == null || "".equals(lerror) && rerror != null && !"".equals(rerror)){
				errors.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" 
						+ResourceFactory.getProperty("colcheckanalyse.rexpr")+rerror);
			}else if(lerror != null || !"".equals(lerror)&& rerror == null && "".equals(rerror)){
				errors.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" 
						+ResourceFactory.getProperty("colcheckanalyse.lexpr")+lerror);
			}
		}
		
		//如果表达式语法分析出错
		if(b== false || bb == false){
			//返回语法错误信息
			this.colCheckErrors = errors.toString();
		}else{
			//语法正确
			sql.append("select secid ,");
			sql.append(le);
			sql.append(" as lexpr , ");
			sql.append(re);
			sql.append(" as rexpr from ");
			sql.append(this.reportPrefix + tbo.getTabid()); //表名
			sql.append(" where ");
			sql.append(le);
			sql.append(ExprUtil.getReverseOperatorToSQL(ExprUtil.getOperator(operator)));
			sql.append(re);
			sql.append(this.whereSQL);
			
			//列校验中分析行信息：要把甲行和排除行去除
			//行是甲的信息
			String st = this.getPCMessages(tbo.getColSerialNo());	
			//排除行信息
			String pc =this.getEliminatesRowToSQL(this.pcMessage);
			
			if(StringUtils.isNotEmpty(st) || StringUtils.isNotEmpty(pc)){
				sql.append(" and secid not in ( ");
				if(StringUtils.isNotEmpty(st) && StringUtils.isNotEmpty(pc)){
					sql.append(st);
					if(st.charAt(st.length()-1)!=','){
						sql.append(" , ");
					}
					if(pc.charAt(pc.length()-1)==','){
						sql.append(pc.substring(0,pc.length()-1));//排除信息
					}else{
						sql.append(pc);//排除信息
					}
				}else{
					if(StringUtils.isNotEmpty(pc)){
						if(pc.charAt(pc.length()-1)==','){
							sql.append(pc.substring(0,pc.length()-1));//排除信息
						}else{
							sql.append(pc);//排除信息
						}
						
					}else if (StringUtils.isNotEmpty(st)){	
						if(st.charAt(st.length()-1)==','){
							sql.append(st.substring(0,st.length()-1));//排除信息
						}else{
							sql.append(st);//排除信息
						}
					}
				}
				sql.append(" )");
			}
		}//end if
		
		if(sql == null || "".equals(sql)){
			return null;
		}else{
			return sql.toString(); 
		}
		
	}
	
	/**
	 * 规范排除信息：甲
	 * @param str 报表的甲行信息-对应的是针对与二维数组的下标
	 * @return
	 */
	public String getPCMessages(String str){
		StringBuffer temp = new StringBuffer();		
		if(str ==  null||str.trim().length()==0){
			return null;
		}
		String [] t = str.split(",");		
		for(int i=0; i< t.length ; i++){
			//下标+1是表的secid字段的值
			temp.append(Integer.parseInt(t[i])+1);
			temp.append(",");
		}	
		return temp.toString();
	}

	
	
	/**
	 * 获取列校验中的排除行的SQL部分
	 * @param eRows  表达式中的排除行
	 * @return
	 */
	public String getEliminatesRowToSQL(String eRows ){
		if(eRows == null){
			return null;
		}
		StringBuffer psql = new StringBuffer();
		String [] temp = eRows.split(",");
		for(int i = 0 ; i< temp.length; i++){
			//排除行在数据库中的实际行号
			if(temp[i] == null){
			}else{
				if(this.tbo.getRowMap().get(temp[i])!=null){
				//	System.out.println("排除行=" + (String)this.tbo.getRowMap().get(temp[i]));
					//tbo.getRowMap()中存放的是针对于二维数组下标的数据，转化为tb表中的secid字段值需要加1
					int n = Integer.parseInt((String)tbo.getRowMap().get(temp[i]))+1;
					psql.append(n);
					psql.append(",");
				}
			}
		}
		return psql.toString();
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
			if(nToken > nMaxFactorNum || nToken <1 ){
				this.setError(Constant.E_FACTORNOEXIST);
				return result;
			}
			
			//列集合封装的是针对与二维数组数据从0开始的
			int col =Integer.parseInt((String)(tbo.getColMap().get(token)))+1;
			//防止数据为NULL
			SQL_SUM += Sql_switcher.isnull("C"+col ,"0");
			
			if(!this.getToken()){
				return result;
			}
			
		}else if(token_type == Constant.CONSTANT){
			//如果因子长度等于1那么元素只有‘C’
			if(token.length() == 1){
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
	
	
	/**
	 * 
	 * @param num
	 */
	public void setError(int num){
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
			this.cError ="列不存在!";
		}else if(Constant.E_LOSSCONSTANT == num){
			this.cError =ResourceFactory.getProperty("constant.e_lossconstant");
		}else if(Constant.E_CONSTANT == num){
			this.cError = "定义常量错误!";
		}else if(Constant.E_EliminatesError == num){
			this.cError =
				ResourceFactory.getProperty("colcheckanalyse.paichu")+
				ResourceFactory.getProperty("colcheckanalyse.row")+
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
			this.cError = "\'" + fSource.substring(nCurPos-200,nCurPos) +"\' "+
			ResourceFactory.getProperty("colcheckanalyse.zhong")+ ExprUtil.getLNumber(fSource.substring(0,nCurPos)) + this.cError;			
		}
	}
	
	
	
	public String getCError() {
		return cError;
	}


	public String getSQL_Sum() {
		return SQL_SUM;
	}

	public String getColCheckErrors() {
		return colCheckErrors;
	}
}
