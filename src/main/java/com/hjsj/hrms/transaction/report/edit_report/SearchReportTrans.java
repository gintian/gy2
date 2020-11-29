/*
 * Created on 2006-3-27
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.report.edit_report;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchReportTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		//报表汇总/总效验用
		//sortId=-1&print=5&checkFlag=2&unitcode='+unitcode
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String temp =hm.get("sortId")==null?"":(String)hm.get("sortId");
		String unitcode="";
		String reptype="";
		reptype =hm.get("reptype")==null?"1":(String)hm.get("reptype");
		hm.clear();
		String flag ="";
		String username1 = "";
		try {
				if (isApprove1(this.userView.getUserName())) {
					username1 = this.userView.getUserName();
				} else {
					username1 = approve();// 不是负责人，找是不是有人报表给他
				}
				userView = new UserView(username1, this.frameconn);
				userView.canLogin();

		} catch (Exception e) {
			e.printStackTrace();
		}
		StringBuffer sql = new StringBuffer();
		String sortId = (String) (this.getFormHM().get("sid")); //报表类别
		if("".equals(temp)){
			sortId="-1";
			this.getFormHM().put("sortId", "-1");
		}else{
			this.getFormHM().put("sortId", temp);
		}
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());

		// 提取数据页面中的报表分类也是根据用户过滤，仅列出登录用户负责的报表分类;
		//String unitcode="";
		sql.delete(0, sql.length());
		sql.append("select reporttypes,report,unitcode from tt_organization where unitcode = (select unitcode from operuser where username = '");
		sql.append(userView.getUserName());
		sql.append("')");

		String reportTypes = "";   //当前用户负责的报表类别
		String report="";
		try {
			this.frowset = dao.search(sql.toString());
			if (this.frowset.next()) {
				reportTypes = (String) this.frowset.getString("reporttypes");
				report=Sql_switcher.readMemo(this.frowset,"report");
				unitcode=this.frowset.getString("unitcode");
				if (reportTypes == null || "".equals(reportTypes)) {
					// 用户没有权限操作任何报表
					Exception e = new Exception(ResourceFactory.getProperty("report.usernotreport"));
					throw GeneralExceptionHandler.Handle(e);
				} else {
					if (reportTypes.charAt(reportTypes.length() - 1) == ',') {
						reportTypes = reportTypes.substring(0, reportTypes.length() - 1);
					}

				}

			} else {
					//从资源里找
//				if(sortId.equals("-1")){
				sql.delete(0, sql.length());
				sql.append("select TSortId sortid,Name sortname from TSort");
				this.frowset = dao.search(sql.toString());
				while(this.frowset.next())
	    		{
	    			reportTypes+=this.frowset.getString("sortid")+",";
	    		}
				if (reportTypes.charAt(reportTypes.length() - 1) == ',') {
					reportTypes = reportTypes.substring(0, reportTypes.length() - 1);
				}
//				}
//				SysPrivBo privbo=null;
//				if(userView.getStatus()==4) //自助用户关联业务用户
//				{
//					privbo=new SysPrivBo(userView.getDbname()+""+userView.getUserId(),"4",this.getFrameconn(),"warnpriv");
//				}else{
//					privbo=new SysPrivBo(userView.getUserName(),"0",this.getFrameconn(),"warnpriv");
//				}
//				String res_str=privbo.getWarn_str();
//				 if(res_str!=null&&res_str.indexOf("<Report>")!=-1){
//					 report =  res_str.substring(res_str.indexOf("<Report>")+8,res_str.indexOf("</Report>"));
//				 }
//				ResourceParser parser=new ResourceParser(res_str,1);
//				/**1,2,3*/
//				String str_content=","+parser.getContent()+",";
				sql.delete(0, sql.length());
				sql.append("select tabid  from tname");
				this.frowset = dao.search(sql.toString());
				while(this.frowset.next())
	    		{
				
				if(userView.isHaveResource(IResourceConstant.REPORT,this.frowset.getString("tabid")))
				{
					report+=	this.frowset.getString("tabid")+",";
				}
	    		}
				flag="1";
//				if(report.equals("")&&!userView.isSuper_admin()){
//					// 用户没有权限操作任何报表
//					Exception e = new Exception(ResourceFactory.getProperty("report.usernotreport"));
//					throw GeneralExceptionHandler.Handle(e);
//				}
				}

		
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		sql.delete(0, sql.length());
		if ("-1".equals(sortId)) {  //-1时表明用户选择了全部列表 或者是用户点击提取数据（第一次进入）
			sql.append("select tname.tabid ,tname.name,treport_ctrl.status from tname,treport_ctrl where  tname.tabid=treport_ctrl.tabid and  tname.tsortid in ("
					+ reportTypes + ")");
		} else {
			sql.append("select tname.tabid ,tname.name,treport_ctrl.status  from tname,treport_ctrl where  tname.tabid=treport_ctrl.tabid and  tname.tsortid =  ");
			sql.append(sortId);
		}
		sql.append(" and treport_ctrl.unitcode='"+unitcode+"'");
		sql.append(" order by tname.tabid");
		
		report=","+report;
		try {
			if("1".equals(flag)){
				if(userView.isSuper_admin()){
					sql.delete(0, sql.length());
					if ("-1".equals(sortId)) {  //-1时表明用户选择了全部列表 或者是用户点击提取数据（第一次进入）
					sql.append("select tabid,name,paper,tsortid  from tname  ");
					}else{
					sql.append("select tabid,name,paper,tsortid  from tname where tsortid="+sortId+"   ");
					}
				}else{
				report = report.replace(" ", "");
				report = report.replace("R", "");
				while(report.indexOf(",,")!=-1){
					report = report.replace(",,", ",");
				}
				if (report.length()>0&&report.charAt(report.length() - 1) == ',') {
					report = report.substring(0, report.length() - 1);
				}
				if (report.length()>0&&report.charAt(0) == ',') {
					report = report.substring(1, report.length());
				}
				if(report.length()==0){
					// 用户没有权限操作任何报表
					Exception e = new Exception(ResourceFactory.getProperty("report.usernotreport"));
					throw GeneralExceptionHandler.Handle(e);
				}
					
				sql.delete(0, sql.length());
				if ("-1".equals(sortId)) {  //-1时表明用户选择了全部列表 或者是用户点击提取数据（第一次进入）
				sql.append("select tabid,name,paper,tsortid  from tname where tabid in ("+report+")");
				}else{
					sql.append("select tabid,name,paper,tsortid  from tname where tabid in ("+report+") and tsortid="+sortId+" ");
				}
				}
				if("-1".equals(sortId)){
					reportTypes="";
					}
			}
			this.frowset = dao.search(sql.toString());
			TnameBo tnamebo  = new TnameBo(this.getFrameconn());
			HashMap scopeMap = tnamebo.getScopeMap();
			while (this.frowset.next()) {
				/*isHaveResource(int type,String res_id)
				  type为资源类型参数
				  res_id资源号
				  超级用户返回真
				*/
				String tabid = String.valueOf(this.frowset.getInt("tabid"));
				if("1".equals(reptype)&&scopeMap!=null&&scopeMap.get(tabid)!=null&& "1".equals(scopeMap.get(tabid)))
					continue;
				if("1".equals(flag)){

						if(!this.getUserView().isHaveResource(IResourceConstant.REPORT,tabid))
							continue;
						RecordVo vo = new RecordVo("tname");
						vo.setString("tabid", tabid);
						vo.setString("name", this.frowset.getString("name"));
						vo.setInt("paper", -1);   //报表状态，因历史原因，将其放入paper字段
						list.add(vo);
						if("-1".equals(sortId)){
							if(reportTypes.indexOf(""+this.frowset.getInt("tsortid"))==-1)
						reportTypes+=this.frowset.getInt("tsortid")+",";
						}
					
					
				}else{
				if(report.indexOf(","+tabid+",")==-1)
				{
					if(!"1".equals(reptype)&&!this.getUserView().isHaveResource(IResourceConstant.REPORT,tabid))
						continue;
						RecordVo vo = new RecordVo("tname");
						vo.setString("tabid", tabid);
						vo.setString("name", this.frowset.getString("name"));
						vo.setInt("paper", this.frowset.getInt("status"));   //报表状态，因历史原因，将其放入paper字段
						list.add(vo);
					
				}
				}
			}
			if (!"".equals(reportTypes)&&reportTypes.charAt(reportTypes.length() - 1) == ',') {
				reportTypes = reportTypes.substring(0, reportTypes.length() - 1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		if(this.getFormHM().get("reportTypes")!=null&&this.getFormHM().get("reportTypes").toString().length()>0)
			reportTypes = 	(String)this.getFormHM().get("reportTypes");
		if(list.size()== 0&& "-1".equals(sortId)){
			//用户没有权限操作任何报表
			Exception e = new Exception(ResourceFactory.getProperty("report.usernotreport"));
			throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("sql","select tsortid , name from tsort where tsortid in ("+ reportTypes + ")");
		this.getFormHM().put("reportlist", list);
		this.getFormHM().put("reportTypes", reportTypes);
		//
		this.getFormHM().put("userid", userView.getUserId());
		this.getFormHM().put("username", userView.getUserName());
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
