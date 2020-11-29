package com.hjsj.hrms.transaction.performance.perAnalyse;

import com.hjsj.hrms.businessobject.performance.PerformanceAnalyseBo;
import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:contrastAnalyseTrans.java</p> 
 *<p>Description:单人对比分析</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 20, 2008</p> 
 *@author dengcan
 *@version 4.0
 */

public class contrastAnalyseTrans extends IBusiness 
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
			HashMap dataMap=new HashMap();
			ArrayList dataList=new ArrayList();
			PerformanceAnalyseBo bo=new PerformanceAnalyseBo(this.getFrameconn(),this.userView);
			ArrayList planList=bo.getPlanList_commonData("7",0,0,this.getUserView(),plan_id,busitype);
			String planIds="";
			String byModel="";//按岗位素质模型测评 0不按 1按
			String objSelected=(String) hm.get("a0100");
			this.formHM.put("objSelected", objSelected);
			ArrayList pointToNameList=new ArrayList();
			
			String chartParameterStr="";			 
			String scoreGradeStr="";
			if("1".equals(busitype))
			chartParameterStr="`0`0`0`0`0`0`0`本人得分,岗位要求,`0,0`";//单人对比分析默认显示 本人得分和岗位要求
			String chart_type = (String)this.getFormHM().get("chart_type");//图形样式
			if(chart_type==null || chart_type.trim().length()<=0)			
			{
				if(busitype!=null && busitype.trim().length()>0 && "1".equals(busitype))
					chart_type="41";
				else
					chart_type="4";
			}
			String isShowPercentVal = (String)this.getFormHM().get("isShowPercentVal");//是否按百分比显示分值
			isShowPercentVal = (isShowPercentVal==null || isShowPercentVal!=null && isShowPercentVal.length()==0)?"0":isShowPercentVal;
			if(hm.get("b_contrastAnalyse0")!=null&& "query0".equals((String)hm.get("b_contrastAnalyse0")))
			{
			    if(planList.size()>0)				
				planIds=((CommonData)planList.get(0)).getDataValue();
				dataMap=new HashMap();
				ChartParameter chartParam=new ChartParameter();
				chartParam.setChartTitle(ResourceFactory.getProperty("label.performance.noData"));//没有考核数据
				this.getFormHM().put("chartParam", chartParam);
				hm.remove("b_contrastAnalyse0");
				if("1".equals(busitype))
					isShowPercentVal="2";///单人对比分析默认按级别显示
				else
					isShowPercentVal="1";///单人对比分析默认按百分比显示
			}
			else if(hm.get("b_contrastAnalyse0")!=null&& "query".equals((String)hm.get("b_contrastAnalyse0")))
			{
				planIds=(String)this.getFormHM().get("planIds");
				hm.remove("b_contrastAnalyse0");
				ChartParameter chartParam=new ChartParameter();
				chartParam.setChartTitle(ResourceFactory.getProperty("label.performance.noData"));
				this.getFormHM().put("chartParam", chartParam);
			}
			else
			{
				planIds=(String)this.getFormHM().get("planIds");
				String a0100=(String)hm.get("a0100"); 
				a0100= "root".equalsIgnoreCase(a0100)?"":a0100;
				a0100 = PubFunc.decrypt(a0100);
				
				ChartParameter chartParam=new ChartParameter();
				chartParameterStr=(String)this.getFormHM().get("chartParameterStr");				
			    String selectids="null";
			    /* 任务：3818 点击人员高级花名册链接显示最近一个已结束能力素质雷达图 start */
			    this.getFormHM().put("lastplan", null);
			    String lastplan = (String)hm.get("lastplan");
			    if(StringUtils.equals(lastplan, "1")) {
			    	this.getFormHM().put("lastplan", "1");
			    }
			    if(StringUtils.isEmpty(chartParameterStr)) {
			    	chartParameterStr = "`0`0`0`0`0`0`0`本人得分,岗位要求,`0,0`";
			    }
			    /* 任务：3818 点击人员高级花名册链接显示最近一个已结束能力素质雷达图 end */
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
				/**判断评估计划是按否按岗位素质模型测评 ByModel**/
				byModel=bo.getByModelByPlanId(planIds);
				//e01a1=bo.getE01A1ByA0100(objSelected);
				if(byModel!=null&& "1".equalsIgnoreCase(byModel)){//按岗位素质模型进行单人对比分析
					dataMap=bo.getSingleContrastAnalyseByModel(planIds,a0100,pointToNameList,selectids,isShowPercentVal,chart_type,busitype,"sign");
					dataList=bo.getSingleContrastAnalyseListByModel(planIds,a0100,pointToNameList,selectids,isShowPercentVal,busitype);
					scoreGradeStr=bo.getSingleContrastStr(planIds,a0100,selectids,busitype);
				}else{//按测量表进行单人对比分析
					dataMap=bo.getSingleContrastAnalyse(planIds,a0100,pointToNameList,selectids,isShowPercentVal,chart_type,busitype,"sign");
					dataList=bo.getSingleContrastAnalyseList(planIds,a0100,pointToNameList,selectids,isShowPercentVal,busitype);
					scoreGradeStr=bo.getSingleContrastStr(planIds,a0100,selectids,busitype);
				}

				this.getFormHM().put("chartParam", chartParam);
				
				
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
			this.getFormHM().put("scoreGradeStr",scoreGradeStr);//分值序列
			this.getFormHM().put("pointToNameList",pointToNameList);
			this.getUserView().getHm().put("pointToNameList", pointToNameList);
			this.getFormHM().put("plan_ids",planIds);//考核计划id 
			this.getFormHM().put("dataMap", dataMap);
			this.getFormHM().put("dataList",dataList);
			this.getFormHM().put("perPlanList",planList);//考核计划列表
			this.getFormHM().put("planIds", planIds);
			this.getFormHM().put("chartHeight",chartHeight);
			//5.0以上版本图形设置为自动适应
			if(this.userView.getVersion()>=50)
				chartWidth="-1";
			this.getFormHM().put("chartWidth",chartWidth);
			
			this.getFormHM().put("isShowPercentVal",isShowPercentVal);
			
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
