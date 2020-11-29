package com.hjsj.hrms.transaction.report.edit_report;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class AdjustFormulaOrderTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String upExpid=(String)this.getFormHM().get("upExpid");
			String[] a_upExpid=upExpid.split("§§");
			String downExpid=(String)this.getFormHM().get("downExpid");
			String[] a_downExpid=downExpid.split("§§");
			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			dao.update("update tformula set expid=100001 where expid="+a_upExpid[0]);
			dao.update("update tformula set expid="+a_upExpid[0]+" where expid="+a_downExpid[0]);
			dao.update("update tformula set expid="+a_downExpid[0]+" where expid=100001");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
