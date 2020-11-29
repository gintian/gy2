package com.hjsj.hrms.businessobject.report.auto_fill_report;

import com.hjsj.hrms.businessobject.report.SQL_Util;
import com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse.ExprUtil;
import com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse.ReportSpaceAnalyse;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;


/**
 * <p>Title:普通表间校验</p>
 * <p>Description:表间公式校验</p>
 * <p>Company:hjsj</p>
 * <p>create time:</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportSpaceCheck {
	
	private Connection conn;	//数据库连接
	private String tabid ;      //报表表号
	private String whereSQL;    //数据限制条件  
	private String errors;      //表间校验错误信息
	private String reportPrefix; //操作表的前缀：tb 或 tt_
	private UserView userView;
	/*
	 * 表间校验说明：运算规则[表号：列号：行号]
	 * 示例：[11:2:3]=[12:1:2] 第11表3行2列数据 = 第12表2行1列数据
	 * 		[11:3+4:4*5] = [12:4:6*4] 第11表4行3列数据*5行3列数据+4行4列数据*5行4列数据 = 第12表6行4列数据*4行4列数据
	 */
	

	/**
	 * 表间校验
	 * @param conn  		DB连接
	 * @param tabid			报表表号
	 * @param reportFlag    报表前缀 1 tb  /  2 tt_
	 * @param whereSQL      SQL语句的where片段
	 */
	public ReportSpaceCheck(Connection conn , String tabid ,int reportFlag,String whereSQL){
		this.conn = conn ;
		this.tabid = tabid;
		if(reportFlag == 1){
			this.reportPrefix = "tb";
			this.whereSQL = " and username = '" + whereSQL + "' ";
		}else if(reportFlag == 2){
			this.reportPrefix = "tt_";
			this.whereSQL = " and unitcode = '" + whereSQL + "' ";
		}
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
  

	/**
	 * 表间校验
	 * @return 校验结果-JSP页面输出
	 * @throws GeneralException
	 */
	public String reportSpaceCheckResult() throws GeneralException{
		StringBuffer rscr = new StringBuffer();
	
		//判断报表统计结果表是否存在
		if(this.isExistTable(this.reportPrefix,this.tabid)){
			SQL_Util su = new SQL_Util();
			//校验结果
		
			
			//表间公式信息
			String sql="select * from tcheck where tabid = " + this.tabid;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			try{
				rs =dao.search(sql.toString());
				while(rs.next()){//表间校验公式
					
					//原始表达式
					String lExpr = rs.getString("lexpr");	 //左表达式
					String operator = rs.getString("opsign");//运算符
					String rExpr = rs.getString("rexpr");    //右表达式
					String ndec = rs.getString("ndec");      //小数位 表tcheck中定义了每个表间计算公式的小数位
					
					//获得表达式SQL表示
					String lExprSQL = null;
					String rExprSQL = null;
					
					//表间校验类,校验语法是否正确,返回SQL语句
					ReportSpaceAnalyse rsc = new ReportSpaceAnalyse(this.conn,this.reportPrefix ,this.whereSQL);
					rsc.setUserView(this.userView);
					if(rsc.run(lExpr)){
						 lExprSQL = su.sqlSwitch(rsc.getSQL_Sum());
						 if(lExprSQL!=null&&lExprSQL.length()>0) {
                             lExprSQL="round("+lExprSQL+","+ndec+")";
                         }
					}
					if(rsc.getCError()!=null){
						this.errors = rsc.getCError();
					}
					
					if(rsc.run(rExpr)){
						 rExprSQL = su.sqlSwitch(rsc.getSQL_Sum());
						 if(rExprSQL!=null&&rExprSQL.length()>0) {
                             rExprSQL="round("+rExprSQL+","+ndec+")";
                         }
					} 
					if(rsc.getCError()!=null){
						this.errors = rsc.getCError();
					}
					
					//语法出现错误
					if(lExprSQL == null || rExprSQL == null){
						StringBuffer error = new StringBuffer(); 
						error.append("&nbsp;&nbsp;&nbsp;&nbsp;" + ResourceFactory.getProperty("reportspacecheck.check"));
						error.append(lExpr);
						error.append(ExprUtil.getOperator(operator));
						error.append(rExpr);
						error.append(ResourceFactory.getProperty("reportspacecheck.space")+ "<br>");
						error.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
						error.append(this.errors);
						error.append("<br>");
						rscr.append(error.toString());
					}else{
						//语法正确
						StringBuffer reportspacesql = new StringBuffer();
						reportspacesql.append("select  ");
						reportspacesql.append(lExprSQL);
						reportspacesql.append(" as lexpr , ");
						reportspacesql.append(rExprSQL);
						reportspacesql.append(" as rexpr ");
						reportspacesql.append(" from tcheck where ");
						reportspacesql.append(lExprSQL);
						reportspacesql.append(ExprUtil.getReverseOperatorToSQL(ExprUtil.getOperator(operator)));
						reportspacesql.append(rExprSQL);
						
				//		System.out.println("sql=" + reportspacesql.toString());
						
						String reportSpaceCheckResult = this.getReportSpaceCheckValues(reportspacesql.toString(),lExpr,operator,rExpr,ndec);
						if(reportSpaceCheckResult !=null){
							rscr.append(reportSpaceCheckResult);
						}
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
			return rscr.toString();
		}else{
			rscr.append("&nbsp;&nbsp;&nbsp;&nbsp;"+ResourceFactory.getProperty("auto_fill_report.batchFillData.info9")+"！<br>");
			return rscr.toString();
		}

	}
	
	
	/**
	 * 获得特定校验公式校验结果
	 * @param sql 	SQL语句
	 * @param lexpr 左表达式
	 * @param o     运算符
	 * @param rexpr 右表达式 
	 * @param ndec  小数位
	 * @return      错误信息
	 * @throws GeneralException
	 */
	public String getReportSpaceCheckValues(String sql , String lexpr ,String o , String rexpr ,String ndec) throws GeneralException{
		StringBuffer checkValue = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs =dao.search(sql.toString());		
			if(rs.next()){
				//校验结果有错误
				checkValue.append("&nbsp;&nbsp;&nbsp;&nbsp;"+ ResourceFactory.getProperty("reportspacecheck.check"));
				checkValue.append(lexpr);
				checkValue.append(ExprUtil.getOperator(o));
				checkValue.append(rexpr);
				checkValue.append(ResourceFactory.getProperty("reportspacecheck.space")+ "<br>");
				String lExprValue = rs.getString("lexpr");
				String rExprValue = rs.getString("rexpr");
				
			/*	System.out.println("******************************");
				System.out.println("lExprValue=" + lExprValue);
				System.out.println("rExprValue=" + rExprValue);
				System.out.println("小数点=" + ndec);
				System.out.println("******************************");
				*/
				if(ndec == null || "".equals(ndec)){
					ndec="0";
				}
				String temp = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + 
				this.getFormatExprValue(lExprValue , Integer.parseInt(ndec)) +
				ExprUtil.getReverseOperator(ExprUtil.getOperator(o))+ 
				this.getFormatExprValue(rExprValue , Integer.parseInt(ndec));
				
				checkValue.append(temp);
				checkValue.append("<br>");
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
		return checkValue.toString();
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
