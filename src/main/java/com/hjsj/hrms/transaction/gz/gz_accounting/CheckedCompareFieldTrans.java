package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class CheckedCompareFieldTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String msg = "0";
			String salaryid= (String)this.getFormHM().get("salaryid");
			String gz_module=(String)this.getFormHM().get("gz_module");
			String flow_flag=(String)this.getFormHM().get("flow_flag");
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			ArrayList list = gzbo.getField(SalaryCtrlParamBo.COMPARE_FIELD);
			if(list==null||list.size()<=0)
			{
				msg="1";
			}
			this.getFormHM().put("msg",msg);
			this.getFormHM().put("salaryid", salaryid);
			this.getFormHM().put("gz_module", gz_module);
			this.getFormHM().put("flow_flag", flow_flag);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
