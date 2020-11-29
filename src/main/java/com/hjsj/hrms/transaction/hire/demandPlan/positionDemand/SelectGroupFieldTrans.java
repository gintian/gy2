package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:SelectGroupFieldTrans.java</p>
 * <p>Description>:SelectGroupFieldTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jul 27, 2010  1:57:34 PM </p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei3000000248
 */
public class SelectGroupFieldTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			PositionDemand pd = new PositionDemand(this.getFrameconn());
			ArrayList list = pd.getGroupFieldList();
			this.getFormHM().put("groupFieldList",list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
