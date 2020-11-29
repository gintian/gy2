package com.hjsj.hrms.transaction.performance.perAnalyse;

import com.hjsj.hrms.businessobject.performance.PerformanceAnalyseBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:MainbodySetContrastAnalyseTrans.java</p> 
 *<p>Description:主体分类对比分析</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 25, 2008</p> 
 *@author dengcan
 *@version 4.0
 */

public class MainbodySetContrastAnalyseTrans extends IBusiness 
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
			String planID="";
			
			HashMap dataMap=new HashMap();
			ArrayList dataList = new ArrayList();//柱状图
			String chartParameterStr="";
			String scoreGradeStr="";
			
			String chart_type = (String)this.getFormHM().get("chart_type");
			if(chart_type!=null && chart_type.trim().length()>0 && !"0".equalsIgnoreCase(chart_type))
				chart_type = (String)this.getFormHM().get("chart_type");
			else
			{
				if(busitype!=null && busitype.trim().length()>0 && "1".equals(busitype))
					chart_type="41";
				else
					chart_type="4";
			}
			
			ArrayList pointToNameList=new ArrayList();
			String objectType="0";
			PerformanceAnalyseBo bo=new PerformanceAnalyseBo(this.getFrameconn(),this.userView);
			ArrayList planList=bo.getPlanList_commonData("7",0,0,this.getUserView(),plan_id,busitype);
			String isShowPercentVal = (String)this.getFormHM().get("isShowPercentVal");
			isShowPercentVal = (isShowPercentVal==null || isShowPercentVal!=null && isShowPercentVal.length()==0)?"0":isShowPercentVal;
			if(hm.get("b_perMainbodyAnalyse0")!=null&& "query0".equals((String)hm.get("b_perMainbodyAnalyse0")))
			{
			    if(planList.size()>0)
				planID=((CommonData)planList.get(0)).getDataValue();
				if(plan_id!=null&&plan_id.length()>0)
					planID=plan_id;
				hm.remove("b_perMainbodyAnalyse0");
				ChartParameter chartParam=new ChartParameter();
				chartParam.setChartTitle(ResourceFactory.getProperty("label.performance.noData"));
				this.getFormHM().put("chartParam", chartParam);
			}
			else if(hm.get("b_perMainbodyAnalyse0")!=null&& "query".equals((String)hm.get("b_perMainbodyAnalyse0")))
			{
				planID=(String)this.getFormHM().get("planIds");
				if(plan_id!=null&&plan_id.length()>0)
					planID=plan_id;
				objectType=(String)this.getFormHM().get("objectType");
				hm.remove("b_perMainbodyAnalyse0");
				ChartParameter chartParam=new ChartParameter();
				chartParam.setChartTitle(ResourceFactory.getProperty("label.performance.noData"));
				this.getFormHM().put("chartParam", chartParam);
			}
			else
			{
				
				planID=(String)this.getFormHM().get("planIds");
				if(plan_id!=null&&plan_id.length()>0)
					planID=plan_id;
				objectType=(String)this.getFormHM().get("objectType");
				ChartParameter chartParam=new ChartParameter();
				chartParameterStr=(String)this.getFormHM().get("chartParameterStr");
				String selectids="null";
				String byModel=bo.getByModelByPlanId(planID)==null?"":bo.getByModelByPlanId(planID);
				if(chartParameterStr!=null&&chartParameterStr.length()>0)
				{
					chartParam.analyseChartParameter(chartParameterStr);
					selectids=chartParam.getMarkers();
				}
				else
				{
					chartParam.setChartTitle("");
				}
				String object_id="";
				String objid = (String)hm.get("codeitemid");
				if("root".equalsIgnoreCase(objid)){
					object_id = objid;
				}else{
					object_id = PubFunc.decrypt((String)hm.get("codeitemid"));
				}				
				String e01a1=bo.getE01A1ByA0100(object_id);
				if("0".equals(objectType))//单考核对象
				{
//					pointToNameList=bo.getPlanPointList(planID);
//					dataMap=bo.getSingleMainbodyContrastData(planID,object_id,pointToNameList,selectids,isShowPercentVal);
					if("1".equals(byModel)&&!"".equals(e01a1)){
						dataMap=bo.getSingleMainbodyContrastDataByModel(planID,object_id,selectids,isShowPercentVal,chart_type);
					}else{
						dataMap=bo.getSingleMainbodyContrastDataModify(planID,object_id,selectids,isShowPercentVal,chart_type,byModel);	
					}
					dataList = bo.getSingleMainbodyContrastList(dataMap);
					this.getFormHM().put("codeitemid", object_id);
				}
				else if("1".equals(objectType))
				{
					String objects=(String)hm.get("objects");
					if("1".equals(byModel)&&!"".equals(e01a1)){
						dataMap=bo.getMultipleMainbodyContrastDataByModel(planID,objects,selectids,isShowPercentVal,chart_type);
					}else{
						dataMap=bo.getMultipleMainbodyContrastDataModify(planID,objects,selectids,isShowPercentVal,chart_type,byModel);
					}
					
					this.getFormHM().put("codeitemid", "");
				}
				scoreGradeStr=bo.getMainbodySetGradeStr(selectids,planID);
				
				
				String isShowScore=(String)this.getFormHM().get("isShowScore");
				if("0".equals(isShowScore))
					chartParam.setItemLabelsVisible(false);
				chartParam.setLineNodeIsMarked(1);
				this.getFormHM().put("chartParam", chartParam);
				
				if(("4".equals((String)this.getFormHM().get("chart_type")) || "30".equals((String)this.getFormHM().get("chart_type"))) && "1".equals((String)this.getFormHM().get("isShow3D")))
					chart_type="30";
				else if(("29".equals((String)this.getFormHM().get("chart_type")) || "31".equals((String)this.getFormHM().get("chart_type"))) && "1".equals((String)this.getFormHM().get("isShow3D")))
				{
					chart_type="31";
					if("1".equals(objectType))
						chart_type="4";
				}
				else if(("4".equals((String)this.getFormHM().get("chart_type")) || "30".equals((String)this.getFormHM().get("chart_type"))) && "0".equals((String)this.getFormHM().get("isShow3D")))
					chart_type="4";
				else if(("29".equals((String)this.getFormHM().get("chart_type")) || "31".equals((String)this.getFormHM().get("chart_type"))) && "0".equals((String)this.getFormHM().get("isShow3D")))
				{
					chart_type="29";
					if("1".equals(objectType))
						chart_type="4";
				}
			}
			
			this.getFormHM().put("chart_type",chart_type);
			if(!"30".equals(chart_type) && !"31".equals(chart_type))
				this.getFormHM().put("isShow3D", "0");
			this.getFormHM().put("scoreGradeStr",scoreGradeStr);
			this.getFormHM().put("chartParameterStr",chartParameterStr);
			this.getFormHM().put("objectType",objectType);
			this.getFormHM().put("pointToNameList",pointToNameList);
			this.getUserView().getHm().put("pointToNameList", pointToNameList);
			this.getFormHM().put("planIds",planID);
			this.getFormHM().put("plan_ids",planID);
			this.getFormHM().put("perPlanList",planList);
			this.getFormHM().put("dataMap", dataMap);
			this.getFormHM().put("dataList", dataList);
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
		}
	}

}
