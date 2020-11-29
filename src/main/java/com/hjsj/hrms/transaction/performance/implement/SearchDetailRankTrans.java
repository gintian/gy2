package com.hjsj.hrms.transaction.performance.implement;

import com.hjsj.hrms.businessobject.performance.implement.DetailRankBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:SearchDetailRankTrans.java</p> 
 *<p>Description:考核实施/评价关系明细及权重</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2011-09-13 10:15:35</p> 
 *@author JinChunhai
 *@version 5.0
 */

public class SearchDetailRankTrans extends IBusiness
{
	
	public void execute() throws GeneralException
    {
		   
		try
		{
			String plan_id=(String)this.getFormHM().get("planid");	
			String code=(String)this.getFormHM().get("code");	
			
			// 评价关系明细表信息
			String detailHeadHtml = "";  
			ArrayList objectidTypeList = new ArrayList();  // 取得计划对应的对象类别列表
			ArrayList mainbodyTypeList = new ArrayList();  // 取得主体类别列表
			HashMap mainbodyMap = new HashMap();       // 取得范围内考核对象对应的考核主体
			HashMap mainbodyDefaultRankMap = new HashMap(); // 取得设置的主体默认权重
			HashMap mainbodyRankMap = new HashMap();   // 取得设置的动态主体权重
						
			if (plan_id != null && plan_id.trim().length() > 0)
			{				
				DetailRankBo bo = new DetailRankBo(this.getFrameconn(), this.userView, plan_id);
				
				detailHeadHtml = bo.getDetailHeadHtml(code);
								
				objectidTypeList = bo.getObjectTypeList();
				mainbodyTypeList = bo.getMainbodyTypeList();
				mainbodyMap = bo.getMainbodyMap();
				mainbodyDefaultRankMap = bo.getMainbodyDefaultRankMap();
				mainbodyRankMap = bo.getMainbodyRankMap();
				
			}
			
			this.getFormHM().put("detailHeadHtml", detailHeadHtml);
			this.getFormHM().put("objectidTypeList", objectidTypeList);
			this.getFormHM().put("mainbodyTypeList", mainbodyTypeList);
			this.getFormHM().put("mainbodyMap", mainbodyMap);
			this.getFormHM().put("mainbodyDefaultRankMap", mainbodyDefaultRankMap);
			this.getFormHM().put("mainbodyRankMap", mainbodyRankMap);
			
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}		
		
    }
	
}
