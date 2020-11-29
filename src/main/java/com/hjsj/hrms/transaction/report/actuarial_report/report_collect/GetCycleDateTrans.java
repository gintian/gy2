package com.hjsj.hrms.transaction.report.actuarial_report.report_collect;

import com.hjsj.hrms.businessobject.report.actuarial_report.ActuarialReportBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class GetCycleDateTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ActuarialReportBo ab=new ActuarialReportBo(this.getFrameconn(),this.getUserView());
			ArrayList cycleList=ab.getCycleList(1);
			String cycle_id="";
			if(cycleList.size()>0)
			{
				cycle_id=((CommonData)cycleList.get(0)).getDataValue();
			}
			
			this.getFormHM().put("cycle_id",cycle_id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
