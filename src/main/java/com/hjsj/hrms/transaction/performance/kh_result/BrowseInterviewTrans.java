package com.hjsj.hrms.transaction.performance.kh_result;

import com.hjsj.hrms.businessobject.performance.kh_result.ResultBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class BrowseInterviewTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String id=(String)map.get("ID");
			ResultBo bo = new ResultBo(this.getFrameconn());
			String interview=bo.getInterviewContent(id);
			this.getFormHM().put("interview", interview);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
