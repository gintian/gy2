package com.hjsj.hrms.transaction.performance.perAnalyse;

import com.hjsj.hrms.businessobject.performance.PerformanceAnalyseBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:StatAnalyseTrans.java</p> 
 *<p>Description:统计分析</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 18, 2008</p> 
 *@author dengcan
 *@version 4.0
 */

public class StatAnalyseTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			PerformanceAnalyseBo bo=new PerformanceAnalyseBo(this.getFrameconn(),this.getUserView());
			String busitype=(String)hm.get("busitype");  // 业务分类字段 =0(绩效考核); =1(能力素质)
			ArrayList planList=bo.getPlanList_commonDataByModel("7",0,0,this.getUserView(),"",busitype);
			String planIds="";
			if(hm.get("b_statAnalyse")!=null&& "query0".equals((String)hm.get("b_statAnalyse")))
			{
			    if(planList.size()>0)
				planIds=((CommonData)planList.get(0)).getDataValue();
			}
			else
				planIds=(String)this.getFormHM().get("planIds");
			
			int num=0;
			String statHtml="";
			if(planIds.length()>0)
			{
			    num = bo.getPlanObjectNum(planIds);			
			    statHtml=bo.getStatHtml(planIds); 
			}			   
			
			String chartHeight=(String)hm.get("chartHeight");
			String chartWidth=(String)hm.get("chartWidth");
			this.getFormHM().put("chartHeight",chartHeight);
			//5.0以上版本图形设置为自动适应
			if(this.userView.getVersion()>=50)
				chartWidth="-1";
			this.getFormHM().put("chartWidth",chartWidth);
			this.getFormHM().put("statHtml",statHtml);
			this.getFormHM().put("perPlanList",planList);
			this.getFormHM().put("planIds", planIds);
			this.getFormHM().put("objectNum",String.valueOf(num));

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
