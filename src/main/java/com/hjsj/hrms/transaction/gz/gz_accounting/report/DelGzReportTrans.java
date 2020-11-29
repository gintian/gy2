package com.hjsj.hrms.transaction.gz.gz_accounting.report;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 * 
 *<p>Title:DelGzReportTrans.java</p> 
 *<p>Description:删除工资报表</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 22, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class DelGzReportTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String rsdtlid=(String)this.getFormHM().get("rsdtlid");
			String rsid=(String)this.getFormHM().get("rsid");
			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			dao.delete("delete from reportdetail where rsid="+rsid+"  and rsdtlid="+rsdtlid,new ArrayList());
			dao.delete("delete from reportitem where  rsdtlid="+rsdtlid,new ArrayList());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
