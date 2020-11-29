package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:按新标度重新计算分值</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 7, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class reComputeScoreTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String plan_id=(String)this.getFormHM().get("plan_id");
			PerEvaluationBo pe=new PerEvaluationBo(this.getFrameconn(),plan_id,"");
			pe.repeatComputeScore();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
