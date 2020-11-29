package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SaveRemarkTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			PerEvaluationBo bo=new PerEvaluationBo(this.getFrameconn());
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("opt");   //  1：评语  2：总结
			String  objectid=(String)this.getFormHM().get("objectid");
			String appraise=(String)this.getFormHM().get("summarize");
			String planid=(String)this.getFormHM().get("planid");
			bo.saveAppraise(objectid, planid, appraise,opt);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
