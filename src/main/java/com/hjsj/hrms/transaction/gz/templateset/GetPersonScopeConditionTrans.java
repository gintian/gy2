package com.hjsj.hrms.transaction.gz.templateset;

import com.hjsj.hrms.businessobject.gz.SalaryPropertyBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class GetPersonScopeConditionTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
		
			String salaryid=(String)this.getFormHM().get("salaryid");
			SalaryPropertyBo bo=new SalaryPropertyBo(this.getFrameconn(),salaryid,0,this.getUserView());
			String    condStr=bo.getVo().getString("cond");
			if(condStr==null)
				condStr="";
			String    cexpr=bo.getVo().getString("cexpr");
			if(cexpr==null)
				cexpr="";		
			this.getFormHM().put("condStr",SafeCode.encode(condStr));
			this.getFormHM().put("cexpr",SafeCode.encode(cexpr));
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
