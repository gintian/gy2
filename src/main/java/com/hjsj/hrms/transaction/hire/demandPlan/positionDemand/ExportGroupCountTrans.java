package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:ExportGroupCountTrans.java</p>
 * <p>Description>:ExportGroupCountTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jul 27, 2010  1:57:25 PM </p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei3000000249
 */
public class ExportGroupCountTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			String groupField=SafeCode.decode((String)this.getFormHM().get("groupField"));
			String countField=SafeCode.decode((String)this.getFormHM().get("countField"));
			String where =SafeCode.decode((String)this.getFormHM().get("where"));
			PositionDemand pd = new PositionDemand(this.getFrameconn());
			String fileName=pd.exportGroupCountFile(groupField, countField, userView,where).replaceAll(".xls","#");
			this.getFormHM().put("filename", fileName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
