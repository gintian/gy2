package com.hjsj.hrms.transaction.general.template;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:验证角色属性是否为 为“9，10，11，12，13”</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 20, 2009</p> 
 *@author dengcan
 *@version 4.2
 */
public class ValidateRolePropertyTrans extends IBusiness {


	public void execute() throws GeneralException {
		
		try
		{
			String flag="0";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String value=(String)this.getFormHM().get("value");
			String tabid=(String)this.getFormHM().get("tabid");
			RowSet rowSet=dao.search("select * from t_sys_role where role_id='"+value+"'");
			if(rowSet.next())
			{	
				int role_property=rowSet.getInt("role_property");
				if(role_property==9||role_property==10||role_property==11||role_property==12||role_property==13||role_property==14)
					flag=String.valueOf(role_property);
			}
			
			
			this.getFormHM().put("flag", flag);
			this.getFormHM().put("role_id", value);
			this.getFormHM().put("tabid", tabid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		 

	}

}
