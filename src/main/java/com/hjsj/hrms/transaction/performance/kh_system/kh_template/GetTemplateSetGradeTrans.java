package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
public class GetTemplateSetGradeTrans extends IBusiness{

	private UserView userView = null;
	private Connection con = null;
	public void execute() throws GeneralException {
		try
		{
			con = this.getFrameconn();
			userView = this.getUserView();
			String parentid = (String)this.getFormHM().get("parentid");
			String subsys_id =(String)this.getFormHM().get("subsys_id");
			String currid = parentid;
			StringBuffer buf = new StringBuffer("");
			String orgLink = getOrgLinkStr(buf,parentid,currid, subsys_id,con,userView);
			orgLink = orgLink.substring(0, orgLink.length()-1);
			this.getFormHM().put("parentid", parentid);
			this.getFormHM().put("orgLink", orgLink);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	public String getOrgLinkStr(StringBuffer buf,String parentid,String currid,String subsys_id,Connection con,UserView userView){
		if(parentid.equals(currid)){
			buf.append(currid+"/");
		}
		RowSet rs = null;
		try {
			con = this.getFrameconn();
			ContentDAO dao = new ContentDAO(con);
			String sql = "select * from per_template_set where template_setid ='"+currid+"' and parent_id is not null and subsys_id='"+subsys_id+"'";
//			if(!this.userView.isAdmin() && !this.userView.getGroupId().equals("1"))
//			{
//				String b0110 = KhTemplateBo.getyxb0110(userView, this.getFrameconn());
//				sql+=" and (b0110='"+b0110+"' or b0110='HJSJ' )";
//			}
			rs = dao.search(sql);
			if(rs.next()){
				boolean flag = false;
				String b0110 = (String)rs.getString("b0110");
 				if(!userView.isAdmin() && !"1".equals(userView.getGroupId()))
 				{
 					if(b0110!=null && !"".equalsIgnoreCase(b0110) && "HJSJ".equalsIgnoreCase(b0110)){
 						flag=true;
 					}else{
 						if(!(b0110.length()>KhTemplateBo.getyxb0110(userView, con).length()?b0110.substring(0, KhTemplateBo.getyxb0110(userView, con).length()):b0110).equalsIgnoreCase(KhTemplateBo.getyxb0110(userView, con))){
 							flag=true;
 						}
 					}
 				}else{
 					flag=true;
 				}
 				if(flag){
 					int parent_id = rs.getInt("parent_id");
					if(parent_id!=Integer.parseInt(parentid)){
						buf.append(String.valueOf(parent_id)+"/");
					}
					if(parent_id>0){
						currid=String.valueOf(parent_id);
						if(hasParent(currid,subsys_id,con,userView)){
							getOrgLinkStr(buf,parentid,currid, subsys_id,con,userView);
						}
					}
 				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null)
				{
					rs.close();
				}
				/*if(con!=null)
				{
					con.close();
				}*/
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return buf.toString();
	}
	public boolean hasParent(String currid,String subsys_id,Connection con,UserView userView){
		RowSet rs = null;
		boolean flag = false;
		try {
			con = this.getFrameconn();
			ContentDAO dao = new ContentDAO(con);
			String sql = "select * from per_template_set where template_setid ='"+currid+"' and parent_id is not null and subsys_id='"+subsys_id+"'";
//			if(!this.userView.isAdmin() && !this.userView.getGroupId().equals("1"))
//			{
//				String b0110 = KhTemplateBo.getyxb0110(userView, this.getFrameconn());
//				sql+=" and (b0110='"+b0110+"' or b0110='HJSJ' )";
//			}
			rs = dao.search(sql);
			if(rs.next()){
				String b0110 = (String)rs.getString("b0110");
 				if(!userView.isAdmin() && !"1".equals(userView.getGroupId()))
 				{
 					if(b0110!=null && !"".equalsIgnoreCase(b0110) && "HJSJ".equalsIgnoreCase(b0110)){
 						flag=true;
 					}else{
 						if(!(b0110.length()>KhTemplateBo.getyxb0110(userView, con).length()?b0110.substring(0, KhTemplateBo.getyxb0110(userView, con).length()):b0110).equalsIgnoreCase(KhTemplateBo.getyxb0110(userView, con))){
 							flag=true;
 						}
 					}
 				}else{
 					flag=true;
 				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null)
				{
					rs.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return flag;
	}

}
