package com.hjsj.hrms.transaction.gz.gz_analyse.historydata;

import com.hjsj.hrms.businessobject.gz.gz_analyse.HistoryDataBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class HistoryStrutIsChangeTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HistoryDataBo bo = new HistoryDataBo(this.getFrameconn(),this.userView);
			boolean flag = bo.strutIsChange();
			bo.syncSalaryTaxArchiveStrut();
			this.getFormHM().put("ot",(String)this.getFormHM().get("ot"));
			this.getFormHM().put("type",(String)this.getFormHM().get("type"));
			this.getFormHM().put("startDate",(String)this.getFormHM().get("startDate"));
			this.getFormHM().put("endDate",(String)this.getFormHM().get("endDate"));
			this.getFormHM().put("id",(String)this.getFormHM().get("id"));
			this.getFormHM().put("msg",(flag?"1":"0"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
