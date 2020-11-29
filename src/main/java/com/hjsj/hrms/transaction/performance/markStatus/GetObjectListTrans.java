package com.hjsj.hrms.transaction.performance.markStatus;

import com.hjsj.hrms.businessobject.performance.markStatus.MarkStatusBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class GetObjectListTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String planid=PubFunc.decrypt((String)map.get("planid"));
			String mainbodyid=PubFunc.decrypt((String)map.get("mainbodyid"));
			MarkStatusBo bo = new MarkStatusBo(this.getFrameconn());
			ArrayList list = bo.getSubmitObject(mainbodyid, planid,this.getUserView());
			this.getFormHM().put("submitList",list);
			this.getFormHM().put("submitid","-1");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
