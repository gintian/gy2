package com.hjsj.hrms.transaction.performance.kh_result;

import com.hjsj.hrms.businessobject.performance.PerformanceAnalyseBo;
import com.hjsj.hrms.businessobject.performance.kh_result.ResultBo;
import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:GetFiguresTrans.java</p>
 * <p>Description>:GetFiguresTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-6-10 下午05:05:43</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */

public class GetFiguresTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)map.get("opt");
			String plan_id="";
			String object_id="";
			String distinctionFlag =""; 
			String drawtype="0";
			String drawId="";
			String model="";
			String scoreGradeStr="";
			String graphType = "1"; // 图形的方式  分为：1 个人得分概览  2 主体分类对比分析 3 同级人群得分分布图
			/**显示3D=0不显示3D图形*/
		    String isShow3D="0";
		    /**显示分值=1显示分值*/
		    String isShowScore="1";
		    /**统计图类型=4或=30*/
		    String chart_type="4";      //柱状 29  折线 4
		    String label_enabled="true";
		    String chartsets = (String)map.get("chartParameters");
		//  if(opt.equals("1"))
		//    	chartsets = "";
			ChartParameter chartParameter = new ChartParameter(); //图形参数对象
			String selectids = "null";//点击右键选中的要显示的项
			if(chartsets != null && chartsets.trim().length()>0 && !"null".equalsIgnoreCase(chartsets))
			{											
				chartParameter.analyseChartParameter(chartsets);
				selectids = chartParameter.getMarkers();				
			}else
			{
				chartParameter.setChartTitle("");
			}	
			if("1".equals(opt))
			{
				model = (String)map.get("model");
				plan_id = PubFunc.decryption((String)map.get("planid"));
				object_id = PubFunc.decryption((String)map.get("object_id"));
				distinctionFlag=(String)map.get("distinctionFlag");
			}
			else
			{
				model = (String)this.getFormHM().get("model");
				plan_id = PubFunc.decryption((String)this.getFormHM().get("planid"));
				object_id = PubFunc.decryption((String)this.getFormHM().get("object_id"));
				distinctionFlag=(String)this.getFormHM().get("distinctionFlag");
				if(map.get("drawtype")!=null)
		    		drawtype=(String)map.get("drawtype");
				chart_type=(String)this.getFormHM().get("chart_type");
				isShow3D=(String)this.getFormHM().get("isShow3D");
				isShowScore=(String)this.getFormHM().get("isShowScore"); //1 按分值 0 按得分率
				graphType = (String)this.getFormHM().get("graphType"); // 图形的方式  分为：1 个人得分概览  2 主体分类对比分析 3 同级人群得分分布图
			}
			chartParameter.setLineNodeIsMarked(1);
			if("0".equals(isShowScore))
			{
				chartParameter.setItemLabelsVisible(false);
			}
			else
			{
				chartParameter.setItemLabelsVisible(true);
			}
			chartParameter.setLineNodeIsMarked(1);
			if(("4".equals(chart_type)|| "30".equals(chart_type))&& "1".equals(isShow3D))
				chart_type="30";
			else if(("29".equals(chart_type)|| "31".equals(chart_type))&& "1".equals(isShow3D))
				chart_type="31";
			else if(("4".equals(chart_type)|| "30".equals(chart_type))&& "0".equals(isShow3D))
				chart_type="4";
			else if(("29".equals(chart_type)|| "31".equals(chart_type))&& "0".equals(isShow3D))
				chart_type="29";
			
			PerformanceAnalyseBo pbo=new PerformanceAnalyseBo(this.getFrameconn(),this.userView,plan_id,object_id);
			ResultBo bo = new ResultBo(this.getFrameconn(),this.userView,plan_id,object_id);
			String sameField = bo.getSameField(plan_id,object_id);//得到同级指标
			int sameFieldflag = 0;//判断是否有同级指标
			if(!"".equals(sameField))
				sameFieldflag = 1;
			
			
			//String chartTypeTemp = (String)map.get("chartTypeTemp");
			//map.remove("chartTypeTemp");
			//if(chartTypeTemp==null || chartTypeTemp.equals(""))
				//chartTypeTemp="1";
			
			String department = "0";//选中的部门号  默认是"全部"
			department = (String)this.getFormHM().get("department");
			//得到该计划的部门列表
			ArrayList departmentList = bo.getDepartmentList(plan_id,sameField);//得到部门层级
			ArrayList graphTypeList = bo.getGraphTypeList(sameFieldflag,model);    //图形后面的下拉列表
			int method=bo.getPlanMethod(Integer.parseInt(plan_id));
			ArrayList pointList = bo.getPointList(plan_id);
			HashMap figuresmap=new HashMap();//折线图 雷达图
			if("1".equals(graphType)){//个人得分概览
				figuresmap = bo.getFiguresMap(plan_id, object_id, pointList,drawtype,chart_type,selectids,sameField,model);
				scoreGradeStr = bo.getSingleContrastStr(plan_id,object_id,selectids,sameField,model);
			}else if("2".equals(graphType)){//主体分类对比分析
				figuresmap = pbo.getSingleMainbodyContrastData(plan_id,object_id,selectids,"0",chart_type);
				scoreGradeStr = pbo.getMainbodySetGradeStr(selectids,plan_id);
			}else if("3".equals(graphType)){//同级人群得分分布图
				figuresmap = bo.getTongjiMap(plan_id, object_id , sameField,department,chart_type);
			}
			ArrayList dataList = new ArrayList();//柱状图
			if("1".equals(graphType)){//个人得分概览
				dataList = bo.getFiguresList(plan_id, object_id, pointList,drawtype,selectids,sameField);
			}else if("2".equals(graphType)){//主体分类对比分析
				dataList = pbo.getSingleMainbodyContrastList(figuresmap);
			}else if("3".equals(graphType)){//同级人群得分分布图
				dataList = bo.getTongjiList(plan_id, object_id  , sameField,department);
			}						
			
			////////////////////
			double percent = bo.getPercent(method);//为了先执行盲点功能，如果有盲点才有“评价盲点”这个选项
			///////////////////
			ArrayList drawList = bo.getDrawList(distinctionFlag,method,plan_id,this.userView,object_id,percent,model);
			if(drawList.size()>0)
			{
				drawId=((CommonData)drawList.get(0)).getDataValue();
			}
			String firstLink=bo.getFirstLink(drawId, object_id,plan_id);
			if(pointList.size()<=0)
				pointList=bo.getTemplateItemList(plan_id);
			if(pointList.size()>5&&("29".equals(chart_type)|| "31".equals(chart_type)))
				label_enabled="false";
			this.getFormHM().put("label_enabled",label_enabled);	
			this.getFormHM().put("chartParameter",chartParameter);
			this.getFormHM().put("chartsets",chartsets);
			this.getFormHM().put("scoreGradeStr",scoreGradeStr);
			this.getFormHM().put("title",chartParameter.getChartTitle());
			this.getFormHM().put("pointList",this.userView.getVersion()>=50?new ArrayList():pointList);
			this.getFormHM().put("drawList",drawList);
			this.getFormHM().put("drawId",drawId);
			this.getFormHM().put("drawtype",drawtype);
			this.getFormHM().put("planid",PubFunc.encrypt(plan_id));
			this.getFormHM().put("dataList", dataList);
			this.getFormHM().put("object_id",PubFunc.encrypt(object_id));
			this.getFormHM().put("figuresmap",figuresmap);
			this.getFormHM().put("chart_type", chart_type);
			this.getFormHM().put("isShow3D", isShow3D);
			this.getFormHM().put("isShowScore", isShowScore);
			this.getFormHM().put("firstLink",firstLink);
			this.getFormHM().put("graphTypeList", graphTypeList);
			this.getFormHM().put("graphType", graphType);
			this.getFormHM().put("department", department);
			this.getFormHM().put("departmentList", departmentList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
