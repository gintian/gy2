package com.hjsj.hrms.transaction.train.plan;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class InsertMovementToPlanTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		String selectIDs=(String)this.getFormHM().get("selectIDs");
		String planID=(String)this.getFormHM().get("trainPlanID");
		 if (planID != null && planID.length() > 0)
		     planID = PubFunc.decrypt(SafeCode.decode(planID));
		 
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			selectIDs =selectIDs.substring(1);
            String[] ids=selectIDs.split("#");
            StringBuffer idstr=new StringBuffer("");
            for(int i=0;i<ids.length;i++)
            {
            	idstr.append(",'"+ids[i]+"'");
            }
            String ss="update r31 set r3125='"+planID+"' where r3101 in ("+idstr.substring(1)+")";
            dao.update(ss);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
