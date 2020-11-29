package com.hjsj.hrms.transaction.gz.gz_analyse;

import com.hjsj.hrms.businessobject.gz.gz_analyse.GzAnalyseBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class DeleteGzAnalyseTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String rsid=(String)this.getFormHM().get("rsid");
			String rsdtlid=(String)this.getFormHM().get("rsdtlid");
			GzAnalyseBo bo = new GzAnalyseBo(this.getFrameconn());
			bo.deleteReportDetail(rsdtlid, rsid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
