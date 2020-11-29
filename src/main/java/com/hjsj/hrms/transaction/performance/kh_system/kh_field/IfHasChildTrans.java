package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 
 * <p>Title:IfHasChildTrans.java</p>
 * <p>Description>:IfHasChildTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-10-27 下午06:51:26</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class IfHasChildTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			String pointsetid=(String)this.getFormHM().get("pointsetid");
			String subsys_id=(String)this.getFormHM().get("subsys_id");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			StringBuffer buf = new StringBuffer("");
			buf.append("select pointsetid,pointsetname from per_pointset where ");
			String msg="0";
			if("root".equalsIgnoreCase(pointsetid))
			{
				buf.append(" parent_id is null ");
			}else
			{
				buf.append(" UPPER(parent_id) = '");
				buf.append(pointsetid.toUpperCase()+"' ");
			}
			buf.append(" and subsys_id = '"+subsys_id+"' ");
			this.frowset=dao.search(buf.toString());
			while(this.frowset.next())
			{
				msg="1";
				break;
			}
			this.getFormHM().put("subsys_id", subsys_id);
			this.getFormHM().put("msg",msg);
			this.getFormHM().put("pointsetid",pointsetid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
