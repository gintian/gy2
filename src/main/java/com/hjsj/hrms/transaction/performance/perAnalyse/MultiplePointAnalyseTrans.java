package com.hjsj.hrms.transaction.performance.perAnalyse;

import com.hjsj.hrms.businessobject.performance.PerformanceAnalyseBo;
import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:MultiplePointAnalyseTrans.java</p> 
 *<p>Description:多指标分析</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 18, 2008</p> 
 *@author dengcan
 *@version 4.0
 */

public class MultiplePointAnalyseTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String busitype=(String)hm.get("busitype");  // 业务分类字段 =0(绩效考核); =1(能力素质)
			//给左边的机构树用	
			PerformanceAnalyseBo bo=new PerformanceAnalyseBo(this.getFrameconn(),this.userView);
			String plan_ids=bo.getPlanIDs(this.getUserView(),busitype);
			this.getFormHM().put("plan_ids",plan_ids);			
						
			String chartHeight=(String)hm.get("chartHeight");
			String chartWidth=(String)hm.get("chartWidth");
			this.getFormHM().put("chartHeight",chartHeight);
			//5.0以上版本图形设置为自动适应
			if(this.userView.getVersion()>=50)
				chartWidth="-1";
			this.getFormHM().put("chartWidth",chartWidth);
			
			
			String cooki_planids = (String)hm.get("cooki_planids");
			hm.remove("cooki_planids");
			
			
			HashMap dataMap=new HashMap();
			String planIds=cooki_planids;
			ArrayList pointToNameList=new ArrayList();
			String chartParameterStr="";
			String chart_type="4";
			if(hm.get("b_multiplePointAnalyse0")!=null&& "query0".equals((String)hm.get("b_multiplePointAnalyse0")))
			{
//				planIds=(String)this.getFormHM().get("planIds");
//				if(planIds==null)
//					planIds="";
				dataMap=new HashMap();
				ChartParameter chartParam=new ChartParameter();
				chartParam.setChartTitle(ResourceFactory.getProperty("label.performance.noData"));
				this.getFormHM().put("chartParam", chartParam);
				hm.remove("b_multiplePointAnalyse0");
			}
			else
			{

				
				
				if(hm.get("b_multiplePointAnalyse")!=null)
				{
					String a0100_e = (String)hm.get("a0100");
					String a0100 = a0100_e;
					if(!"root".equalsIgnoreCase(a0100)){//第一次时是根节点不需要解密，否则报错。
						a0100 = PubFunc.decrypt(a0100);
					}
					
					this.getFormHM().put("objSelected", PubFunc.encrypt(a0100));
//					hm.remove("a0100");
//					planIds=(String)this.getFormHM().get("planIds");
					planIds=cooki_planids==null?"":cooki_planids;
					////过滤出选中对象所参与的计划
					String id[]=planIds.split(",");
					planIds="";
					String plan_ids1=bo.getPlanIDs(this.getUserView(),busitype,a0100);
					for(int i=0;i<id.length;i++){
						if(plan_ids1.indexOf(id[i])!=-1){
							planIds+=id[i]+",";
						}
					}
					if(planIds.length()>0)
						planIds=planIds.substring(0, planIds.length()-1);
					
					if(cooki_planids==null||cooki_planids.trim().length()==0)
						planIds=bo.getPlanByObject(a0100,busitype);
					if(planIds.length()>0 && a0100.length()>0){
						if("0".equals(busitype)){
							dataMap=bo.getMultiplePointAnalyse(planIds,a0100,pointToNameList);		
						}else if("1".equals(busitype)){
							dataMap=bo.getMultiplePointAnalyseModify(planIds,a0100,pointToNameList);
						}
					}
						
									
						
					hm.remove("b_multiplePointAnalyse");
				}
				
				ChartParameter chartParam=new ChartParameter();
				chartParameterStr=(String)this.getFormHM().get("chartParameterStr");
				if(chartParameterStr!=null&&chartParameterStr.length()>0)
				{
					chartParam.analyseChartParameter(chartParameterStr);
				}
				else
					chartParam.setChartTitle(ResourceFactory.getProperty("org.performance.qsfx"));
				String isShowScore=(String)this.getFormHM().get("isShowScore");
				if("0".equals(isShowScore))
					chartParam.setItemLabelsVisible(false);
				
				this.getFormHM().put("chartParam", chartParam);
			
				if(("4".equals((String)this.getFormHM().get("chart_type"))|| "30".equals((String)this.getFormHM().get("chart_type")))&& "1".equals((String)this.getFormHM().get("isShow3D")))
					chart_type="30";
				else
					chart_type="4";
				
				
			}
			this.getFormHM().put("chart_type", chart_type);
			if(!"30".equals(chart_type))
				this.getFormHM().put("isShow3D", "0");
			this.getFormHM().put("pointToNameList",pointToNameList);
			
			this.getUserView().getHm().put("pointToNameList", pointToNameList);
			this.getFormHM().put("chartParameterStr",chartParameterStr);
			this.getFormHM().put("planIds",planIds);
			this.getFormHM().put("dataMap",dataMap);		

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
