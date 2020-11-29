package com.hjsj.hrms.transaction.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 角色另存为
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 31, 2008</p> 
 *@author sxin
 *@version 5.0
 */
public class CheckOtherNameTrans extends IBusiness {


	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String other_name=(String)this.getFormHM().get("other_name");
		StringBuffer strsql=new StringBuffer();
		strsql.append("select role_id,role_name,role_desc,role_property,valid,status from t_sys_role ");
		strsql.append(" where role_name='"+other_name+"'");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String flag="1";
		try
		{
			this.frowset=dao.search(strsql.toString());
			if(this.frowset.next())
				flag="2";
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		this.getFormHM().put("flag", flag);
		this.getFormHM().put("other_name", other_name);
	}
	
}


