package com.hjsj.hrms.transaction.performance.perAnalyse;

import com.hjsj.hrms.businessobject.performance.PerformanceAnalyseBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:InitPerResultTableTrans.java</p> 
 *<p>Description:初始化综合评测表</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 21, 2008</p> 
 *@author dengcan
 *@version 4.0
 */

public class InitPerResultTableTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			PerformanceAnalyseBo bo=new PerformanceAnalyseBo(this.getFrameconn(),this.userView);
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String busitype=(String)hm.get("busitype");  // 业务分类字段 =0(绩效考核); =1(能力素质)
			String plan_id=(String)hm.get("plan_id");
			ArrayList planList=bo.getPlanList_commonData("7",0,1,this.getUserView(),plan_id,busitype);
			
			String planIds="";
			if(hm.get("b_perResultTable0")!=null&& "query0".equals((String)hm.get("b_perResultTable0")))
			{
			    if(planList.size()>0)
				planIds=((CommonData)planList.get(0)).getDataValue();
			}
			else
			{
				planIds=(String)this.getFormHM().get("planIds");
			}
			
			this.getFormHM().put("planIds",planIds);
			this.getFormHM().put("plan_ids",planIds);
			this.getFormHM().put("perPlanList",planList);
	
			String returnflag=(String)hm.get("returnflag");
			this.getFormHM().put("returnflag",returnflag);
			
			this.getFormHM().put("busitype",busitype);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
