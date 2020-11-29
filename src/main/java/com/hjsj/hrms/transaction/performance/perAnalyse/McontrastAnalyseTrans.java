package com.hjsj.hrms.transaction.performance.perAnalyse;

import com.hjsj.hrms.businessobject.performance.PerformanceAnalyseBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:McontrastAnalyseTrans.java</p> 
 *<p>Description:多人对比分析</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 20, 2008</p> 
 *@author dengcan
 *@version 4.0
 */

public class McontrastAnalyseTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String busitype=(String)hm.get("busitype");  // 业务分类字段 =0(绩效考核); =1(能力素质)
			String chartHeight=(String)hm.get("chartHeight");
			String chartWidth=(String)hm.get("chartWidth");
			String plan_id=(String)hm.get("plan_id");
			CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
			boolean _flag = _bo.isHavePriv(this.userView, plan_id);
			if(!_flag){
				return;
			}
			HashMap dataMap=new HashMap();
			ArrayList dataList=new ArrayList();
			PerformanceAnalyseBo bo=new PerformanceAnalyseBo(this.getFrameconn(),this.getUserView());
			ArrayList planList=bo.getPlanList_commonData("7",0,0,this.getUserView(),plan_id,busitype);
			String planIds="";
			ArrayList pointToNameList=new ArrayList();
			
			String chartParameterStr="";
			String scoreGradeStr="";
			
			String chart_type = (String)this.getFormHM().get("chart_type");
			if(chart_type==null || chart_type.trim().length()<=0)			
			{
				if(busitype!=null && busitype.trim().length()>0 && "1".equals(busitype))
					chart_type="41";
				else
					chart_type="4";
			}
			
			String isShowPercentVal = (String)this.getFormHM().get("isShowPercentVal");
			isShowPercentVal = (isShowPercentVal==null || isShowPercentVal!=null && isShowPercentVal.length()==0)?"0":isShowPercentVal;
			
			if(hm.get("b_mcontrastAnalyse0")!=null&& "query0".equals((String)hm.get("b_mcontrastAnalyse0")))
			{
			    if(planList.size()>0)
				planIds=((CommonData)planList.get(0)).getDataValue();
				dataMap=new HashMap();
				ChartParameter chartParam=new ChartParameter();
				chartParam.setChartTitle(ResourceFactory.getProperty("label.performance.noData"));
				this.getFormHM().put("chartParam", chartParam);
				hm.remove("b_mcontrastAnalyse0");
				this.getFormHM().put("mcontrastids", "");
			}
			else
			{
				planIds=(String)this.getFormHM().get("planIds");
				String objects=(String)hm.get("objects");
				String opt=(String)hm.get("opt");   //1：单人对比分析   2：多人对比分析
				
				if(objects!=null)
					this.getFormHM().put("mcontrastids", objects);
				ChartParameter chartParam=new ChartParameter();
				chartParameterStr=(String)this.getFormHM().get("chartParameterStr");
			    String selectids="null";
				if(chartParameterStr!=null&&chartParameterStr.length()>0)
				{
					chartParam.analyseChartParameter(chartParameterStr);
					selectids=chartParam.getMarkers();
				}
				else
				{
					chartParam.setChartTitle("");
				}
				chartParam.setLineNodeIsMarked(1);
				this.getFormHM().put("chartParam", chartParam);
				String isShowScore=(String)this.getFormHM().get("isShowScore");
				if("0".equals(isShowScore))
					chartParam.setItemLabelsVisible(false);
				chartParam.setLineNodeIsMarked(1);
				this.getFormHM().put("chartParam", chartParam);	
				
				/**判断评估计划是按否按岗位素质模型测评 ByModel**/
				String byModel=bo.getByModelByPlanId(planIds);
				this.getFormHM().put("byModel",byModel);
				if(objects!=null && objects.trim().length()>0)
				{
					if(byModel!=null&& "1".equalsIgnoreCase(byModel)){//按岗位素质模型进行多人对比分析
						dataMap=bo.getMultipleContrastAnalyseByModel(planIds,objects,pointToNameList,selectids,isShowPercentVal,chart_type);
						dataList=bo.getMultipleContrastAnalyseListByModel(planIds,objects,pointToNameList,selectids,isShowPercentVal);
						//scoreGradeStr=bo.getMultipleContrastStr(planIds,objects,selectids);
					}else{//按测量表进行多人对比分析
						dataMap=bo.getMultipleContrastAnalyse(planIds,objects,pointToNameList,selectids,isShowPercentVal,chart_type);
						dataList=bo.getMultipleContrastAnalyseList(planIds,objects,pointToNameList,selectids,isShowPercentVal);
						scoreGradeStr=bo.getMultipleContrastStr(planIds,objects,selectids);
					}
				}				
				
				if(("4".equals((String)this.getFormHM().get("chart_type"))|| "30".equals((String)this.getFormHM().get("chart_type")))&& "1".equals((String)this.getFormHM().get("isShow3D")))
					chart_type="30";
				else if(("29".equals((String)this.getFormHM().get("chart_type"))|| "31".equals((String)this.getFormHM().get("chart_type")))&& "1".equals((String)this.getFormHM().get("isShow3D")))
					chart_type="31";
				else if(("4".equals((String)this.getFormHM().get("chart_type"))|| "30".equals((String)this.getFormHM().get("chart_type")))&& "0".equals((String)this.getFormHM().get("isShow3D")))
					chart_type="4";
				else if(("29".equals((String)this.getFormHM().get("chart_type"))|| "31".equals((String)this.getFormHM().get("chart_type")))&& "0".equals((String)this.getFormHM().get("isShow3D")))
					chart_type="29";
			}
			
			this.getFormHM().put("chart_type",chart_type);
			if(!"30".equals(chart_type)&&!"31".equals(chart_type))
				this.getFormHM().put("isShow3D", "0");
			this.getFormHM().put("chartParameterStr",chartParameterStr);
			this.getFormHM().put("scoreGradeStr",scoreGradeStr);
			this.getFormHM().put("dataList",dataList);
			this.getFormHM().put("pointToNameList",pointToNameList);
			this.getUserView().getHm().put("pointToNameList", pointToNameList);
			this.getFormHM().put("plan_ids",planIds);
			this.getFormHM().put("dataMap", dataMap);
			this.getFormHM().put("perPlanList",planList);
			this.getFormHM().put("planIds", planIds);
			this.getFormHM().put("chartHeight",chartHeight);
			//5.0以上版本图形设置为自动适应
			if(this.userView.getVersion()>=50)
				chartWidth="-1";
			this.getFormHM().put("chartWidth",chartWidth);
			this.getFormHM().put("isShowPercentVal", isShowPercentVal);
			
			String returnflag=(String)hm.get("returnflag");
			this.getFormHM().put("returnflag",returnflag);
			
			this.getFormHM().put("busitype",busitype);
	
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
