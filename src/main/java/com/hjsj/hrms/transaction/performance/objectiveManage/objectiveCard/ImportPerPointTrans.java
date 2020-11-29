package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:引入绩效指标</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jun 3, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class ImportPerPointTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String itemid=(String)hm.get("itemid");
			String importPoint_value=(String)this.getFormHM().get("importPoint_value");
			String object_id=(String)this.getFormHM().get("object_id");
			String planid=(String)this.getFormHM().get("planid");
			String body_id=(String)this.getFormHM().get("body_id");
			String model=(String)this.getFormHM().get("model");
			
			String a_p0400="";
			if(hm.get("a_p0400")!=null)
			{
				a_p0400=(String)hm.get("a_p0400");
				hm.remove("a_p0400");
			}
			
			ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),planid,object_id,this.getUserView(),model,body_id);
			bo.importPerPoint(importPoint_value,"2",itemid,a_p0400);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
