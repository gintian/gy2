package com.hjsj.hrms.transaction.performance.workplan.workplanstatus;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:WorkplanGobackTrans.java</p>
 * <p>Description:填报状态从下级返回上级</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-07-17 11:28:36</p>
 * @author JinChunhai
 * @version 6.0
 */

public class WorkplanGobackTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		try
		{
			String opt = (String)this.getFormHM().get("opt");
			String unitcode = (String)this.getFormHM().get("unitcode");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if("back".equals(opt))
			{						
				StringBuffer sqlstr = new StringBuffer();							
				sqlstr.append("select parentid from organization where codeitemid = '"+ unitcode +"' and parentid not in ");
				sqlstr.append("(select codeitemid from organization where codeitemid = parentid ) ");								
				this.frowset = dao.search(sqlstr.toString());
				String parent_unitcode = "";
				if(this.frowset.next())
					parent_unitcode = this.frowset.getString("parentid");
				else
					parent_unitcode = "init";
				this.getFormHM().put("parent_unitcode",parent_unitcode);								
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}