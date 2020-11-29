package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.report.actuarial_report.ActuarialReportBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class Validate4Trans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String id=(String)this.getFormHM().get("id");
			String opt=(String)this.getFormHM().get("opt");
			String unitcode=(String)this.getFormHM().get("unitcode");
			String current_values=(String)this.getFormHM().get("current_values");			
			ActuarialReportBo ab=new ActuarialReportBo(this.getFrameconn(),this.getUserView());		
			String info = ab.validateU04Values(unitcode,id,current_values);	
			this.getFormHM().put("info", info);
			this.getFormHM().put("opt", opt);
			this.getFormHM().put("values", current_values);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
