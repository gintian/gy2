package com.hjsj.hrms.transaction.report.org_maintenance;


import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


public class ReportUserListTrans extends IBusiness {
	
	ArrayList list = new ArrayList();
	String unitcode ="";
	
	//显示报表负责人
	public void execute() throws GeneralException {
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String usUnitCode = (String) hm.get("usunitcode");
		
		String sql1 = "select unitcode from tt_organization where unitcode= '" + usUnitCode +"'";
		try {
			this.frowset=dao.search(sql1);
			if(this.frowset.next()){
				unitcode=this.frowset.getString("unitcode");
			}
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
		
		StringBuffer  sql = new StringBuffer();
		if(userView.isSuper_admin()){//roleid = 0 用户 1 组
			list.clear();
			//系统管理员显示所有用户
			sql.append("select username ,unitcode  from operuser where roleid =0 ");
			try {
				this.frowset = dao.search(sql.toString());
				while (this.frowset.next()) {
					RecordVo vo = new RecordVo("operuser");

					vo.setString("username", this.frowset.getString("username"));	
					//获得用户表中设置的填报单位编码
					String temp = this.frowset.getString("unitcode");
					if(temp == null){
						vo.setString("userflag", "0");
					}else{
						if(temp.equals(unitcode)){
							vo.setString("userflag", "1");//选中状态
						}else{
							vo.setString("userflag", "0");
						}
					}
					
					list.add(vo);
				}

			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
		}else{
			list.clear();
			//当前用户组ID
			String strsql="select groupid from operuser where username = '"+userView.getUserName()+"'";
			String groupId = this.getGroupId(strsql);
			
			//普通用户显示本组用户列表(本级不包括当前用户的所有用户)
			sql.append("select username ,unitcode ,roleid from operuser where groupid = '");
			sql.append(groupId);
			sql.append("'  and username <> '" + userView.getUserName() +"'");
			
			try {
				this.frowset = dao.search(sql.toString());
				while (this.frowset.next()) {
					String roleid = this.frowset.getString("roleid");
					if("1".equalsIgnoreCase(roleid)){//用户组
						String userName = this.frowset.getString("username");
						String groupid = this.getGroupIds(userName);
						dg(groupid);
					}else{
						RecordVo vo = new RecordVo("operuser");
						
						vo.setString("username", this.frowset.getString("username"));	
						
						//获得用户表中设置的填报单位编码
						String temp = this.frowset.getString("unitcode");
						
						if(temp == null){
							vo.setString("userflag", "0");
						}else{
							if(temp.equals(unitcode)){
								vo.setString("userflag", "1");//选中状态
							}else{
								vo.setString("userflag", "0");
							}
						}
						list.add(vo);
					}
					
				}

			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			
			
		}
		this.getFormHM().put("reportuserlist", list);
		this.getFormHM().put("usunitcode",usUnitCode);
	}
	
	public void dg(String groupid){
		StringBuffer sql = new StringBuffer();
		sql.append("select username ,unitcode ,roleid from operuser where groupid = '");
		sql.append(groupid);
		sql.append("'");
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			RowSet rs  = dao.search(sql.toString());
			while(rs.next()){
				String roleid = rs.getString("roleid");
				if("1".equalsIgnoreCase(roleid)){//用户组
					String userName = rs.getString("username");
					String gid = this.getGroupIds(userName);
					dg(gid);
				}else{
					RecordVo vo = new RecordVo("operuser");
					
					vo.setString("username", rs.getString("username"));	
					
					//获得用户表中设置的填报单位编码
					String temp = rs.getString("unitcode");
					
					if(temp == null){
						vo.setString("userflag", "0");
					}else{
						if(temp.equals(unitcode)){
							vo.setString("userflag", "1");//选中状态
						}else{
							vo.setString("userflag", "0");
						}
					}
					list.add(vo);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 根据组名获取组ID
	 * @param groupName
	 * @return
	 */
	public String getGroupIds(String groupName){
		String groupId = "";
		StringBuffer sql = new StringBuffer();
		sql.append("select groupid  from usergroup where groupname='");
		sql.append(groupName);
		sql.append("'");
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			RowSet rss  = dao.search(sql.toString());
			if(rss.next()){
				groupId = rss.getString("groupid");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return groupId;		
	}
	
	
	
	/**
	 * 获得当前用户的组ID
	 * @param sql
	 * @return
	 */
	public String getGroupId(String sql){
		String groupId = "";
		//System.out.println(sql);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			RowSet rs1  = dao.search(sql);
			if(rs1.next()){
				groupId = rs1.getString("groupid");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return groupId;
	}
}