package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.report.actuarial_report.ActuarialReportBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveReport5DescTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String id=(String)this.getFormHM().get("id");
			String t5_desc=(String)this.getFormHM().get("t5_desc");
			String unitcode=(String)this.getFormHM().get("unitcode");
			ActuarialReportBo ab=new ActuarialReportBo(this.getFrameconn(),this.getUserView());
			ab.saveDesc(unitcode,Integer.parseInt(id),"t5_desc",t5_desc);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
