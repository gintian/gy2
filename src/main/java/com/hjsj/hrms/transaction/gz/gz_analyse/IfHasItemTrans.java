/**
 * <p>Title:IfHasItemTrans.java</p>
 * <p>Description>:IfHasItemTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-11-15 下午04:33:25</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
package com.hjsj.hrms.transaction.gz.gz_analyse;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:IfHasItemTrans.java</p>
 * <p>Description>:IfHasItemTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-11-15 下午04:33:25</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class IfHasItemTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String rsid=(String)this.getFormHM().get("rsid");
			String rsdtlid=(String)this.getFormHM().get("rsdtlid");
			String msg="1";
			if("8".equals(rsid)|| "9".equals(rsid)|| "17".equals(rsid))
			{
				msg="2";
			}else
			{
		    	StringBuffer buf = new StringBuffer("");
		    	buf.append("select reportitem.* from reportitem ,fielditem where reportitem.rsdtlid="+rsdtlid+" and fielditem.itemid=reportitem.itemid");
		    	ContentDAO dao = new ContentDAO(this.getFrameconn());
		    	this.frowset=dao.search(buf.toString());
		    	while(this.frowset.next())
		    	{
		    		msg="2";
		    		break;
		    	}
			}
	    	this.getFormHM().put("msg", msg);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
