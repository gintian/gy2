package com.hjsj.hrms.transaction.performance.perAnalyse;

import com.hjsj.hrms.businessobject.performance.PerformanceAnalyseBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.interfaces.performance.AnalyseSelPointTree;
import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

/**
 *<p>Title:SinglePointAnalyseTrans.java</p> 
 *<p>Description:单指标分析</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 28, 2008</p> 
 *@author dengcan
 *@version 4.0
 */

public class SinglePointAnalyseTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		try
        {
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String busitype=(String)hm.get("busitype");  // 业务分类字段 =0(绩效考核); =1(能力素质)
			
			PerformanceAnalyseBo bo=new PerformanceAnalyseBo(this.getFrameconn(),this.userView);
			String chartHeight=(String)hm.get("chartHeight");
			String chartWidth=(String)hm.get("chartWidth");
			
			this.getFormHM().put("chartHeight",chartHeight);	
			
			//5.0以上版本图形设置为自动适应
			if(this.userView.getVersion()>=50)
				chartWidth="-1";
			this.getFormHM().put("chartWidth",chartWidth);

			String returnflag=(String)hm.get("returnflag");
			this.getFormHM().put("returnflag",returnflag);			
			String objSelected=PubFunc.decrypt((String) this.getFormHM().get("objSelected"));
			if(hm.get("b_singlePointAnalyse0")!=null&& "query0".equals((String)hm.get("b_singlePointAnalyse0")))// 首次进入这个模块
			{
				ChartParameter chartParam=new ChartParameter();
				chartParam.setChartTitle(ResourceFactory.getProperty("label.performance.noData"));
				this.getFormHM().put("chartParam", chartParam);
				hm.remove("b_singlePointAnalyse0");
				
//				给左边的机构树用			
				String plan_ids=bo.getPlanIDs(this.getUserView(),busitype);
				this.getFormHM().put("plan_ids",plan_ids);	
				return;
				
			}		
			
			String pointID = (String)hm.get("pointid");			
			CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
			boolean _flag = _bo.isHaveItemPriv(pointID);
			if(!_flag){
				return;
			}
			String cooki_planids = (String)hm.get("cooki_planids");
			hm.remove("pointid");
			hm.remove("cooki_planids");
			String pointName="";
			String chartParameterStr="";
			
			AnalyseSelPointTree pointSelTreeBo = new AnalyseSelPointTree(cooki_planids,this.userView,busitype,objSelected);
			if(pointID==null || pointID!=null && pointID.length()==0)//首次进入该模块 找到第一个指标
			{
				HashMap map = pointSelTreeBo.getFirstSelectedPoint();		
				if(map.size()>0)
				{
					pointID=(String)map.get("point_id");
					if("i_".equalsIgnoreCase(pointID.substring(0,2)))
						pointName="["+pointID.substring(2)+"]"+(String)map.get("pointname");					
					else
						pointName="["+pointID+"]"+(String)map.get("pointname");
				}else
				{
					pointID="";
					pointName="";
				}
				
				
			}else
			{
				if("i_".equalsIgnoreCase(pointID.substring(0,2)))
					pointName ="["+pointID.substring(2)+"]"+ getPointNameById(pointID);				
				else
					pointName ="["+pointID+"]"+ getPointNameById(pointID);
			}
				
			this.getFormHM().put("pointID", pointID);
			this.getFormHM().put("pointName", pointName);
			
			
			//String a0100=PubFunc.decrypt((String)hm.get("objId"));//左边机构树 选中的考核对象
			String objId_e = (String)hm.get("objId");//左边机构树 选中的考核对象
			String a0100 = objId_e;
			if(!"root".equalsIgnoreCase(a0100)){//第一次时是根节点不需要解密，否则报错。
				a0100 = PubFunc.decrypt(a0100);
			}
			a0100=a0100==null?"":a0100;
			hm.remove("objId");
			this.getFormHM().put("objSelected", PubFunc.encrypt(a0100));		
			
			String chart_type="4";	
			String planIds=cooki_planids==null?"":cooki_planids;
			////过滤出选中对象所参与的计划
			String id[]=planIds.split(",");
			planIds="";
			String plan_ids=bo.getPlanIDs(this.getUserView(),busitype,a0100);
			for(int i=0;i<id.length;i++){
				if(plan_ids.indexOf(id[i])!=-1){
					planIds+=id[i]+",";
				}
			}
			if(planIds.length()>0)
				planIds=planIds.substring(0, planIds.length()-1);
			
			HashMap dataMap=new HashMap();
			if(hm.get("b_singlePointAnalyse")!=null&&("query0".equals((String)hm.get("b_singlePointAnalyse")) || "query".equals((String)hm.get("b_singlePointAnalyse"))))
			{				
				if(cooki_planids==null||cooki_planids.trim().length()==0  && pointID.length()>0)
					planIds=bo.getPlanByPoint(pointID,a0100);//由指标和考核对象来确定计划
				
				if(planIds.length()>0 && pointID.length()>0)
				{
					dataMap=bo.getSinglePointAnalyse(planIds,pointID,a0100,pointName);				
					
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
				}else
				{
					ChartParameter chartParam=new ChartParameter();
					chartParam.setChartTitle(ResourceFactory.getProperty("label.performance.noData"));
					this.getFormHM().put("chartParam", chartParam);
				}		
				
				hm.remove("b_singlePointAnalyse");
			}

			this.getFormHM().put("chart_type", chart_type);
			if(!"30".equals(chart_type))
				this.getFormHM().put("isShow3D", "0");		
			
			this.getFormHM().put("chartParameterStr",chartParameterStr);
			this.getFormHM().put("planIds",planIds);
			this.getFormHM().put("planList",bo.getPlanList(planIds));
			this.getFormHM().put("dataMap",dataMap);		
//			this.getFormHM().put("pointList", pointList);
		
			this.getFormHM().put("busitype",busitype);
        }
		catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }

	}
	
	public String getPointNameById(String pointID)
	{
		String pointName="";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			String sql="";
			if("i_".equalsIgnoreCase(pointID.substring(0,2)))
				sql="select itemdesc from per_template_item where item_id="+pointID.substring(2);
			else
				sql="select pointname from per_point where point_id='"+pointID+"'";
			this.frowset=dao.search(sql);
			if(this.frowset.next())
				pointName=this.frowset.getString(1);
							
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return pointName;
	}
	
}
