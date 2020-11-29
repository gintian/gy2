package com.hjsj.hrms.transaction.report.actuarial_report.report_collect;

import com.hjsj.hrms.businessobject.report.actuarial_report.ActuarialReportBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class CollectActuarialReportDataTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String cycle_id=(String)this.getFormHM().get("cycle_id");
			String opt=(String)this.getFormHM().get("opt");
			ActuarialReportBo ab=new ActuarialReportBo(this.getFrameconn(),this.getUserView());
			if("1".equals(opt))
			{
				String unitcode=(String)this.getFormHM().get("unitcode");
				HashMap map =ab.getDescribe(cycle_id, unitcode);
				ab.collectReportData(unitcode,cycle_id,0,map);
			}
			else if("2".equals(opt)) //逐层汇总
			{
				ab.layerCollect(cycle_id);
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
