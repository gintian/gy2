package com.hjsj.hrms.transaction.performance.workplan.workplanview;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;

/**
 * <p>Title:SearchAppiaryBodyTrans.java</p>
 * <p>Description>:查找报批的直接领导</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:jul 26, 2012 12:52:06 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class SearchAppiaryBodyNameTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		String curr_user = (String)this.getFormHM().get("curr_user");
		String pendingCode = (String)this.getFormHM().get("pendingCode");
		curr_user=SafeCode.decode(curr_user);
		String flag = (String)this.getFormHM().get("flag");
		String allStr = (String)this.getFormHM().get("allStr");
		allStr=SafeCode.decode(allStr);
		String a0101 = "";
		if(curr_user!=null && !"".equals(curr_user)){
			a0101 = getName(curr_user);
		}
		this.getFormHM().put("pendingCode", pendingCode);
		this.getFormHM().put("a0101", a0101);
		this.getFormHM().put("curr_user", SafeCode.encode(curr_user));
		this.getFormHM().put("flag", flag);
		this.getFormHM().put("allStr", SafeCode.encode(allStr));
	}

	/**
	 * 得到直接上级姓名
	 * @param a0100
	 * @param dao
	 * @return
	 */
	public String getName(String curr_user)
	{
		String sql="";
		RowSet rs = null;
		String nbase = curr_user.substring(0,3);
		String a0100 = curr_user.substring(3);
		String a0101 = "";
		sql = "select a0101 from "+nbase+"a01 where a0100 = '"+a0100+"'";
		
		try 
		{
		    ContentDAO dao = new ContentDAO(this.getFrameconn());	
			rs = dao.search(sql);
			if(rs.next())
			{
				
				a0101 = (String)rs.getString("a0101");
				
			}
			
		} catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return a0101;
	}	
	
}