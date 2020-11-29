package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ParseXmlBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:保存任务回顾</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 21, 2009</p> 
 *@author dengcan
 *@version 4.2
 */
public class SaveTaskReviewTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String p0400=(String)hm.get("p0400");
			String myView=(String)this.getFormHM().get("myView");
			String object_id=(String)this.getFormHM().get("object_id");
			String planid=(String)this.getFormHM().get("planid");
			
			myView=myView.replaceAll("<", "《");
			myView=myView.replaceAll(">", "》");
			
			ParseXmlBo bo=new ParseXmlBo(this.getFrameconn());
			
			bo.insertContext(this.userView.getA0100(),this.userView.getDbname(),myView,p0400,"summarizes",planid,object_id);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
