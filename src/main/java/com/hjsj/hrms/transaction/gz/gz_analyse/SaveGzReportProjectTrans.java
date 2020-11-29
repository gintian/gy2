package com.hjsj.hrms.transaction.gz.gz_analyse;

import com.hjsj.hrms.businessobject.gz.gz_analyse.GzAnalyseBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveGzReportProjectTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String right_fields=(String)this.getFormHM().get("selectedids");
			right_fields = right_fields.replaceAll("／", "/");
			String rsname=(String)this.getFormHM().get("rsname");
			String rsdtlid=(String)this.getFormHM().get("rsdtlid");
			String reportTabId=(String)this.getFormHM().get("reportTabId");
			String bgroup=(String)this.getFormHM().get("bgroup");
			if(bgroup==null|| "".equals(bgroup))
				bgroup="0";
			String ownerType=(String)this.getFormHM().get("ownerType");
			GzAnalyseBo bo = new GzAnalyseBo(this.getFrameconn(),this.userView);
			rsdtlid=bo.updateOrInsert(reportTabId, rsdtlid, rsname, right_fields,bgroup,ownerType);
			this.getFormHM().put("rsdtlid",rsdtlid);
			this.getFormHM().put("reportTabId", reportTabId);
			this.getFormHM().put("rsname",rsname);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
