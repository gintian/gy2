package com.hjsj.hrms.transaction.performance.evaluation.dealWithBusiness;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:修正分值</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 24, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class SubCorrectScoreTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String correctScore=(String)this.getFormHM().get("correctScore");
			String correctCause=(String)this.getFormHM().get("correctCause");
			String object_id=(String)this.getFormHM().get("object_id");
			String planid=(String)this.getFormHM().get("planid");
			CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
			boolean _flag = _bo.isHavePriv(this.userView, planid);
			if(!_flag){
				return;
			}
			PerEvaluationBo bo=new PerEvaluationBo(this.getFrameconn());
			bo.insertCorrectRecord(correctScore,correctCause,object_id,planid);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
