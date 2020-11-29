package com.hjsj.hrms.transaction.performance.implement.dataGather;

import com.hjsj.hrms.businessobject.performance.singleGradeBo_new;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:票数统计</p>
 * <p>Description:第一次读取计算出的票数</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 10, 2008:10:36:41 AM</p>
 * @author JinChunhai
 * @version 1.0
 */

public class StatDataTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		// TODO Auto-generated method stub
		String planId=(String)this.getFormHM().get("planId");

		singleGradeBo_new bo=new singleGradeBo_new(this.getFrameconn(),planId,this.getUserView());
		String vote = bo.getVote2(planId);
		this.getFormHM().put("vote", vote);

		ArrayList fullvoteList = bo.getFullvote(vote,planId);
		this.getFormHM().put("fullvoteList", fullvoteList);	
		
	}

}
