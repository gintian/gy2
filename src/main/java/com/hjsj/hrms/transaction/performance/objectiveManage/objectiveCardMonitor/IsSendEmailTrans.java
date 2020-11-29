package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCardMonitor;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Hashtable;

public class IsSendEmailTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			AnalysePlanParameterBo appb=new AnalysePlanParameterBo(this.getFrameconn());
  			Hashtable ht_table=appb.analyseParameterXml();
  			String creatCard_mail=(String)ht_table.get("creatCard_mail");
  			String creatCard_mail_template=(String)ht_table.get("creatCard_mail_template");
  			String evaluateCard_mail=(String)ht_table.get("evaluateCard_mail");
  			String evaluateCard_mail_template=(String)ht_table.get("evaluateCard_mail_template");
  			if("true".equalsIgnoreCase(creatCard_mail))
  			{
  				this.getFormHM().put("send", "send");
  			}
  			else
  			{
  				this.getFormHM().put("send", "nosend");
  			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
