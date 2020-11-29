package com.hjsj.hrms.transaction.workplan.plan_track;

import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_track.RelatePerformancePlanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class RelatePerformancePlanTrans extends IBusiness
{
	public void execute() throws GeneralException
	{
			
	    HashMap hm = this.getFormHM();
        String oprType=(String)hm.get("oprType");
        String periodType =(String)hm.get("periodType");
        periodType=(periodType==null)?"":periodType;                           
        String periodYear =(String)hm.get("periodYear");
        periodYear=(periodYear==null)?"":periodYear; 
        String periodMonth =(String)hm.get("periodMonth");
        periodMonth=(periodMonth==null)?"":periodMonth; 
        String periodWeek =(String)hm.get("periodWeek"); 
        periodWeek=(periodWeek==null)?"":periodWeek; 
        String planType =(String)hm.get("planType");                
        planType=(planType==null)?"":planType;
        planType="undefined".equals(planType)?"":planType;
        planType=(planType==null)?"2":planType;
        
        String planId =(String)hm.get("planId");
        planId=(planId==null)?"":planId;
        
        String objectIds =(String)hm.get("objectId");  
        objectIds=(objectIds==null)?"":objectIds;
        String [] arrObjectIds= objectIds.split(",");
        for (int i=0;i<arrObjectIds.length;i++){
            String objectid = WorkPlanUtil.decryption( arrObjectIds[i]);
            arrObjectIds[i]=objectid;
        }
        
		try
		{
		    hm.put("info", "true");
		    RelatePerformancePlanBo relateBo= new RelatePerformancePlanBo(this.frameconn,this.userView);
		    if ("initPlan".equals(oprType)){   
		        ArrayList setlist = relateBo.searchExamPlanList(planType,periodType,periodYear,periodMonth,periodWeek);
		        hm.put("setlist", setlist);
            }		  
		    else if ("initAddedPlan".equals(oprType)){   
		        ArrayList setlist = relateBo.getPlanListById(planId);
		        hm.put("setlist", setlist);
		    }		  
		    else if ("checkBody".equals(oprType)){ //此计划没有设置上级、上上级
		        String info =relateBo.checkHaveSuperBodySet(planId);
		        if (info.length()>0){
		            hm.put("info", "false");
		            hm.put("bodySet", info);
		        }
		    }		
		  			
		} catch (Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}



}
