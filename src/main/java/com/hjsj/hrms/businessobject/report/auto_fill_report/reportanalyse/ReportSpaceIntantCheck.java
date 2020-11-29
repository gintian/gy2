/**
 * 
 */
package com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse;

import com.hjsj.hrms.businessobject.report.SQL_Util;
import com.hjsj.hrms.businessobject.report.auto_fill_report.ReportCheckErrorInfo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;


/**
 * <p>Title:特定的表间即时校验</p>
 * <p>Description:报表编辑中的表间校验</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 7, 2006:9:21:05 AM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportSpaceIntantCheck {
	private Connection conn;  //数据库连接
	private double [][]reportValues ; //特定报表值
	private String tabid;     //表号
	private String reportPrefix;
	private String whereSQL;
	private UserView userView;
	/**
	 * 特定报表表间校验
	 * @param conn DB连接
	 * @param reportValues 报表数据
	 * @param tbo  特定报表类
	 */
	public ReportSpaceIntantCheck(Connection conn , double [][]reportValues ,String tabid ,int reportFlag,String whereSQL){
		this.conn = conn;
		this.reportValues = reportValues;
		this.tabid = tabid;
		if(reportFlag == 1){
			this.reportPrefix = "tb";
			this.whereSQL = " and username = '" + whereSQL + "' ";
		}else if(reportFlag == 2){
			this.reportPrefix = "tt_";
			this.whereSQL = " and unitcode = '" + whereSQL + "' ";
		}
		
	/*	System.out.println("************特定表数据****************");
		for(int i = 0 ; i< reportValues.length; i++){
			for(int j=0; j<reportValues[i].length; j++){
				System.out.print("  value["+i+"]["+j+"]= " + reportValues[i][j]);
			}
			System.out.println();
		}
		System.out.println("************特定表数据****************");
		*/
	}
	
	/**
	 * 特定报表表间校验
	 * @return
	 * @throws GeneralException 
	 */
	public String reportSpaceIntantChek() throws GeneralException{ 
		SQL_Util su = new SQL_Util();
		StringBuffer result = new StringBuffer();
		String sql="select * from tcheck where tabid = " +this.tabid;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs =dao.search(sql.toString());
			while(rs.next()){//表间校验公式
				
				//原始表达式
				String lExpr = rs.getString("lexpr");	 //左表达式
				String operator = rs.getString("opsign");//运算符
				String rExpr = rs.getString("rexpr");    //右表达式
				String ndec = rs.getString("ndec");      //小数位
				
		/*		System.out.println("lexpr=" + lExpr);
				System.out.println("rExpr=" + rExpr);*/
				
				//获得表达式SQL表示
				String lExprSQL = null;
				String rExprSQL = null;
			
				//特定表间校验类,校验语法是否正确,返回SQL语句
				ReportSpaceAnalyse rsc = new ReportSpaceAnalyse(this.conn,this.tabid,this.reportValues ,this.reportPrefix,this.whereSQL);
				rsc.setUserView(this.userView);
			
				if(rsc.run(lExpr)){
					 lExprSQL = su.sqlSwitch(rsc.getSQL_Sum());
				}
				if(rsc.getCError()!=null){
					
				}
				//System.out.println("lsql=" + rsc.getSQL_Sum());

				
				if(rsc.run(rExpr)){
					 rExprSQL = su.sqlSwitch(rsc.getSQL_Sum());
					 if(rExprSQL!=null&&rExprSQL.length()>0) {
                         rExprSQL="round("+rExprSQL+","+ndec+")";
                     }
				} 
				if(rsc.getCError()!=null){
					
				}				
			//	System.out.println("rsql=" + rsc.getSQL_Sum());

				
				//表达式
				StringBuffer expr = new StringBuffer();
				expr.append("&nbsp;&nbsp;&nbsp;&nbsp;"+ ResourceFactory.getProperty("reportspacecheck.check"));
				expr.append(lExpr);
				expr.append(ExprUtil.getOperator(operator));
				expr.append(rExpr);
				expr.append(ResourceFactory.getProperty("reportspacecheck.space"));

				
				//语法出现错误
				if(lExprSQL == null || rExprSQL == null){
					ReportCheckErrorInfo rcei = new ReportCheckErrorInfo(expr.toString(),rsc.getCError());
					String temp = rcei.toString();
					result.append(temp);
					result.append("#");
				}else{
					//语法正确
					StringBuffer reportspacesql = new StringBuffer();
					reportspacesql.append("select  ");
					reportspacesql.append("round("+lExprSQL+","+ndec+")");
					reportspacesql.append(" as lexpr , ");
					reportspacesql.append(rExprSQL);
					reportspacesql.append(" as rexpr ");
					reportspacesql.append(" from tcheck where ");
					reportspacesql.append("round("+lExprSQL+","+ndec+")");
					reportspacesql.append(ExprUtil.getReverseOperatorToSQL(ExprUtil.getOperator(operator)));
					reportspacesql.append(rExprSQL);
			
			/*		System.out.println("************************************");
					System.out.println("lExprsql=" + lExprSQL.toString());
					System.out.println("rs=" + reportspacesql.toString());
					System.out.println("RExprsql=" + rExprSQL.toString());
					System.out.println("**************************************");*/
					
					String reportSpaceCheckResult = this.getReportSpaceCheckValues(reportspacesql.toString(),operator,ndec);
					if(reportSpaceCheckResult == null){		
						//System.out.println("ok");
					}else{
						ReportCheckErrorInfo rcei = new ReportCheckErrorInfo(expr.toString(),reportSpaceCheckResult,"null","null","null",3);
						String temp = rcei.toString();
						result.append(temp);
						result.append("#");
					}
					/*if(reportSpaceCheckResult !=null || !reportSpaceCheckResult.equals("")){
						ReportCheckErrorInfo rcei = new ReportCheckErrorInfo(expr.toString(),reportSpaceCheckResult,"null","null","null",3);
						String temp = rcei.toString();
						result.append(temp);
						result.append("#");	
					}*/
				}
				
				
			}
		}catch(Exception e){
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

		}*/
		
		if(result == null || "".equals(result.toString())){
			return "null";
		}
		
		if(result.charAt(result.length()-1)=='#'){
			String temp = result.toString().substring(0,result.length()-1);
			return temp;
		}else{
			return result.toString();
		}	
	}
	
	
	/**
	 * 获得特定校验公式校验结果
	 * @param sql    SQL语句
	 * @param lexpr  左表达式
	 * @param o      运算符
	 * @param rexpr  右表达式 
	 * @param ndec   小数位
	 * @return       错误信息
	 * @throws GeneralException
	 */
	public String getReportSpaceCheckValues(String sql ,String o  ,String ndec) throws GeneralException{
		StringBuffer checkValue = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs =dao.search(sql.toString());		
			if(rs.next()){
				//校验结果有错误				
				String lExprValue = rs.getString("lexpr");
				String rExprValue = rs.getString("rexpr");
				
				//System.out.println("rExprValue = " + rExprValue);
				
				String temp = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + 
				this.getFormatExprValue(lExprValue , Integer.parseInt(ndec)) +
				ExprUtil.getReverseOperator(ExprUtil.getOperator(o))+ 
				this.getFormatExprValue(rExprValue , Integer.parseInt(ndec));
				
				checkValue.append(temp);
				
				//checkValue.append("<br>");
/*				
				System.out.println("rValue = " + this.getFormatExprValue(lExprValue , Integer.parseInt(ndec)));
				System.out.println("rValue = " + this.getFormatExprValue(rExprValue , Integer.parseInt(ndec)));
				
				System.out.println("checkValue=" + checkValue.toString());*/
			
			}//end while
		}catch(Exception e){
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
		}*/
		if(checkValue != null && !"".equals(checkValue.toString())){
			return checkValue.toString();
		}else{
			return null;
		}
		
	}
	
	
	/**
	 * 获取规范的表达式的值
	 * @param exprValue 表达式值
	 * @param flag      小数位
	 * @return  规范后的值
	 */
	public String getFormatExprValue(String exprValue , int flag){
		return PubFunc.round(exprValue,flag);
	/*	StringBuffer sb = new StringBuffer();	
		if(flag == 0){
			sb.append("####");
		}else{
			sb.append("####.");
			for(int i = 0 ; i < flag ; i++){
				sb.append("0");
			}
		}	
		DecimalFormat df = new DecimalFormat(sb.toString());
		String dstr = df.format(Double.parseDouble(exprValue));
		return dstr;*/
	}

	public UserView getUserView() {
		return userView;
	}

	public void setUserView(UserView userView) {
		this.userView = userView;
	}
	
}
