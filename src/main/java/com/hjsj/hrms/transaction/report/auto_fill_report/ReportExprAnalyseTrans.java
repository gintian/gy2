
package com.hjsj.hrms.transaction.report.auto_fill_report;

import com.hjsj.hrms.businessobject.report.ReportAnalyse;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 
 * <p>Title:表达式分析</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 21, 2006:8:36:57 AM</p>
 * @author zhangfengjin
 * @version 1.0
 *
 */
public class ReportExprAnalyseTrans extends IBusiness {

	//报表表达式分析
	public void execute() throws GeneralException {
		//选中报表的集合（RecordVo）类
		ArrayList list = (ArrayList)this.getFormHM().get("selectedlist");
		if(list == null || list.size()==0){
			Exception e = new Exception(ResourceFactory.getProperty("auto_fill_report.batchFillData.info7")+"！");
			throw GeneralExceptionHandler.Handle(e);
		}
		String username1 = "";
		try {
				if (isApprove1(this.userView.getUserName())) {
					username1 = this.userView.getUserName();
				} else {
					username1 = approve();// 不是负责人，找是不是有人报表给他
				}
				if(username1==null|| "".equals(username1)){
					username1 = this.userView.getUserName();
				}
				userView = new UserView(username1, this.frameconn);
				userView.canLogin();

		} catch (Exception e) {
			e.printStackTrace();
		}
		StringBuffer result = new StringBuffer();
		result.append("<table width='80%' border='0' cellspacing='0'  align='center' cellpadding='0'>");
		/* 报表管理-自动生成-提取数据-表达式分析 xiaoyun 2014-7-1 start */
		//result.append("<tr><td colspan='4' class='TableRow'>");
		result.append("<tr><td colspan='4' class='TableRow' style='padding-left:5px;'>");
		/*result.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		result.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		result.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		result.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		result.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		result.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		result.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		result.append("&nbsp;&nbsp;&nbsp;&nbsp;");*/		
		
		result.append(ResourceFactory.getProperty("auto_fill_report.expressionsReport"));
		result.append("</td></tr>");
		result.append("<tr><td>");
		result.append("<table  border='0' cellspacing='0'  width='100%' class='framestyle3' align='center' cellpadding='0'>");
		result.append("<tr><td colspan='4' style='padding-left:5px;' >");
	/*	result.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		result.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		result.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		result.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		result.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		result.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		result.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		result.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		result.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		result.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		result.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		result.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		result.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		result.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		result.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		result.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		result.append("&nbsp;&nbsp;&nbsp;&nbsp;");*/
		/* 报表管理-自动生成-提取数据-表达式分析 xiaoyun 2014-7-1 end */
		result.append(ResourceFactory.getProperty("reportfieldanalysetrans.builddate")+": ");
		result.append(this.getCurrentDate());
		result.append("  ");
		result.append("<br>");
		
		//获得选中报表的表号
		for(int i = 0 ; i< list.size(); i++){
			RecordVo vo = (RecordVo)list.get(i);
			String tabid = vo.getString("tabid");
			if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
			String name = vo.getString("name");
			ReportAnalyse ra = new ReportAnalyse(this.getFrameconn(),tabid,name);
			ra.fieldAnalyse();//指标分析
			ra.exprAnalyse();//表达式分析
			result.append(ra.getReportExprAnalyseResult());
		}
	    
		result.append("</td></tr>");
		result.append("</table>");
		result.append("</table>");
		this.getFormHM().put("reportExprAnalyseResult" , result.toString());

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
	 * 判断当前用户是否负责报表  zhaoxg 2013-2-17
	 * @param username
	 * @return
	 * @throws GeneralException 
	 * @throws SQLException 
	 */
	public boolean isApprove1(String username) throws GeneralException, SQLException{
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
	/**
	 * 获取报批人信息   zhaoxg 2013-2-17
	 * @param userName
	 * @return
	 * @throws GeneralException 
	 * @throws SQLException 
	 */
	public String approve() throws GeneralException, SQLException{
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
						if(aa[i].equals(this.userView.getUserFullName())){
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
	 * 当前登录用户有没有报批权限
	 */
	public boolean isApp(String tabid,String unitcode,String username){
		boolean isapp = false;
		try{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String sql = "select currappuser,status from treport_ctrl where tabid = '"+tabid+"' and unitcode = '"+unitcode+"'";
			RowSet rs = dao.search(sql);
			if(rs.next()){
				String user = rs.getString("currappuser");
				String status = rs.getString("status");
				if(username.equals(user)){
					isapp = true;
				}
				if(user==null|| "".equals(user)){
					if(username.equals(this.userView.getUserName())){
						isapp = true;
					}else{
						isapp = false;
					}
				}
				if("1".equals(status)){
					isapp = false;
				}
			}
			
		}catch (Exception e)
        {
            e.printStackTrace();
        }
		return isapp;
	}
}
