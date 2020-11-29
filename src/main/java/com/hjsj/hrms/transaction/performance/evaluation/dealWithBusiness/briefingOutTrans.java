package com.hjsj.hrms.transaction.performance.evaluation.dealWithBusiness;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:简报输出</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jun 14, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class briefingOutTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			
			String planid=(String)this.getFormHM().get("planid");
			CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
			boolean _flag = _bo.isHavePriv(this.userView, planid);
			if(!_flag){
				return;
			}
			PerEvaluationBo bo=new PerEvaluationBo(this.getFrameconn());
			String fileName = bo.outBriefing(planid,this.userView);
			fileName = PubFunc.encrypt(fileName);
			fileName = SafeCode.encode(fileName);
			this.getFormHM().put("briefingName",fileName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
