package com.hjsj.hrms.transaction.performance.perAnalyse;

import com.hjsj.hrms.businessobject.performance.PerformanceAnalyseBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:PerVoteStatTrans.java</p> 
 *<p>Description:选票统计</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 21, 2008</p> 
 *@author dengcan
 *@version 4.0
 */

public class PerVoteStatTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			PerformanceAnalyseBo bo=new PerformanceAnalyseBo(this.getFrameconn(),this.userView);
			String plan_id=(String)hm.get("plan_id");
			CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
			boolean _flag = _bo.isHavePriv(this.userView, plan_id);
			if(!_flag){
				return;
			}
			ArrayList planList=bo.getPlanList_commonData("7",1,0,this.getUserView(),plan_id,"0");
			String planID="";
			String codeitemid="-1";
			String perVoteStatInfo="";
			if(hm.get("b_voteStat0")!=null&& "query0".equals((String)hm.get("b_voteStat0")))
			{
				String fromModule =(String)hm.get("fromModule"); 
				if("evaluation".equalsIgnoreCase(fromModule)&&planList.size()==0)
					perVoteStatInfo="计划中不包含总体评价！";				
					
				if(planList.size()>0)
					planID=((CommonData)planList.get(0)).getDataValue();
				codeitemid="-1";
				hm.remove("b_voteStat0");
			}
			else if(hm.get("b_voteStat0")!=null&& "query".equals((String)hm.get("b_voteStat0")))
			{
				planID=(String)this.getFormHM().get("planIds");
				codeitemid="-1";
				hm.remove("b_voteStat0");
			}
			else
			{
				planID=(String)this.getFormHM().get("planIds");
				String _id = (String) hm.get("codeitemid"); // setid + itemId by lium
				String _itemid = _id == null || "".equals(_id) ? "" : _id;
				codeitemid=PubFunc.decrypt(_itemid);
			}
			
			ArrayList perDegreeList=bo.getPerDegreeList(planID);
			ArrayList wholeEvalDataList=bo.getWholeEvalDataList(planID,codeitemid,perDegreeList);
			
			this.getFormHM().put("perDegreeList",perDegreeList);
			this.getFormHM().put("wholeEvalDataList",wholeEvalDataList);
			
			this.getFormHM().put("planIds",planID);
			this.getFormHM().put("plan_ids",planID);
			this.getFormHM().put("perPlanList",planList);
			this.getFormHM().put("perVoteStatInfo",perVoteStatInfo);
			
			this.getFormHM().put("busitype","0");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
