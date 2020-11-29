package com.hjsj.hrms.transaction.performance.perAnalyse;

import com.hjsj.hrms.businessobject.performance.PerformanceAnalyseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:InitPlanListTrans.java</p> 
 *<p>Description:初始化考核计划</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 28, 2008</p> 
 *@author dengcan
 *@version 4.0
 */

public class InitPlanListTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("opt");  // 0:init  1:select
			hm.remove("opt");
			
			PerformanceAnalyseBo bo=new PerformanceAnalyseBo(this.getFrameconn(),this.userView);
			ArrayList planList=new ArrayList();
			String objSelected=PubFunc.decrypt((String) this.getFormHM().get("objSelected"));//考核对象
			String busitype=(String)this.getFormHM().get("busitype"); // 业务分类字段 =0(绩效考核); =1(能力素质)
			String planIds=(String)this.getFormHM().get("planIds");
			String period="-1";
			String stencilId="-1";
			if("0".equals(opt))
			{
				planList=bo.getPlanListByModel(1,"-1","-1",this.getUserView(),busitype,objSelected);
				String point_obj=(String)hm.get("point_obj");
				if(planIds==null||planIds.length()==0)
					planIds=bo.getPlanIds(point_obj);
			}
			else
			{
				period=(String)this.getFormHM().get("period");
				stencilId=(String)this.getFormHM().get("stencilId");
				planList=bo.getPlanListByModel(1,period,stencilId,this.getUserView(),busitype,objSelected);
				planIds="";
			}
			
			String temps=","+planIds+",";
			ArrayList new_planList=new ArrayList();
			for(int i=0;i<planList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)planList.get(i);
				String plan_id=(String)abean.get("plan_id");
				if(planIds.length()>0&&temps.indexOf(","+plan_id+",")!=-1)
				{
					abean.set("select","1");
				}
				else
					abean.set("select","0");
				new_planList.add(abean);
			}
			this.getFormHM().put("period", period);
			this.getFormHM().put("periodList",bo.getPeriodList());
			this.getFormHM().put("stencilId",stencilId);			
			this.getFormHM().put("perPlanList",new_planList);
			this.getFormHM().put("planIds",planIds);
			
			
			planList=bo.getPlanListByModel(1,"-1","-1",this.getUserView(),busitype,objSelected);
			planIds="";
			temps=","+planIds+",";
			new_planList=new ArrayList();
			for(int i=0;i<planList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)planList.get(i);
				String plan_id=(String)abean.get("plan_id");
				if(planIds.length()>0&&temps.indexOf(","+plan_id+",")!=-1)
				{
					abean.set("select","1");
				}
				else
					abean.set("select","0");
				new_planList.add(abean);
			}
			this.getFormHM().put("stencilList",bo.getStencilList(new_planList));
			
			this.getFormHM().put("busitype",busitype);
		}
		catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	}

}
