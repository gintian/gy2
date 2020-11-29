package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.report.ReportExcelBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExportExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String tabid=(String)this.getFormHM().get("tabid");
			String username=SafeCode.decode((String)this.getFormHM().get("username"));
			if(username==null|| "".equals(username)){
				username = this.userView.getUserName();
			}
//			if(isApprove(this.userView.getUserName())){
//				this.userView=userView;
//			}else{
//				String username=approve(this.userView.getUserName());
//				if(username==null||username.equals("")){
//					username=this.userView.getUserName();
//					userView=new UserView(username, this.frameconn); 
//					userView.canLogin();
//				}
				userView=new UserView(username, this.frameconn); 
				userView.canLogin();
//			}
			if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
			String operateObject=(String)this.getFormHM().get("operateObject");
			String unitcode=(String)this.getFormHM().get("unitcode");
			String reverseSql=(String)this.getFormHM().get("reverseSql");
			ReportExcelBo bbo = null;
			if(reverseSql!=null){
				reverseSql = PubFunc.decrypt(reverseSql);  //add by wangchaoqun on 2014-9-29
				reverseSql=PubFunc.keyWord_reback(reverseSql);
			}
			if(username==null|| "".equals(username)){
				bbo=new ReportExcelBo(this.getUserView(),tabid,unitcode,operateObject,this.getFrameconn());
			}else{
				bbo=new ReportExcelBo(this.getUserView(),username,tabid,unitcode,operateObject,this.getFrameconn());
			}
			
			if("5".equals(operateObject)) //反查表
			{
				bbo.setReverseSql(reverseSql);
				bbo.setSetMap_str((String)this.getFormHM().get("setMap_str"));
				bbo.setFieldItem_str((String)this.getFormHM().get("fieldItem_str"));
				bbo.setScanMode((String)this.getFormHM().get("scanMode"));
			}
			String outName=PubFunc.encrypt(bbo.executReportExcel());  //update by wangchaoqun on 2014-9-15 
//			outName=outName.replaceAll(".xls","#");
			this.getFormHM().put("outName",outName);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

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
