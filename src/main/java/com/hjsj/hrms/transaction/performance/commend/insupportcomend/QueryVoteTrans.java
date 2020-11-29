package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hjsj.hrms.businessobject.performance.commend.CommendSetBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class QueryVoteTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String a0100=(String)map.get("a0100");
			String nbase=(String)map.get("nbase");
			CommendSetBo bo = new CommendSetBo(this.getFrameconn());
			ArrayList yearList =bo.getVoteList(nbase, a0100);
			HashMap hm=bo.getPersonInfo(a0100, nbase);
			this.getFormHM().put("yearList",yearList);
			this.getFormHM().put("b0110",(String)hm.get("dw"));
			this.getFormHM().put("e0122",(String)hm.get("bm"));
			this.getFormHM().put("a0101", (String)hm.get("xm"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
