package com.hjsj.hrms.transaction.competencymodal.personPostModal;

import com.hjsj.hrms.businessobject.competencymodal.personPostModal.PersonPostModalBo;
import com.hjsj.hrms.businessobject.performance.PerformanceAnalyseBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchPersonPostMatchTrans.java</p>
 * <p>Description:人岗匹配、岗人匹配</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-01-15 11:11:11</p>
 * @author JinChunhai
 * @version 5.0
 */

public class SearchPersonPostMatchTrans extends IBusiness
{

    public void execute() throws GeneralException
    {  
    	
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");   
		String a_code = (String)hm.get("a_code");	
		String signLogo = (String)hm.get("signLogo");
		hm.remove("a_code");							
		hm.remove("signLogo");
		
		String planId = (String)this.getFormHM().get("planId");	
		String plan_id = (String)this.getFormHM().get("plan_id");	
		String orgCode = (String)this.getFormHM().get("orgCode");
		String subSetMenu = "-1";	// 代码类指标
		String layer = "";	// 代码类指标层级
		
		if(signLogo!=null && signLogo.trim().length()>0 && "changePlan".equalsIgnoreCase(signLogo))
		{
			a_code = orgCode;
//			subSetMenu = (String)this.getFormHM().get("subSetMenu");	// 代码类指标
//			layer = (String)this.getFormHM().get("layer");	// 代码类指标层级
		}
//		else
//			plan_id = planId;
		
		if(signLogo!=null && signLogo.trim().length()>0 && "changeSubSetMenu".equalsIgnoreCase(signLogo))
		{
			a_code = orgCode;
			subSetMenu = (String)this.getFormHM().get("subSetMenu");	// 代码类指标
			layer = (String)this.getFormHM().get("layer");	// 代码类指标层级
		}
		
//		String creatDate = PubFunc.getStringDate("yyyy-MM-dd"); // 获得系统当前时间
		// 查询的参数
		String chart_type = (String)this.getFormHM().get("chart_type");
		if(chart_type==null || chart_type.trim().length()<=0)			
			chart_type="20";
		
		ArrayList perDegreeList = new ArrayList();
		ArrayList dataList = new ArrayList();
		ArrayList subSetMenuList = new ArrayList();
		ArrayList codeItemList = new ArrayList();
		try
		{	   
			PerformanceAnalyseBo pao = new PerformanceAnalyseBo(this.getFrameconn(),this.userView);
			ArrayList planList = pao.getPlanList_commonData("7",0,0,this.getUserView(),planId,"1"); // 符合条件的能力素质考核计划
			
			PersonPostModalBo bo = new PersonPostModalBo(this.getFrameconn(),this.userView);											    		    
		    if(plan_id==null || plan_id.trim().length()<=0)
		    {
		    	if(planList!=null && planList.size()>0)
		    		plan_id = ((CommonData)planList.get(0)).getDataValue();
		    }		    
		    if(plan_id==null || plan_id.trim().length()<=0)
		    {
		    	this.getFormHM().put("chartHeight","550");
				this.getFormHM().put("chartWidth","-1");
				
				this.getFormHM().put("plan_id",plan_id);		
			    this.getFormHM().put("planList",planList);	
			    this.getFormHM().put("subSetMenu",subSetMenu);
			    this.getFormHM().put("layer",layer);
			    this.getFormHM().put("subSetMenuList",subSetMenuList);		    
			    this.getFormHM().put("codeItemList",codeItemList);
				this.getFormHM().put("orgCode",a_code);			
				this.getFormHM().put("dataList",dataList);
				this.getFormHM().put("perDegreeList",perDegreeList);
		    	return;
		    }
		    
		    perDegreeList = bo.getDegreeList(plan_id); // 考核等级数据
		    
		    
		    		    
		    subSetMenuList = bo.searchPlanSubSetMenuList(plan_id); // 评估结构表中包含人员基本子集的代码类指标（单位、部门、岗位指标除外）和对象类别
		    
		    if(subSetMenu!=null && subSetMenu.trim().length()>0 && !"-1".equalsIgnoreCase(subSetMenu))
		    {
		    	if(!"29".equals(chart_type) && !"31".equals(chart_type))
		    		chart_type="29";		    	
		    	codeItemList = bo.searchPlanCodeLayer(subSetMenu);
		    	
//		    	if(layer!=null && layer.trim().length()>0 && Integer.parseInt(layer)>1)
//		    	{
		    		
		    		
//		    	}
		    	
		    	dataList = bo.getCodeDataPictrueList(plan_id,chart_type,a_code,subSetMenu,layer);
		    	
		    }else
		    {
		    	if("29".equals(chart_type))
		    		chart_type="11";
		    	else if("31".equals(chart_type))
		    		chart_type="12";
		    	dataList = bo.getDataPictrueList(plan_id,chart_type,a_code);
		    }
		    
		    if("20".equals((String)this.getFormHM().get("chart_type")) && "1".equals((String)this.getFormHM().get("isShow3D")))
				chart_type="5";			
			else if("5".equals((String)this.getFormHM().get("chart_type")) && "0".equals((String)this.getFormHM().get("isShow3D")))
				chart_type="20";		    
			else if("11".equals((String)this.getFormHM().get("chart_type")) && "1".equals((String)this.getFormHM().get("isShow3D")))
				chart_type="12";			
			else if("12".equals((String)this.getFormHM().get("chart_type")) && "0".equals((String)this.getFormHM().get("isShow3D")))
				chart_type="11";
			else if("29".equals((String)this.getFormHM().get("chart_type")) && "1".equals((String)this.getFormHM().get("isShow3D")))
				chart_type="31";			
			else if("31".equals((String)this.getFormHM().get("chart_type")) && "0".equals((String)this.getFormHM().get("isShow3D")))
				chart_type="29";
		    		    
			this.getFormHM().put("chart_type",chart_type);
			if(!"12".equals(chart_type) && !"5".equals(chart_type) && !"31".equals(chart_type))
				this.getFormHM().put("isShow3D", "0");		    		    
			
			//5.0以上版本图形设置为自动适应
			this.getFormHM().put("chartHeight","550");
			this.getFormHM().put("chartWidth","-1");
			
			this.getFormHM().put("plan_id",plan_id);		
		    this.getFormHM().put("planList",planList);	
		    this.getFormHM().put("subSetMenu",subSetMenu);	
		    this.getFormHM().put("layer",layer);
		    this.getFormHM().put("subSetMenuList",subSetMenuList);		    
		    this.getFormHM().put("codeItemList",codeItemList);
			this.getFormHM().put("orgCode",a_code);			
			this.getFormHM().put("dataList",dataList);
			this.getFormHM().put("perDegreeList",perDegreeList);
			
		} catch (Exception ex)
		{
		    ex.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ex);
		}
		
    }         
	
}