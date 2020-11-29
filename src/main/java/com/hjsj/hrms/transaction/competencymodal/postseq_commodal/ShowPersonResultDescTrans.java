package com.hjsj.hrms.transaction.competencymodal.postseq_commodal;

import com.hjsj.hrms.businessobject.competencymodal.PostModalBo;
import com.hjsj.hrms.businessobject.performance.PerformanceAnalyseBo;
import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:ShowPersonResultDescTrans.java</p>
 * <p>Description:岗位分析</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:2012-02-10</p> 
 * @author JinChunhai
 * @version 5.0
 */

public class ShowPersonResultDescTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{			
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");									
		//	String plan_id=(String)hm.get("plan_id");
			HashMap dataMap=new HashMap();
			PerformanceAnalyseBo bo=new PerformanceAnalyseBo(this.getFrameconn(),this.userView);
			ArrayList planList=bo.getPlanList_commonData("7",0,0,this.getUserView(),"","1");
			String planIds="";
			ArrayList pointToNameList=new ArrayList();			
			String chartParameterStr="";
			chartParameterStr="`0`0`0`0`0`0`0`本人得分,岗位要求,`0,0`";//默认显示 本人得分和岗位要求
			String gradeResultHtml = "";
			String scoreGradeStr="";
			String byModel="";// 0按测评表  1 按岗位素质模型。
			String isShowPercentVal = (String)this.getFormHM().get("isShowPercentVal");
			isShowPercentVal = (isShowPercentVal==null || isShowPercentVal!=null && isShowPercentVal.length()==0)?"0":isShowPercentVal;
			if(hm.get("b_personStation0")!=null&& "query0".equals((String)hm.get("b_personStation0")))
			{
			    if(planList.size()>0){				
			    	planIds=((CommonData)planList.get(0)).getDataValue();
			    }
				dataMap=new HashMap();
				ChartParameter chartParam=new ChartParameter();
				chartParam.setChartTitle(ResourceFactory.getProperty("label.performance.noData"));
				this.getFormHM().put("chartParam", chartParam);
				isShowPercentVal="2";//默认按百分比显示
				hm.remove("b_personStation0");
			}
			else if(hm.get("b_personStation0")!=null&& "query".equals((String)hm.get("b_personStation0")))
			{
				planIds=(String)this.getFormHM().get("planIds");
				hm.remove("b_personStation0");
				ChartParameter chartParam=new ChartParameter();
				chartParam.setChartTitle(ResourceFactory.getProperty("label.performance.noData"));
				this.getFormHM().put("chartParam", chartParam);
			}
			else
			{
				if("true".equals(hm.get("encode"))){
					hm.remove("encode");
					planIds=PubFunc.decrypt((String)this.getFormHM().get("planIds"));
				}else{
					planIds=(String)this.getFormHM().get("planIds");
				}
				String a0100=(String)hm.get("a0100"); 
				a0100= "root".equalsIgnoreCase(a0100)?"":a0100;
				a0100 = PubFunc.decrypt(a0100);
				if(planIds==null || planIds.trim().length()<=0 || a0100.trim().length()<=0)
					return;
				byModel=bo.getByModelByPlanId(planIds);
				String e01a1=bo.getE01A1ByA0100(a0100);
				if(byModel!=null&& "1".equals(byModel)&&!"".equals(e01a1)){
					PostModalBo pmo = new PostModalBo(this.getFrameconn(),this.userView,planIds,byModel);
					ArrayList headList = pmo.getHeadList(); // 表头信息	
					gradeResultHtml = pmo.getGradeResultHtmlByModel(a0100,headList,e01a1);	
				}else{
					PostModalBo pmo = new PostModalBo(this.getFrameconn(),this.userView,planIds,byModel);
					ArrayList headList = pmo.getHeadList(); // 表头信息	
					gradeResultHtml = pmo.getGradeResultHtml(a0100,headList);	
				}			
				ChartParameter chartParam=new ChartParameter();
				chartParameterStr=(String)this.getFormHM().get("chartParameterStr");
				String busitype=(String)this.getFormHM().get("busitype");  // 业务分类字段 =0(绩效考核); =1(能力素质)
				if(StringUtils.isEmpty(chartParameterStr)&&"1".equals(busitype))
					chartParameterStr="`0`0`0`0`0`0`0`本人得分,岗位要求,`0,0`";// 在什么都没选的情况下默认显示 本人得分和岗位要求
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
				if(byModel!=null&& "1".equals(byModel)&&!"".equals(e01a1)){
					dataMap=bo.getSingleContrastAnalyseByModel(planIds,a0100,pointToNameList,selectids,isShowPercentVal,"41","1","logo");
				}else{
					//dataMap=bo.getSingleContrastAnalyse(planIds,a0100,pointToNameList,selectids,"0","41","1","logo");
					dataMap=bo.getSingleContrastAnalyse(planIds,a0100,pointToNameList,selectids,isShowPercentVal,"41","1","logo");
				}			
				scoreGradeStr=bo.getSingleContrastStr(planIds,a0100,selectids,"1");
				this.getFormHM().put("chartParam", chartParam);						
				this.getFormHM().put("object_id", a0100);
				this.getFormHM().put("objE01A1", getPerObjE01A1(planIds,a0100));
			}						
			this.getFormHM().put("chartParameterStr",chartParameterStr);
			this.getFormHM().put("isShowPercentVal",isShowPercentVal);
			this.getFormHM().put("scoreGradeStr",scoreGradeStr);
			this.getFormHM().put("pointToNameList",pointToNameList);
			this.getUserView().getHm().put("pointToNameList", pointToNameList);
			this.getFormHM().put("plan_ids",planIds);
			this.getFormHM().put("dataMap", dataMap);
			this.getFormHM().put("perPlanList",planList);
			this.getFormHM().put("planIds", planIds);

			//5.0以上版本图形设置为自动适应
			this.getFormHM().put("chartWidth","-1");			
		//	this.getFormHM().put("isShowPercentVal",isShowPercentVal);			
			String returnflag=(String)hm.get("returnflag");
			this.getFormHM().put("returnflag",returnflag);			
			
			this.getFormHM().put("gradeResultHtml",gradeResultHtml);
			this.getFormHM().put("isfromKhResult",hm.get("isfromKhResult"));//1：自助-绩效考评-考评反馈-本人能力素质考核结果 0:其他
						
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	
	/**获得考核对象的岗位编码*/
    public String getPerObjE01A1(String plan_id,String object_id)
    {
		String e01a1 = "";
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rowSet = null;
		try
		{
			StringBuffer sql = new StringBuffer("");
			sql.append("select e01a1 from per_result_" + plan_id + " where object_id='" + object_id + "' ");									
		    rowSet = dao.search(sql.toString());
		    if (rowSet.next())		    		    	
		    	e01a1 = isNull(rowSet.getString("e01a1"));		    	
		    
		    if(rowSet!=null)
		    	rowSet.close();
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return e01a1;
    }
    
    public String isNull(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str))
		    str = "";
		return str;
    }
    
}
