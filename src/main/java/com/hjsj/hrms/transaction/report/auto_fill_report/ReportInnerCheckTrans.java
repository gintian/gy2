/*
 * Created on 2006-5-17
 *
 */
package com.hjsj.hrms.transaction.report.auto_fill_report;

import com.hjsj.hrms.businessobject.report.auto_fill_report.ReportInnerCheck;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>Title:表内校验：行/列</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportInnerCheckTrans extends IBusiness {
	
	/*
	 * 注：表内校验主要是分析表内的行/列校验公式（rowchk,colchk）表中的校验公式
	 *    通过报表的tabid获得对应的校验公式
	 *    首先分析表达式语法是否正确
	 *    其次判断左表达式值不等于右表达式值的记录集合即校验错误的信息集合
	 */
	
	
	public void execute() throws GeneralException {
		
		StringBuffer reportinnercheckresult = new StringBuffer();
		
		/* hcm样式调整 报表管理-自动生成-提取数据  xiaoyun 2014-6-24 start */
		//StringBuffer result_table=new StringBuffer("<table width='75%' height='20' border='0' cellspacing='0'  align='left' cellpadding='0' class='ListTable'>");
		StringBuffer result_table=new StringBuffer("<table width='75%' height='20' border='0' cellspacing='0'  align='center' cellpadding='0' class='ListTable'>");
		/* hcm样式调整 报表管理-自动生成-提取数据  xiaoyun 2014-6-24 end */
		result_table.append("<thead><tr><td  colspan='2' heigth='25' align='left' class='TableRow' nowrap >");		
		result_table.append(ResourceFactory.getProperty("report_collect.reportInnerValidate")+"(");
		result_table.append(this.getCurrentDate());
		result_table.append(")");
		result_table.append("</td></tr>");
		
		//报表表内校验页面头信息
		//reportinnercheckresult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		//reportinnercheckresult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		//reportinnercheckresult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		//reportinnercheckresult.append(ResourceFactory.getProperty("reportInnerCheckResult.title")+"(");
		reportinnercheckresult.append(ResourceFactory.getProperty("report_collect.reportInnerValidate")+"(");
		reportinnercheckresult.append(this.getCurrentDate());
		reportinnercheckresult.append(")");
		reportinnercheckresult.append("<br>");
		reportinnercheckresult.append("<br>");
		
		//选中的列表集合
		ArrayList list = (ArrayList)this.getFormHM().get("selectedlist");
		
		//如果未选中抛出异常提示用户选择要校验的报表
		if(list == null || list.size()==0){
			Exception e = new Exception(ResourceFactory.getProperty("auto_fill_report.noValidateReport")+"！");
			throw GeneralExceptionHandler.Handle(e);
		}
		
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String checkunitcode = (String)hm.get("checkunitcode");
		String obj1 = (String)hm.get("obj1");
		String checkFlag = (String)hm.get("checkFlag");
		String print = (String)hm.get("print");
		this.getFormHM().put("checkflag",checkFlag);
		this.getFormHM().put("print",print);
		if("5".equals(print) && !"0".equals(checkFlag)){
			this.getFormHM().put("ischeck","show");
		}
		if("5".equals(print) && "0".equals(checkFlag)){
			this.getFormHM().put("ischeck","hidden");
		}
		
		//System.out.println("表内checkunitcode=" + checkunitcode);
		//----------------报表上报是否支持审批  编辑报表  zhaoxg 2013-2-17------------
		try {
		if(obj1!=null){//wangcq 2015-4-8
			if("2".equals(obj1)){
				if(isApprove(this.userView.getUserName())){
					this.userView=userView;
				}else{
					String username=approve(this.userView.getUserName());
					if(username==null|| "".equals(username)){
						username=this.userView.getUserName();
						userView=new UserView(username, this.frameconn); 
						userView.canLogin();
					}
					userView=new UserView(username, this.frameconn); 
					userView.canLogin();
				}
			}else if("1".equals(obj1)){
				String username=approve(this.userView.getUserName());
				if(username==null|| "".equals(username)){
					username=this.userView.getUserName();
					userView=new UserView(username, this.frameconn); 
					userView.canLogin();
				}
				userView=new UserView(username, this.frameconn); 
				userView.canLogin();
			}
		}

		} catch (Exception e) {
			e.printStackTrace();
		}
		//-----------------------------------------------------------------------
		StringBuffer temp = new StringBuffer();
		if(checkunitcode == null || "".equals(checkunitcode)){////编辑报表中和自动取数中的总效验
			
			//获得选中报表的表号
			int j=0;
			for(int i = 0 ; i< list.size(); i++){
				RecordVo vo = (RecordVo)list.get(i);
				String tabid = vo.getString("tabid"); //报表表号
				String name = vo.getString("name");   //用户名
				
				if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
				
				//报表表内校验 1 代表tb tb表中依据用户名判断数据
				ReportInnerCheck rc = new ReportInnerCheck(this.getFrameconn(),tabid,  1 ,userView.getUserName());
				String reportCheckValues = rc.reportInnerCheck();

				if(reportCheckValues == null || "".equals(reportCheckValues) ){
				}else{				
					temp.append(tabid); //报表表号 tname表中信息
					temp.append("&nbsp;");
					temp.append(name);  //报表名称
					temp.append("&nbsp;");
					temp.append("<br>");
					temp.append(reportCheckValues); //校验错误信息
					
					result_table.append("<tr class='"+(j%2==0?"trShallow":"trDeep")+"'>");
					result_table.append("<td align='center' valign='top' class='RecordRow' nowrap>");
					result_table.append(tabid);
					/* hcm样式调整 报表管理-自动生成-提取数据  xiaoyun 2014-6-24 start */
					result_table.append("</td><td align='left' class='RecordRow' nowrap>");
					//result_table.append("</td><td align='center' class='RecordRow' nowrap>");
					/* hcm样式调整 报表管理-自动生成-提取数据  xiaoyun 2014-6-24 end */
					result_table.append(name);  //报表名称
					result_table.append("&nbsp;");
					result_table.append("<br>");
					result_table.append(reportCheckValues); //校验错误信息
					result_table.append("</td></tr>");
					j++;
				}
				
			}
		}else{//报表汇总中的总效验
			result_table.setLength(0);
			/* 报表汇总-总校验-表内校验 xiaoyun 2014-6-30 start */
			//result_table.append("<table width='75%' height='20' border='0' cellspacing='1'  align='left' cellpadding='1' class='ListTable'>");
			result_table.append("<table width='75%' height='20' border='0' cellspacing='0'  align='center' cellpadding='0' class='ListTable'>");
			/* 报表汇总-总校验-表内校验 xiaoyun 2014-6-30 end */
			result_table.append("<thead><tr  heigth='25'  ><td  colspan='2' align='left' class='TableRow' nowrap style='border-bottom:none'>");
			result_table.append(ResourceFactory.getProperty("auto_fill_report.reportGatherInnerValidate")+"(");
			result_table.append(this.getCurrentDate());
			result_table.append(")");
			result_table.append("</td></tr>");
			
			
			
			reportinnercheckresult.delete(0,reportinnercheckresult.length());
			//reportinnercheckresult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
			//reportinnercheckresult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
			//reportinnercheckresult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
			reportinnercheckresult.append(ResourceFactory.getProperty("auto_fill_report.reportGatherInnerValidate")+"(");
			reportinnercheckresult.append(this.getCurrentDate());
			reportinnercheckresult.append(")");
			reportinnercheckresult.append("<br>");
			reportinnercheckresult.append("<br>");
			int j=0;
			for(int i = 0 ; i< list.size(); i++){
				RecordVo vo = (RecordVo)list.get(i);
				String tabid = vo.getString("tabid"); //报表表号
				ReportInnerCheck rc = new ReportInnerCheck(this.getFrameconn(),tabid,  2 ,checkunitcode);
				String reportCheckValues = rc.reportInnerCheck();
				if(reportCheckValues == null || "".equals(reportCheckValues) ){
				}else{		
					temp.append(tabid); //报表表号 tname表中信息
					temp.append("&nbsp;");
					temp.append(this.getTabName(this.getFrameconn(),tabid));  //报表名称
					temp.append("&nbsp;");
					temp.append("<br>");
					temp.append(reportCheckValues); //校验错误信息
					
					
					
					result_table.append("<tr class='"+(j%2==0?"trShallow":"trDeep")+"'>");
					result_table.append("<td align='center'  valign='top'  class='RecordRow' nowrap>");
					result_table.append(tabid);
					result_table.append("</td><td align='left' class='RecordRow' nowrap>");
					result_table.append(this.getTabName(this.getFrameconn(),tabid));  //报表名称
					result_table.append("&nbsp;");
					result_table.append("<br>");
					result_table.append(reportCheckValues); //校验错误信息
					result_table.append("</td></tr>");
					j++;
				}
				
			}
		}
		
		if(temp == null || "".equals(temp.toString())){
			//reportinnercheckresult.delete(0,reportinnercheckresult.length());
			//reportinnercheckresult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
			//reportinnercheckresult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
			//reportinnercheckresult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
			//reportinnercheckresult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
			reportinnercheckresult.append(ResourceFactory.getProperty("report_collect.reportInnerValidateSuccess")+"！");
			
			result_table.setLength(0);
			result_table.append(reportinnercheckresult.toString());
			
			this.getFormHM().put("downloadflag","hidden");
		}else{
			result_table.append("</table>");
			
			reportinnercheckresult.append(temp.toString());
			this.getFormHM().put("downloadflag","show");
		}
		this.getFormHM().put("reportInnerCheckResult" , reportinnercheckresult.toString());
		this.getFormHM().put("reportInnerCheckResult_t" , result_table.toString());
	}

	public String getTabName(Connection conn ,String tabid)throws GeneralException{
		ContentDAO dao = new ContentDAO(conn);
		String tbname = "";
		String sql =" select name from tname where tabid = " + tabid;
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				tbname =this.frowset.getString("name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return tbname;
		
	}
	

	/**
	 * 获得系统当前时间
	 * @return
	 */
	public String getCurrentDate(){
		Date currentTime = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss"); 
		return sdf.format(currentTime); 
	}
	/**
	 * 获取报批人信息   zhaoxg 2013-2-17
	 * @param userName
	 * @return
	 * @throws GeneralException 
	 * @throws SQLException 
	 */
	public String approve(String userName) throws GeneralException, SQLException{
		String approve = "";

		ResultSet rs = null;
		Connection conn = AdminDb.getConnection();	
		ContentDAO dao = null;
        dao = new ContentDAO(conn);
		try{
			String sql = "select appuser,username from treport_ctrl";
			rs = dao.search(sql.toString());
			while(rs.next()){
				String appuser = rs.getString("appuser");
				if(appuser!=null){
					String[] aa = appuser.split(";");
					for(int i=0;i<aa.length;i++){
						if(aa[i].equals(this.getFullName(userName))){
							approve = rs.getString("username");
						}
					}
				}

			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return approve;
	}
	/**
	 * 获取报表填报人姓名 zhaoxg 2013-2-17
	 * @param username
	 * @return
	 * @throws GeneralException 
	 * @throws SQLException 
	 */
	public String getFullName(String username) throws GeneralException, SQLException{
		String fullname = "";
		Connection conn = AdminDb.getConnection();	
		ContentDAO dao = null;
        dao = new ContentDAO(conn);
		ResultSet rs = null;
		try{

			String sql = "select fullname from operuser where username = '"+username+"'";
			rs = dao.search(sql.toString());
			if(rs.next()){
				fullname = rs.getString("fullname");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return fullname;
	}
	/**
	 * 判断当前用户是否负责报表  zhaoxg 2013-2-17
	 * @param username
	 * @return
	 * @throws GeneralException 
	 * @throws SQLException 
	 */
	public boolean isApprove(String username) throws GeneralException, SQLException{
		boolean isapprove = false;
		Connection conn = AdminDb.getConnection();	
		ContentDAO dao = null;
        dao = new ContentDAO(conn);
		ResultSet rs = null;
		try{

			String sql = "select username from operUser,tt_organization  where operUser.unitcode=tt_organization.unitcode";
			rs = dao.search(sql.toString());
			while(rs.next()){
				if(username.equals(rs.getString("username"))){
					isapprove = true;
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return isapprove;
	}
}
