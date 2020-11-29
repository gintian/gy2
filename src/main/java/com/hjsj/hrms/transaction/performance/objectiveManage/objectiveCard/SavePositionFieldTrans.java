package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ImportDeptFieldBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 30200710253
 * <p>Title:SavePositionFieldTrans.java</p>
 * <p>Description>:SavePositionFieldTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Nov 11, 2009 7:52:48 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class SavePositionFieldTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String plan_id=(String)this.getFormHM().get("plan_id");
			String object_id=(String)this.getFormHM().get("object_id");
			String i9999=(String)this.getFormHM().get("i9999");
			String itemid=(String)this.getFormHM().get("itemid");
			String model=(String)this.getFormHM().get("model");
			String body_id=(String)this.getFormHM().get("body_id");
			String p0400=(String)this.getFormHM().get("p0400");
			String importTypeString=(String)this.getFormHM().get("importType");
			ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),plan_id,object_id,this.getUserView(),model,body_id);
			String positionID="";
			String sidsString=(String)this.getFormHM().get("sids");
			if("position".equalsIgnoreCase(importTypeString)){
					positionID=bo.getObjectPositionID(object_id, "USR");
			}else {
				ImportDeptFieldBo abo = new ImportDeptFieldBo(getFrameconn(), getUserView(), plan_id);
				positionID=abo.getDeptString(object_id, "USR");
			}
			bo.importPositionField(this.userView.getDbname(),itemid, positionID, i9999, plan_id, object_id,this.userView.getA0100(),p0400,importTypeString,sidsString);
			this.getFormHM().put("importType", importTypeString);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
