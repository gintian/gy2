package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveSpModifyTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String z0301=(String)this.getFormHM().get("z0301");
			ArrayList positionDemandDescList=(ArrayList)this.getFormHM().get("positionDemandDescList");
			PositionDemand bo=new PositionDemand(this.getFrameconn());
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			bo.addCurrappusername(z0301);
			bo.checkCanOperate(z0301, userView);
			String az0301=bo.modifyPositionDemand(positionDemandDescList, z0301);
			this.getFormHM().put("z0301",z0301);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
