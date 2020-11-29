/**
 * 
 */
package com.hjsj.hrms.businessobject.report.formulaAnalyse;

import com.hjsj.hrms.businessobject.report.SQL_Util;
import com.hjsj.hrms.businessobject.report.TgridBo;
import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse.Constant;
import com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse.ExprUtil;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Title:表间格计算公式分析/运算</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time: </p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportSpaceGridFormulaAnalyse {
	
	private Connection conn;
	private String lExpr;
	private String rExpr;
	private int  tabid;
	private TnameBo tbo ;
	
	//计算基准 左表达式
	private String r;
	private String c;
	
	private String errors;
	private String reportPrefix;
	private String whereSQL;
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
	 * 表间格计算公式分析/运算构造器
	 * @param conn DB连接
	 * @param tabid 表号
	 * @param lExpr 左表达式
	 * @param rExpr 右表达式
	 * @param userName 用户名
	 */
	public ReportSpaceGridFormulaAnalyse(Connection conn ,int tabid ,String lExpr ,String rExpr  ,String reportPrefix ,String whereSQL) {
		this.conn = conn;
		this.tabid = tabid;
		this.lExpr = lExpr;
		this.rExpr = rExpr;
		this.tbo = new TnameBo(this.conn ,String.valueOf(this.tabid));
		this.reportPrefix = reportPrefix;
		this.whereSQL = whereSQL;
	}
	/**
	 * 表间格计算公式语法分析
	 * @return
	 * @throws GeneralException 
	 */
	public String reportSpaceGridFormulaCheck() throws GeneralException{
		StringBuffer result = new StringBuffer();
		if(!this.lExprAnalyse()){
			result.append(this.errors);
		}else{
			//分析右表达式
			if(this.run(this.rExpr)){
			}else{
				result.append(this.getCError());
			}
		}
		if(result== null || "".equals(result.toString())){
			return "ok";
		}else{
			return result.toString();
		}
	}
	
	
	
	/**
	 * 表间格计算公式语法分析与计算
	 * @return
	 * @throws GeneralException 
	 */
	public String reportSpaceGridFormulaAnalyse() throws GeneralException{
		SQL_Util su = new SQL_Util();
		StringBuffer result = new StringBuffer();
		if(!this.lExprAnalyse()){
			result.append(this.errors);
		}else{//左表达式正确
			StringBuffer updatesql = new StringBuffer();
			ArrayList list = new ArrayList();
			TgridBo tgridBo=new TgridBo(conn);
			list.add(""+tabid);
			boolean ownerflag = tgridBo.isSetOwnerDate(list);
			this.tbo.setUserName(userView.getUserName());
			String datevalue =this.tbo.getOwnerDate(""+tabid);
			if(!this.tbo.isUpdateDate(ownerflag,Integer.parseInt((String)this.tbo.getRowMap().get(this.r)), datevalue,""+tabid)){
				
			//分析右表达式
			if(this.run(this.rExpr)){
				String sql = this.getSQL_Sum();
				//update语句
				updatesql.append(" update ");
				updatesql.append(this.reportPrefix);
				updatesql.append(this.tabid);
				updatesql.append(" set C");
				updatesql.append(Integer.parseInt((String)this.tbo.getColMap().get(this.c))+1);
				updatesql.append(" = ");
				//System.out.println("_____" + sql);
				updatesql.append(su.sqlSwitch(sql));
				updatesql.append(" where secid =  ");
				updatesql.append(Integer.parseInt((String)this.tbo.getRowMap().get(this.r))+1);
				updatesql.append(this.whereSQL);
			}else{
				result.append(this.getCError());
			}
			//System.out.println(updatesql.toString());
			//执行SQL语句组
			if(this.executeUpdateSQL(updatesql.toString())){
			}else{	
			}
			String operateObject="1";
			String unitcode="";
			if("tt_".equalsIgnoreCase(reportPrefix)){
				operateObject="2";
				if(this.whereSQL.indexOf("=")!=-1){
					String temp ="";
					temp = this.whereSQL.substring(this.whereSQL.indexOf("=")+1, this.whereSQL.length());
					temp = temp.replace("'", "");
					unitcode=temp.trim();
				}
			}
			this.tbo.autoUpdateDigitalResults(operateObject, "5", ""+(Integer.parseInt((String)this.tbo.getRowMap().get(String.valueOf(this.r)))+1), "c"+(Integer.parseInt((String)this.tbo.getColMap().get(String.valueOf(this.c)))+1), this.tbo.getTabid(),userView.getUserName(),unitcode);

		}
		}
		if(result== null || "".equals(result.toString())){
			return "null";
		}else{
			return result.toString();
		}
		
		
	}
	
	/**
	 * 左表达式分析 如： 1:3 当前表第1行第3列
	 * @return
	 */
	public boolean lExprAnalyse(){
		boolean b = true;
		//左表达式是否合法
		if(this.lExpr.matches("[1-9]\\d*\\:[1-9]\\d*")){
			int n = this.lExpr.indexOf(":");
			this.r = this.lExpr.substring(0,n);
			this.c  = this.lExpr.substring(n+1,this.lExpr.length());
			this.r=parseFormula(this.r);
			this.c=parseFormula(this.c);
			if(this.r.trim().length()>10){
				this.errors= ResourceFactory.getProperty("colcheckanalyse.lexpr")+this.r+ResourceFactory.getProperty("colcheckanalyse.rowNoExist")+"！";
				b= false;
				return b;
			}
			if(Integer.parseInt(this.r) > this.tbo.getMaxRowNumber(this.tbo.getRowMap())){
				this.errors= ResourceFactory.getProperty("colcheckanalyse.lexpr")+this.r+ResourceFactory.getProperty("colcheckanalyse.rowNoExist")+"！";
				b= false;
				return b;
			}
			if(this.c.trim().length()>10){
				this.errors = ResourceFactory.getProperty("colcheckanalyse.lexpr")+this.c+ResourceFactory.getProperty("colcheckanalyse.colNoExist")+"！";
				b= false;
				return b;
			}
			if(Integer.parseInt(this.c) > this.tbo.getMaxRowNumber(this.tbo.getColMap())){
				this.errors = ResourceFactory.getProperty("colcheckanalyse.lexpr")+this.c+ResourceFactory.getProperty("colcheckanalyse.colNoExist")+"！";
				b= false;
				return b;
			}
			
		}else{
			this.errors = ResourceFactory.getProperty("edit_report.info15")+"！";
			b= false;
			return b;
		}
		
		return b;
	}
	
	/**
	 * 表间格计算公式右表达式分析
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
			if(nCurPos == nFSourceLen){				
			}else{
				while(Character.isDigit(fSource.charAt(nCurPos)) || fSource.charAt(nCurPos)=='.'){
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
	
	
	public boolean level0() throws GeneralException, NumberFormatException{
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
	
	public boolean level1() throws GeneralException, NumberFormatException{
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
	
	public boolean level2() throws GeneralException, NumberFormatException{
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
		//中括号	
		}else if(tok == Constant.S_L && token_type == Constant.DELIMITER){
			//获取[]之间的表达式
			StringBuffer temp = new StringBuffer();
			for(int i= nCurPos ; i< this.nFSourceLen ; i++){
				if(this.fSource.charAt(i)==']'){
					break;
				}
				temp.append(this.fSource.charAt(i));
				nCurPos++;
			}
			
			if(nCurPos == nFSourceLen ){
				tok = Constant.S_FINISHED;
				token_type = Constant.DELIMITER;
				this.setError(Constant.E_SYNTAX);
				return result;
			}	
			nCurPos++;
			
			//验证规则是否成立[表号：行：列]
			if(this.checkRepostString(temp.toString())){
				//拆分字符串
				String expr = temp.toString();
				int n = expr.indexOf(":" , 0);
				String tabid1 = expr.substring(0,n);
				int n1 = expr.indexOf(":" , n+1);
				String rowExpr = expr.substring(n+1,n1);
				String colExpr = expr.substring(n1+1,expr.length());
				//针对拆分的字符进行处理，M表示截止日期的月份，Q表示截止日期的季度
				rowExpr=parseFormula(rowExpr);
				colExpr=parseFormula(colExpr);
				
				if(this.isExistReport(this.tbo , tabid1)){
					boolean flag =false;
					TnameBo tb = new TnameBo(this.conn,tabid1);
					try{
						Integer.parseInt(rowExpr);
					}catch(Exception e){
						this.cError = rowExpr +"为非法字符！";
						throw new GeneralException(this.cError);
					}
					try{
						Integer.parseInt(colExpr);
					}catch(Exception e){
						this.cError =  colExpr +"为非法字符！";
						throw new GeneralException(this.cError);
					}
					if(Integer.parseInt(rowExpr) > tb.getMaxRowNumber(tb.getRowMap())){
						//行不存在
						this.cError = "tb" + tabid1 +ResourceFactory.getProperty("reportspacecheck.bz") + rowExpr +ResourceFactory.getProperty("colcheckanalyse.rowNoExist")+"！";
						flag=true;
						//throw new GeneralException(this.cError);
					}
					if(Integer.parseInt(colExpr) > tb.getMaxRowNumber(tb.getColMap())){
						//列不存在
						this.cError = "tb" + tabid1 +ResourceFactory.getProperty("reportspacecheck.bz") + colExpr +ResourceFactory.getProperty("colcheckanalyse.colNoExist")+"！";
						flag=true;
						//throw new GeneralException(this.cError);
					}
					if(!flag){
					int r = Integer.parseInt((String)tb.getRowMap().get(rowExpr))+1;
					int c = Integer.parseInt((String)tb.getColMap().get(colExpr))+1;
					
					StringBuffer sql = new StringBuffer();
					sql.append(" ( select C");
					sql.append(c);
					sql.append(" from ");
					sql.append(this.reportPrefix);
					sql.append(tabid1);
					sql.append(" where secid=");
					sql.append(r);
					sql.append(this.whereSQL);
					sql.append(" )");
					
					this.SQL_SUM += Sql_switcher.isnull(sql.toString(),"0");
					}else{
						this.SQL_SUM += Sql_switcher.isnull("0","0");
					}
					
				}else{
					//报表为未生成统计表
					this.cError = ResourceFactory.getProperty("menu.report") + tabid1 +ResourceFactory.getProperty("reportspacecheck.wsctjb")+"！";
				}
			}else{
				//语法错误
				this.setError(Constant.E_SYNTAX);
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
	 * @throws NumberFormatException 
	 * @throws GeneralException 
	 */
	public boolean run(String fSource ) throws GeneralException{
		boolean result = false;
		this.fSource = fSource.trim(); //校验的表达式
		this.fSource = this.fSource.replace("m", "M");
		this.fSource = this.fSource.replace("q", "Q");
		this.fSource=this.fSource.replace("M+", "CM+");
		this.fSource=this.fSource.replace("M-", "CM-");
		this.fSource=this.fSource.replace("M*", "CM*");
		this.fSource=this.fSource.replace("M/", "CM/");
		this.fSource=this.fSource.replace("+M", "+CM");
		this.fSource=this.fSource.replace("-M", "-CM");
		this.fSource=this.fSource.replace("*M", "*CM");
		this.fSource=this.fSource.replace("/M", "/CM");
		this.fSource=this.fSource.replace("CC", "C");
		this.fSource=this.fSource.replace("Q+", "CQ+");
		this.fSource=this.fSource.replace("Q-", "CQ-");
		this.fSource=this.fSource.replace("Q*", "CQ*");
		this.fSource=this.fSource.replace("Q/", "CQ/");
		this.fSource=this.fSource.replace("+Q", "+CQ");
		this.fSource=this.fSource.replace("-Q", "-CQ");
		this.fSource=this.fSource.replace("*Q", "*CQ");
		this.fSource=this.fSource.replace("/Q", "/CQ");
		this.fSource=this.fSource.replace("CC", "C");
		//System.out.println(this.fSource);
		this.fSource=parseFormula(this.fSource);
		//System.out.println(this.fSource);
		this.nFSourceLen = this.fSource.length();//表达式长度
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
		
		
	/*	排除信息校验
	 * if(!this.checkPCMessage(this.lExpr , nMaxFactorNum)){
			this.setError(Constant.E_EliminatesError);
			return result;
		}
		
		*/
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
		}else if(Constant.E_EliminatesError == num){
			this.cError =
				ResourceFactory.getProperty("colcheckanalyse.paichu")+
				ResourceFactory.getProperty("colcheckanalyse.row")+
				ResourceFactory.getProperty("constant.e_factornoexist");		
		}else if(Constant.E_ErrorNumber == num){
			this.cError =ResourceFactory.getProperty("constant.e_errornumber");
		}else if(Constant.E_CONSTANT == num){
			this.cError = ResourceFactory.getProperty("colcheckanalyse.defineConstantError")+"!";
		}
		
		if(nCurPos <= 200){
			this.cError = "\'" + fSource.substring(0,nCurPos) +"\' "+
			ResourceFactory.getProperty("colcheckanalyse.zhong")+ ExprUtil.getLNumber(fSource.substring(0,nCurPos)) + this.cError;
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
	 * 执行UpdateSQL语句
	 * @return 
	 * @throws GeneralException 
	 */
	public boolean executeUpdateSQL(String  sql) throws GeneralException {


		
		boolean b = false;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			if(sql==null||sql.trim().length()==0) {
                return b;
            }
			dao.update(sql);
			b= true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return b;
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
	
	// 处理字符
	public String  parseFormula(String expr) {
//		RowSet rs= null;
		String temp1 ="";
		String temp2 ="";
		if (expr.indexOf("M")!=-1||expr.indexOf("Q")!=-1) {
			ContentDAO dao = new ContentDAO(this.conn);
			String xml = "";
			String userName = userView.getUserName();
//			try {
				// 常量表中查找rp_param常量
//				 rs = dao
//						.search("select STR_VALUE  from CONSTANT where CONSTANT='RP_PARAM'");
//				if (rs.next()) {
//					xml = Sql_switcher.readMemo(rs, "STR_VALUE");

					// xml文件分析类
//					AnalyseParams aps = new AnalyseParams(xml);
					this.tbo.setUserName(userName);
					String appdate = this.tbo.getOwnerDate(""+tabid); // 截止日期
				//	String startdate = ""; // 启始日期

//					boolean flag = true;
//					if(!this.userView.isSuper_admin()){
//						if(this.userView.getFuncpriv().indexOf(",29011,")==-1)
//							flag =false;
//					}
//					if (aps.checkUserid(userName)&&flag) {// DB中存在当前用户的扫描库配置信息
//
//
//						// 用户配置信息封装在MAP内
//						HashMap hm = aps.getAttributeValues(userName);
//						appdate = (String) hm.get("appdate");// 截止日期
//					//	startdate = (String) hm.get("startdate"); // 起始日期
//
//					}else{
//						if(ConstantParamter.getAppdate(this.userView.getUserName())!=null)
//						{
//							String value=ConstantParamter.getAppdate(this.userView.getUserName()).replaceAll("\\.","-");
//							appdate=value;
//					//		startdate=value;
//						}
//					}
					int app = 0;
//					int start = 0;
					if (appdate != null && appdate.length() > 7) {
                        app = Integer.parseInt(appdate.substring(5, 7));
                    }
//					if (startdate != null && startdate.length() > 7)
//						start = Integer.parseInt(startdate.substring(5, 7));
//					if (start != 0) {
//						this.rExpr = this.rExpr.replace("月(起始日期)", "C" + start);
//					}
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
//			else{
//					if(ConstantParamter.getAppdate(this.userView.getUserName())!=null)
//					{
//						String value=ConstantParamter.getAppdate(this.userView.getUserName()).replaceAll("\\.","-");
//						int app = 0;
////						int start = 0;
//						if (value != null && value.length() > 7)
//							app = Integer.parseInt(value.substring(5, 7));
//						if (expr.indexOf("M")!=-1&&app != 0) {
//							temp1 = ""+app;
//							expr=expr.replace("M", temp1);
//						}
//						if(expr.indexOf("Q")!=-1&&app != 0){
//							if(0<app&&app<4)
//							temp2="1";
//							if(3<app&&app<7)
//								temp2="2";
//							if(6<app&&app<10)
//								temp2="3";
//							if(9<app&&app<13)
//								temp2="4";
//							expr=expr.replace("Q", temp2);
//						}
//					}
//				}
//				rs.close();
//			}

//			catch (Exception e) {
//				e.printStackTrace();
//			}
//			finally{
//				if(rs!=null)
//					try {
//						rs.close();
//					} catch (SQLException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//			}
//		}
		return expr;
	}
	public UserView getUserView() {
		return userView;
	}
	public void setUserView(UserView userView) {
		this.userView = userView;
	}
	
	
	
/*	
	public static void main(String[] args) {
		String temp = "1:1";
		String regex = "[1-9]\\d*\\:[1-9]\\d*";
		System.out.println(temp.matches(regex));
		int n = temp.indexOf(":");
		String row = temp.substring(0,n);
		String col = temp.substring(n+1,temp.length());
		
		System.out.println(row);
		System.out.println(col);
	}*/

}
