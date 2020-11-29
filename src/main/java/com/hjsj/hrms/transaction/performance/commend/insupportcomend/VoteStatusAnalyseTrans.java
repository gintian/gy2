package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hjsj.hrms.businessobject.performance.commend.CommendSetBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;



public class VoteStatusAnalyseTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
		   CommendSetBo bo = new CommendSetBo(this.getFrameconn());
		   HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
		   String opt=(String)map.get("opt");
		   String p0201="";
		   if("1".equals(opt))
		   {
			   p0201=bo.getFirstRecord("06");
		   }
		   else if("0".equals(opt))
		   {
			   p0201=(String)this.getFormHM().get("p0201");
		   }
		   int i=bo.haveOneOrMoreRecord("06");
		   ArrayList voteStatusList = bo.getVoteStatusList(p0201);
		   ArrayList finishCommendList=bo.getFinishCommendList();
		   this.getFormHM().put("p0201",p0201);
		   this.getFormHM().put("finishCommendList",finishCommendList);
		   this.getFormHM().put("voteStatusList",voteStatusList);
		   this.getFormHM().put("have",String.valueOf(i));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
