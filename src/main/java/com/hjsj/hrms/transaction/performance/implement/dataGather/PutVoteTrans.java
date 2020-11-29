package com.hjsj.hrms.transaction.performance.implement.dataGather;

import com.hjsj.hrms.businessobject.performance.singleGradeBo_new;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/** 
 * <p>Title:票数统计</p>
 * <p>Description:第二次票数统计</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 10, 2008:10:37:52 AM</p>
 * @author JinChunhai
 * @version 1.0
 */

public class PutVoteTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		// TODO Auto-generated method stub
		String votedate=null;
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		votedate =(String) hm.get("total");
	
		String plan_id = (String)this.getFormHM().get("planId");
		LoadXml x = new LoadXml(frameconn, plan_id);
		//x.saveAttributeVote("VotesNum",votedate);
		x.saveAttribute("PerPlan_Parameter", "VotesNum", votedate);
		singleGradeBo_new bo=new singleGradeBo_new(this.getFrameconn(),plan_id,this.getUserView());
		String vote = bo.getVote(plan_id);
		this.getFormHM().put("vote", vote);
		
		ArrayList fullvoteList = bo.getFullvote(vote,plan_id);
		this.getFormHM().put("fullvoteList", fullvoteList);	
	
	}

}
