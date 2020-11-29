package com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * <p>Title:表间校验表达式语法分析 </p>
 * <p>Description:分析表间表达式语法是否正确,返回SQL语句
 *   	适用于特定表间校验和普通表间校验
 * </p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 6, 2006:8:51:16 AM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportSpaceAnalyse {
	
	private Connection conn;	//数据库连接
	private TnameBo tbo;	    //报表类
	private String reportPrefix;//报表前缀
	private String whereSQL;
	
	//特定表间校验使用
	private String tabid = null;	//特定报表表号
	private double [][]reportValues;//特定报表数据
	
	private String fSource;              //表达式
	private String cError = null;        //错误信息
	private String SQL_SUM = null;       //表达式结果	
	
	private int tok;          //因子具体类型
	private String token;     //因子
	private int token_type;   //因子基本类型
	private int nCurPos;      //字符定位标识
	private int nFSourceLen;  //表达式长度
	private UserView userView;
	
	/**
	 * 表间校验语法分析构造器-自动生成模块
	 * @param conn
	 * @param userName
	 * @param userID
	 */
	public ReportSpaceAnalyse(Connection conn , String reportPrefix ,String whereSQL){
		this.conn = conn;
		this.reportPrefix = reportPrefix;
		this.whereSQL = whereSQL;
	}
	
	/**
	 * 特定表间校验构造器-报表编辑中/表间即时校验（传入二维数组数据）
	 * @param conn         数据库连接
	 * @param userName     用户名
	 * @param userID       用户ID
	 * @param tabid        特定报表表号
	 * @param reportValues 报表数据
	 */
	public ReportSpaceAnalyse(Connection conn ,String tabid , double [][] reportValues , String reportPrefix ,String whereSQL){
		this.conn = conn;
		this.tabid = tabid;
		this.reportValues = reportValues;
		this.reportPrefix = reportPrefix;
		this.whereSQL = whereSQL;
	}
	
	/**
	 * 获得一个基本因子 
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
		if("+-*/()[]：".indexOf(fSource.charAt(nCurPos))!=-1){
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
				case '[':
					tok = Constant.S_L;
					break;
				case ']':
					tok = Constant.S_R;
					break;
			}
			token_type = Constant.DELIMITER;
			nCurPos++;
			return result;
		}
		
		//处理常量
		if(fSource.charAt(nCurPos)=='C' || fSource.charAt(nCurPos)=='c'){
			token = String.valueOf(fSource.charAt(nCurPos));
			nCurPos++;
			
			//如果当前 字符位=表达式长度那么结束即：字符已取完
			if(nCurPos == nFSourceLen || !Character.isDigit(fSource.charAt(nCurPos))){
				result = false;
				this.setError(Constant.E_ErrorNumber);//非法字符出现
				return result;
			}
			
			while(Character.isDigit(fSource.charAt(nCurPos)) || fSource.charAt(nCurPos)=='.'){
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
	
	public boolean level0() throws GeneralException{
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
	
	public boolean level1() throws GeneralException{
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
	
	public boolean level2() throws GeneralException{
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
			//负号后面只能是常量，括号,中括号
			SQL_SUM += token;
			if(SQL_SUM.endsWith("--")){
				SQL_SUM = SQL_SUM.substring(0,SQL_SUM.length()-2);
				SQL_SUM += "+";
			}
			if(!this.getToken()){
				return result;
			}	
			if(tok == Constant.S_LPARENTHESIS || token_type==Constant.CONSTANT || token_type==Constant.S_L){
				if(!level0()){
					return result;
				}
			}else{
				this.putBack();
				this.setError(Constant.E_SYNTAX);
				return result;
			}
			
		}else if(tok == Constant.S_L && token_type == Constant.DELIMITER){//中括号	
			//获取[]之间的表达式
			StringBuffer temp = new StringBuffer();
			for(int i= nCurPos ; i< this.nFSourceLen ; i++){
				if(this.fSource.charAt(i)==']'){
					break;
				}
				temp.append(this.fSource.charAt(i));
				nCurPos++;
			}
			
			if(nCurPos == nFSourceLen ){//说明[表号：列：行]缺损语法错误
				tok = Constant.S_FINISHED;
				token_type = Constant.DELIMITER;
				this.setError(Constant.E_SYNTAX);
				return result;
			}	
			nCurPos++;
			
			//验证规则是否成立[表号：列：行]
			if(this.checkRepostString(temp.toString())){
				//拆分字符串
				String expr = temp.toString();
				int n = expr.indexOf(":" , 0);
				String tabid1 = expr.substring(0,n);  //报表表号
				int n1 = expr.indexOf(":" , n+1);
				String colExpr = ExprUtil.getExpr(expr.substring(n+1,n1)); //报表列号信息表达式
				String rowExpr = ExprUtil.getExpr(expr.substring(n1+1,expr.length())); //报表行号信息表达式
						
				//特定表的表间校验表达式中的表是当前表用数组中数值替换
				if(this.tabid != null && tabid1.equals(this.tabid)){
					//实例化报表类
					this.tbo = new TnameBo(this.conn,tabid1);				
					ReportSpaceExprAnalyse rsea = new ReportSpaceExprAnalyse(this.tbo,this.reportPrefix ,this.whereSQL);
					//获得最大行数,实际行去除甲行的行个数
					int maxRowNum = this.tbo.getMaxRowNumber(this.tbo.getRowMap());
					//执行行分析
					boolean b = rsea.run(rowExpr,maxRowNum,1,"");
					if(!b){					
						this.cError=rsea.getCError();
						return result;
					}
					
					//最大列数,去除了编号实际的列个数
					int maxColNum = this.tbo.getMaxRowNumber(this.tbo.getColMap());
					//执行列分析
					boolean bb = rsea.run(colExpr,maxColNum,2,"");
					if(!bb){
						this.cError=rsea.getCError();
						return result;
					}	
					
					//语法正确
					if(b == true && bb == true){
						//现有数据合成表达式-->SQL语句片段
						
						//System.out.println("SQLPP=" + this.getTempSQL(rowExpr , colExpr) );
						
						this.SQL_SUM +=this.getTempSQL(rowExpr , colExpr);
					}
				
				}else{//通用表的表间校验
					//if(this.isExistReport(this.tbo,tabid1)){
					if(this.isExistTable(this.reportPrefix ,tabid1)){	
						//表号是否存在,即是否生成统计表					
						this.tbo = new TnameBo(this.conn,tabid1);	
						StringBuffer sql = new StringBuffer();
						sql.append("( ");	
						
						ReportSpaceExprAnalyse rsea = new ReportSpaceExprAnalyse(this.tbo , this.reportPrefix , this.whereSQL);
						
						//获得最大行数,实际行去除甲行的行个数
						int maxRowNum = this.tbo.getMaxRowNumber(this.tbo.getRowMap());
						
						//执行行分析-得到表间校验行中模板
						boolean b = rsea.run(rowExpr,maxRowNum,1,"");
						
						String tempsql = "";
						if(b){//表达式验证正确
							//SQL语句模板
							tempsql = rsea.getSQL_Sum();
						}else{
							this.cError=rsea.getCError();
							return result;
						}
						
						//最大列数,去除了编号实际的列个数
						int maxColNum = this.tbo.getMaxRowNumber(this.tbo.getColMap());
						//执行列分析
						boolean bb = rsea.run(colExpr,maxColNum,2,tempsql);
						if(bb){//表达式验证正确
							//得到列替换行SQL模板的SQL片段
							sql.append(rsea.getSQL_Sum());	
						}else{
							this.cError=rsea.getCError();
							return result;
						}	
						sql.append(")");
						this.SQL_SUM +=sql.toString();
						
					}else{//表号错误
						this.cError=this.reportPrefix.toUpperCase() + tabid1+"==>未生成统计表！";
						return result;
					}
					
				}

			}else{
				//语法错误
				this.setError(Constant.E_SYNTAX);
				return result;
			}
			
			if(!this.getToken()){
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
		if(token_type == Constant.CONSTANT){
			//如果因子长度等于1那么元素只有‘C’
			if(token.length() == 1){
				this.setError(Constant.E_LOSSCONSTANT);
				return result;
			}else if(token.endsWith(".")){
				this.setError(Constant.E_CONSTANT);
				return result;
			}else{
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
	 * @throws GeneralException 
	 */
	public boolean run(String fSource ) throws GeneralException{
		boolean result = false;
		fSource = fSource.replace("m", "M");
		fSource = fSource.replace("q", "Q");
		fSource=fSource.replace("M+", "CM+");
		fSource=fSource.replace("M-", "CM-");
		fSource=fSource.replace("M*", "CM*");
		fSource=fSource.replace("M/", "CM/");
		fSource=fSource.replace("+M", "+CM");
		fSource=fSource.replace("-M", "-CM");
		fSource=fSource.replace("*M", "*CM");
		fSource=fSource.replace("/M", "/CM");
		fSource=fSource.replace("CC", "C");
		fSource=fSource.replace("Q+", "CQ+");
		fSource=fSource.replace("Q-", "CQ-");
		fSource=fSource.replace("Q*", "CQ*");
		fSource=fSource.replace("Q/", "CQ/");
		fSource=fSource.replace("+Q", "+CQ");
		fSource=fSource.replace("-Q", "-CQ");
		fSource=fSource.replace("*Q", "*CQ");
		fSource=fSource.replace("/Q", "/CQ");
		fSource=fSource.replace("CC", "C");
		//System.out.println(fSource);
		fSource=parseFormula(fSource);
		this.fSource = fSource.trim(); //校验的表达式
		this.nFSourceLen = fSource.length();//表达式长度
		this.nCurPos = 0; //遍例索引
		this.SQL_SUM = ""; //
		this.cError = ""; //错误信息
		
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
			this.cError =ResourceFactory.getProperty("colcheckanalyse.row")+
				ResourceFactory.getProperty("constant.e_factornoexist");
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

	/**
	 * 判断表间效验[: :]格式是否正确
	 * @param str
	 * @return
	 */
	public boolean checkRepostString(String str){
		 Pattern p = Pattern.compile("\\d+\\:([^:\\[])+\\:([^:\\[])+");
		 Matcher m = p.matcher(str);
		 boolean b = m.matches();
		 return b;
	}
	
	
	/**
	 * [表号：列：行]
	 * @param rowExpr 规范的行表达式
	 * @param colExpr 规范的列表达式
	 * @return
	 */
	public String getTempSQL(String rowExpr , String colExpr ){
		StringBuffer sql = new StringBuffer();
		for(int i = 0 ; i< colExpr.length(); i++){//遍例列表达式
			//获得列数值
			if(Character.isDigit(colExpr.charAt(i))){
				StringBuffer num = new StringBuffer();
				num.append(colExpr.charAt(i));
				for(int j = i+1; j< colExpr.length(); j++){
					if(Character.isDigit(colExpr.charAt(j))){
						num.append(colExpr.charAt(j));
						if(j == colExpr.length()-1){
							i=j;
						}
					}else{
						i = j-1;
						break;
					}
				}
				StringBuffer t = new StringBuffer();
				t.append("(");
				for(int k = 0 ; k< rowExpr.length(); k++){//遍例行表达式
					//获得列数值
					if(Character.isDigit(rowExpr.charAt(k))){
						StringBuffer num1 = new StringBuffer();
						num1.append(rowExpr.charAt(k));
						for(int n = k+1; n< rowExpr.length(); n++){
							if(Character.isDigit(rowExpr.charAt(n))){
								num1.append(rowExpr.charAt(n));
								if(n == rowExpr.length()-1){
									k=n;
								}
							}else{
								k = n-1;
								break;
							}
						}
						String rowNum = num1.toString();
						String colNum = num.toString();
						
						int row = Integer.parseInt((String)(this.tbo.getRowMap().get(rowNum)));
						int col = Integer.parseInt((String)(this.tbo.getColMap().get(colNum)));
						
						t.append(this.reportValues[row][col]);
						
					}else if(rowExpr.charAt(k)=='c' || rowExpr.charAt(k)=='C'){//列表达式中的常量
						StringBuffer num2 = new StringBuffer();
						for(int m = k+1; m< rowExpr.length(); m++){
							if(Character.isDigit(rowExpr.charAt(m))||rowExpr.charAt(m)=='.'){
								num2.append(rowExpr.charAt(m));
								if(m == rowExpr.length()-1){
									k=m;
								}
							}else{
								k = m-1;
								break;
							}
						}
						t.append(num2.toString());
					}else{//列表达式中的其他字符
						t.append(rowExpr.charAt(k));
					}
	
				}//end for
				
				t.append(")");
				sql.append(t.toString());
				
			}else if(colExpr.charAt(i)=='c' || colExpr.charAt(i)=='C'){//行表达式中的常量
				StringBuffer num2 = new StringBuffer();
				for(int ii = i+1; ii< rowExpr.length(); ii++){
					if(Character.isDigit(colExpr.charAt(ii))||colExpr.charAt(ii)=='.'){
						num2.append(colExpr.charAt(ii));
						if(ii == colExpr.length()-1){
							i=ii;
						}
					}else{
						i = ii-1;
						break;
					}
				}
				sql.append(num2.toString());	
			}else{
				sql.append(colExpr.charAt(i));
			}
		}
		return sql.toString();
	}
	
/*	*//**
	 * 判断报表是否存在
	 * @param tbo
	 * @param tabid
	 * @return
	 * @throws GeneralException 
	 *//*
	public boolean isExistReport(TnameBo tbo , String tabid) throws GeneralException{
		boolean b = false;
		if(this.reportPrefix.equals("tb")){
			b = tbo.isExistTable(tabid);
		}else if(this.reportPrefix.equals("tt_")){
			//没有则创建表
			tbo.getTgridBo().execute_TT_table(tabid,tbo.getColInfoList().size());
			b=true;
		}
		return b;
	}
	*/
	
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
		// 校验右表达式
		// 支持月(起始日期)或月(截止日期)
		RowSet rs= null;
		String temp1 ="";
		String temp2 ="";
		if (expr.indexOf("M")!=-1||expr.indexOf("Q")!=-1) {
			
			if(this.tbo!=null){
				this.tbo.setUserName(this.userView.getUserName());
				String appdate = this.tbo.getOwnerDate(""+this.tbo.getTabid());//左表达式的tabid
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
}
