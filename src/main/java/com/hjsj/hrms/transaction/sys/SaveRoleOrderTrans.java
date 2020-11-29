package com.hjsj.hrms.transaction.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 *<p>Title:SaveRoleOrderTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Feb 17, 2009:4:17:16 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class SaveRoleOrderTrans extends IBusiness {
	
	public void execute() throws GeneralException {

		String [] role_list=(String [])this.getFormHM().get("role_list");
		if(role_list==null||role_list.length<=0)
		{
		  return;	
		}
		
		for(int i=0;i<role_list.length;i++)//(int i=role_list.length-1;i>=0;i--)
	    {  
	 		try
			{
				ContentDAO dao = new ContentDAO(this.getFrameconn());
		 		String sql = "update t_sys_role set norder = "+(i+1)+" where role_id = "+role_list[i];
		 		dao.update(sql);
				}
		        catch(Exception ex)
				{
		        	ex.printStackTrace();
				}
	    }
		this.getFormHM().put("oqname", "");
		this.getFormHM().put("order_name", "");
    	this.getFormHM().put("oqroleproperty", "");
		
	}

}
