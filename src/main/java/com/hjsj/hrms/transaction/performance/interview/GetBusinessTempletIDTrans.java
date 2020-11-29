package com.hjsj.hrms.transaction.performance.interview;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Hashtable;

public class GetBusinessTempletIDTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			AnalysePlanParameterBo bo=new AnalysePlanParameterBo(this.getFrameconn());
			Hashtable ht_table=bo.analyseParameterXml();
			String appeal_template="";
			if(ht_table!=null)
			{
				if(ht_table.get("appeal_template")!=null)
					appeal_template=(String)ht_table.get("appeal_template");
			}
			this.getFormHM().put("appeal_template", appeal_template);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
