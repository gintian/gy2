package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hjsj.hrms.businessobject.performance.commend.CommendSetBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class ExecutingVoteAnalyseTrans extends IBusiness{
	public void execute() throws GeneralException{
		try{
			String id="";
			if(this.getFormHM().get("p0201") !=null)
				id=(String)this.getFormHM().get("p0201");
			CommendSetBo bo = new CommendSetBo(this.getFrameconn());
			if(id.trim().length()>0)
				bo.AnalyseVote(id);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
