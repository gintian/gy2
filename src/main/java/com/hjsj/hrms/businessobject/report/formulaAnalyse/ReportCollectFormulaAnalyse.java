/**
 * 
 */
package com.hjsj.hrms.businessobject.report.formulaAnalyse;

import com.hjsj.hrms.businessobject.report.SQL_Util;
import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse.Constant;
import com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse.ExprUtil;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>Title:报表汇总公式分析/计算</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 28, 2006:11:57:17 AM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportCollectFormulaAnalyse {
	
	private Connection conn;
	private String unitCode;
	private ArrayList unitCodeList = new ArrayList();
	private String tabid;
	private TnameBo tbo;
	private String errors;
	
	private String fSource;  //表达式
	private String cError;   //错误信息
	private int nMaxFactorNum;//最大因子
	private String SQL_SUM;       //表达式结果	
	private int tok;          //因子具体类型
	private String token;     //因子
	private int token_type;   //因子基本类型
	private int nCurPos;      //字符定位标识
	private int nFSourceLen;  //表达式长度
	private String operate;
	private String username="";
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * 报表汇总公式分析计算-构造器
	 *   公式描述@错误信息描述#公式描述@错误信息描述
	 *	 如果没有错误信息返回“null”和先前的计算公式一致！
	 * @param conn      DB连接
	 * @param tabid     报表表号
	 * @param unitCode  填报单位编码
	 */
	public ReportCollectFormulaAnalyse(Connection conn , String tabid , String unitCode){
		this.conn = conn;
		this.tabid = tabid;
		this.unitCode = unitCode;
		this.tbo = new TnameBo(conn,tabid);
	}
	
	/**
	 * 报表汇总公式分析计算-构造器
	 *   公式描述@错误信息描述#公式描述@错误信息描述
	 *	 如果没有错误信息返回“null”和先前的计算公式一致！
	 * @param conn      DB连接
	 * @param tabid     报表表号
	 * @param unitCodeList  填报单位编码列表
	 */
	public ReportCollectFormulaAnalyse(Connection conn , String tabid , ArrayList unitCodeList){
		this.conn = conn;
		this.tabid = tabid;
		this.unitCodeList = unitCodeList;
		this.tbo = new TnameBo(conn,tabid);
	}
	
	/**
	 * 汇总公式计算
	 * @return
	 * @throws GeneralException
	 */
	public String reportCollectFormulaAnalyse() throws GeneralException{
		StringBuffer result = new StringBuffer();
		SQL_Util su = new SQL_Util();
		
		StringBuffer sql = new StringBuffer();
		sql.append("select * from tformula where tabid = ");
		sql.append(this.tabid);
		sql.append(" and colrow = 5");
		
		ContentDAO  dao = new ContentDAO(this.conn); 
		RowSet rs = null;
		try {
			rs = dao.search(sql.toString());
			while(rs.next()){
				String le = rs.getString("lexpr");
				String re = rs.getString("rexpr");
				String ree = this.getRExpr(re);
				
				//左表达式是否正确
				if(this.lExprAnalyse(le)){
					//右表达式语法分析正确
					if(this.run(ree,this.tbo.getMaxRowNumber(this.tbo.getColMap()))){
						String usql = this.CreateUpdateSQL(le,re,su.sqlSwitch(this.SQL_SUM));
						//System.out.println("updateSQL=" + usql);
						String isnull = "";    //不同库isnull函数不同    wangcq 2014-12-19
						if(usql.indexOf("ISNULL((")!=-1) {
                            isnull = "ISNULL((";
                        }
						if(usql.indexOf("COALESCE((")!=-1) {
                            isnull = "COALESCE((";
                        }
						if(usql.indexOf("NVL((")!=-1) {
                            isnull = "NVL((";
                        }
						if(usql.indexOf(isnull)!=-1&&usql.indexOf("/NULLIF(")!=-1){
							
							 Iterator iterator= this.tbo.getRowMap().keySet().iterator();
							 while(iterator.hasNext()){
								 String secid =""+(Integer.parseInt(""+this.tbo.getRowMap().get(iterator.next()))+1);
								 if(this.executeUpdateSQL(usql.replace("secid in ()", "secid in ("+secid+")"))){
										//执行成功
									
									}else{
										//汇总计算公式运算错误
									} 
							}
						}else{
						if(this.executeUpdateSQL(usql)){
							//执行成功
						}else{
							//汇总计算公式运算错误
						}
						}
						 this.tbo.autoUpdateDigitalResults("2", "6", "", "c"+(Integer.parseInt(""+this.tbo.getColMap()
									.get(le))+1), tabid, username,this.unitCode);
					}else{//右表达式语法分析错误
						result.append(this.getFormulaInfo(le,ree));
						result.append("@");
						result.append(this.cError);
						result.append("#");
					}
					
				}else{//坐表达式错误
					result.append(this.getFormulaInfo(le,ree));
					result.append("@");
					result.append(this.errors);
					result.append("#");
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace(); 
			throw GeneralExceptionHandler.Handle(e);
		}/*finally{
			   if(rs!=null){
					try {
						rs.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
						throw GeneralExceptionHandler.Handle(e1);
					}
			   }

		}	*/
		if(result == null || "".equals(result)){
			return "null";
		}else{
			return result.toString();
		}
		
	}
	
	/**
	 * 合成SQL语句
	 * @param lcol 左表达式
	 * @param sql  右表达式生成的SQL片段
	 * @return 
	 */
	public String CreateUpdateSQL(String lcol ,String re,String sql ){
		StringBuffer updateSQL = new StringBuffer();
		boolean flag = false;
		updateSQL.append("update tt_");
		updateSQL.append(this.tabid);
		updateSQL.append(" set C");
		updateSQL.append(Integer.parseInt((String)this.tbo.getColMap().get(lcol))+1);
		updateSQL.append("=");
		//处理sql
		String isnull = "";    //不同库isnull函数不同    wangcq 2014-12-19
		if(sql.indexOf("ISNULL((")!=-1) {
            isnull = "ISNULL((";
        }
		if(sql.indexOf("COALESCE((")!=-1) {
            isnull = "COALESCE((";
        }
		if(sql.indexOf("NVL((")!=-1) {
            isnull = "NVL((";
        }
		if(sql.indexOf(isnull)!=-1&&sql.indexOf("/NULLIF(")!=-1){
			sql = "( select "+sql+"";
			int m =1;
			if(re.indexOf("|")!=-1){
				String temp = re.substring(0,re.indexOf("|")+1);
				
				if(temp.indexOf("/")==-1) {
                    m=1;
                } else{
					String te []=temp.split("/");
					m=te.length;
				}
			}
			String tempsql = "";
			sql = sql.substring(0,sql.indexOf(isnull)+isnull.length())+"sum"+sql.substring(sql.indexOf(isnull)+isnull.length(),sql.length());

			
			for(int i =0 ;i<m;i++){
				tempsql+=sql.substring(0,sql.indexOf("/NULLIF(")+8);
				sql=	sql.substring(sql.indexOf("/NULLIF(")+8,sql.length());
				
			}
			tempsql=tempsql+"sum"+sql;

		sql=tempsql;
		updateSQL.append(sql);
		updateSQL.append(" from tt_");
		updateSQL.append(this.tabid);
		updateSQL.append(" where secid in ()");
		flag=true;
		}else{
		updateSQL.append(sql);
		updateSQL.append(" where secid not in (");
		}
		
		//甲行处理
		StringBuffer jia = new StringBuffer();
		String temp = this.tbo.getRowSerialNo();
		if(temp == null || "".equals(temp)){
			if(!flag){
			updateSQL.append("0");
			updateSQL.append(")");
			}
		}else{
			if(temp.charAt(temp.length()-1)== ','){
				temp = temp.substring(0,temp.length()-1);
			}	
			if(!flag){
			String [] tt = temp.split(",");
			for(int i = 0 ; i< tt.length ; i++){
				jia.append(Integer.parseInt(tt[i])+1);
				jia.append(",");
			}
			jia.deleteCharAt(jia.toString().length()-1);
			
			updateSQL.append(jia.toString());
			updateSQL.append(") ");
			}
			
		}
		if(this.unitCodeList.size()==0){
			updateSQL.append("and unitcode = '");
			updateSQL.append(this.unitCode);
			updateSQL.append("'");
		}else{
			StringBuffer temp4 = new StringBuffer();
			/*
				temp4.append(" and (");
				for(int i=0; i< this.unitCodeList.size(); i++){
					String u = (String)this.unitCodeList.get(i);
					temp4.append(" unitcode = '");
					temp4.append(u);
					temp4.append("' or ");
				}
				temp4.delete(temp4.length()-3,temp4.length());
			 */
			temp4.append(" and unitcode in( ");
			for(int i=0; i< this.unitCodeList.size(); i++){
				String u = (String)this.unitCodeList.get(i);
				if(u.equals(this.unitCode)){
					continue;
				}
				temp4.append("'");
				temp4.append(u);
				temp4.append("' ,");
			}
			temp4.delete(temp4.length()-1,temp4.length());
			
			updateSQL.append(temp4.toString());
			updateSQL.append(" )");
			
		}
		if(sql.indexOf(isnull)!=-1&&sql.indexOf("/NULLIF(")!=-1){
		updateSQL.append(" )");
		updateSQL.append(" where unitcode='"+this.unitCode+"' and secid in ()");
		
		}
		
		return updateSQL.toString();
	}
	
	/**
	 * 执行汇总公式
	 * @param sql
	 * @return
	 */
	public boolean executeUpdateSQL(String sql){
		boolean b = false;
		ContentDAO dao=new ContentDAO(this.conn);
		try {
			dao.update(sql);
			b= true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	
	/**
	 * 校验左表达式是否正确
	 * @param lExpr  左表达式
	 * @return
	 */
	public boolean lExprAnalyse(String lExpr){
		boolean b = lExpr.matches("[1-9]\\d*");
		if(!b){
			//左表达式语法错误！必须是数值！
			this.errors = ResourceFactory.getProperty("edit_report.info13");
			return false;
		}else{
			if(Integer.parseInt(lExpr) > this.tbo.getMaxRowNumber(this.tbo.getColMap()) || Integer.parseInt(lExpr) < 1){
				//左表达式列不存在
				this.errors=ResourceFactory.getProperty("edit_report.info14")+"!";
				return false;
			}else{
				return true;
			}
		}
		
	}
	
	/**
	 * 获得规范化的右表达式
	 * @param expr 右表达式  3|4
	 * @return
	 */
	public String getRExpr(String expr){	
		//逻辑一
		//return expr.replaceAll("\\|" ,"/");		
		
		//逻辑二
		String [] str = expr.split("\\|");
		StringBuffer rexpr = new StringBuffer();
		rexpr.append("(");
		rexpr.append(str[0]);
		rexpr.append(")/(");
		rexpr.append(str[1]);
		rexpr.append(")");
		return ExprUtil.getExpr(rexpr.toString());
	}
	
	/**
	 * @param lExpr 左表达式 
	 * @param rExpr 右表达式
	 */
	public String getFormulaInfo(String lExpr , String rExpr){
		return lExpr + " = " + rExpr + "("+ResourceFactory.getProperty("edit_report.collectFormula")+")";
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
			
			//列集合封装的是针对与二维数组数据从0开始的
			int col =Integer.parseInt((String)(tbo.getColMap().get(token)))+1;
			SQL_SUM += "C" + col;
			
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
				nToken = Integer.parseInt(token.substring(1,token.length()));
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
			this.cError = "\"" + fSource.substring(0,nCurPos) +"\" "+
			ResourceFactory.getProperty("colcheckanalyse.zhong")+ ExprUtil.getLNumber(fSource.substring(0,nCurPos)) + this.cError;
		}else{
			this.cError = "\"" + fSource.substring(nCurPos-200,nCurPos) +"\" "+
			ResourceFactory.getProperty("colcheckanalyse.zhong")+ ExprUtil.getLNumber(fSource.substring(0,nCurPos)) + this.cError;			
		}
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}
	
	public String reportCollectFormulaAnalyse2(String lexper,String rexper){
		String sql="";
		SQL_Util su = new SQL_Util();
		int k=0;
		if(lexper!=null&&lexper.length()!=0&&rexper!=null&&rexper.length()!=0){
			String ree = this.getRExpr(rexper);
			
			if(this.lExprAnalyse(lexper)){
				if(this.run(ree,this.tbo.getMaxRowNumber(this.tbo.getColMap()))){
					sql=this.validatesql(lexper, su.sqlSwitch(this.SQL_SUM),rexper);
				}
			}
		}
		return sql;
	}
	public String validatesql(String le,String sub_sql,String re){
		StringBuffer sql=new StringBuffer();
		StringBuffer skb=new StringBuffer();
		String tempsql = "";
		int ll=0;
		String isnull = "";    //不同库isnull函数不同    wangcq 2014-12-19
		if(sub_sql.indexOf("ISNULL((")!=-1) {
            isnull = "ISNULL((";
        }
		if(sub_sql.indexOf("COALESCE((")!=-1) {
            isnull = "COALESCE((";
        }
		if(sub_sql.indexOf("NVL((")!=-1) {
            isnull = "NVL((";
        }
		if(sub_sql.indexOf(isnull)!=-1&&sub_sql.indexOf("/NULLIF(")!=-1){
			int m =1;
			if(re.indexOf("|")!=-1){
				String temp = re.substring(0,re.indexOf("|")+1);
				
				if(temp.indexOf("/")==-1) {
                    m=1;
                } else{
					String te []=temp.split("/");
					m=te.length;
				}
			}
			sub_sql = sub_sql.substring(0,sub_sql.indexOf(isnull)+isnull.length())+"sum"+sub_sql.substring(sub_sql.indexOf(isnull)+isnull.length(),sub_sql.length());
			for(int i =0 ;i<m;i++){
				tempsql+=sub_sql.substring(0,sub_sql.indexOf("/NULLIF(")+8);
				sub_sql=sub_sql.substring(sub_sql.indexOf("/NULLIF(")+8,sub_sql.length());
			}
			tempsql=tempsql+"sum"+sub_sql;
		}
		StringBuffer sql2=new StringBuffer("");
		StringBuffer temp=new StringBuffer();
		StringBuffer sb=new StringBuffer("");
		sb.append("select ");
//		sb.append(" C");
//		sb.append(Integer.parseInt((String)this.tbo.getColMap().get(le))+1);
//		sb.append("=");
		sb.append(tempsql);
		sb.append(" from tt_");
		sb.append(this.tabid);
		sb.append(" where unitcode in(");
		
		for(int i=0; i< this.unitCodeList.size(); i++){
			String u = (String)this.unitCodeList.get(i);
			sb.append("'");
			sb.append(u);
			sb.append("' ,");
		}sb.setLength(sb.length()-1);
		sb.append(" )");
		return sql.append(sb.toString()).toString();
	}
}
