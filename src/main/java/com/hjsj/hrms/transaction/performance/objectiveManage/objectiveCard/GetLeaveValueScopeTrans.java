package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class GetLeaveValueScopeTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String item_id=(String)this.getFormHM().get("item_id");
			String status=(String)this.getFormHM().get("status");
			String p0400=(String)this.getFormHM().get("p0400");
			String plan_id=(String)this.getFormHM().get("plan_id");
			String object_id=(String)this.getFormHM().get("object_id");
			String plan_objectType=(String)this.getFormHM().get("plan_objectType");
			ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),this.userView,plan_id);
			if(item_id!=null&&item_id.trim().length()>0)
				this.getFormHM().put("info", bo.getLeaveScope(item_id, status, p0400, object_id, plan_objectType, plan_id));
			else
				this.getFormHM().put("info","");
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
