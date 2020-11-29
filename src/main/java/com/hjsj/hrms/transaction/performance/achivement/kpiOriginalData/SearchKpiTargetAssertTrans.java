package com.hjsj.hrms.transaction.performance.achivement.kpiOriginalData;

import com.hjsj.hrms.businessobject.performance.achivement.kpiOriginalData.KpiOriginalDataBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchKpiTargetAssertTrans.java</p>
 * <p>Description:KPI指标维护</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-07-25 10:09:23</p>
 * @author JinChunhai
 * @version 5.0
 */

public class SearchKpiTargetAssertTrans extends IBusiness
{

    public void execute() throws GeneralException
    {  
    	
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
		String a_code=(String)hm.get("a_code");
		hm.remove("a_code");
				
		// 查询的参数
//		String cycle = (String)this.getFormHM().get("cycle");		
		String targetType = (String) this.getFormHM().get("targetType");
		if(targetType!=null && targetType.trim().length()>0 && targetType.indexOf("'")!=-1)				
			targetType = targetType.replaceAll("'","‘"); 	
		String affB0110 = (String) this.getFormHM().get("affB0110");
//		String userbase = (String)this.getFormHM().get("userbase");
		String targetName = (String)this.getFormHM().get("targetName");				
		if(targetName!=null && targetName.trim().length()>0 && targetName.indexOf("'")!=-1)				
			targetName = targetName.replaceAll("'","‘"); 							
		
		try
		{	   
			KpiOriginalDataBo bo = new KpiOriginalDataBo(this.getFrameconn(),this.userView);						
						
		    ArrayList setlist = bo.searchKpiTargetAssert(targetName,targetType,affB0110);		   
		   
		    this.getFormHM().put("setlist", setlist);
//		    this.getFormHM().put("unionOrgCode",a_code);
		    this.getFormHM().put("targetTypeList", bo.getTargetType());
		    
		    
		} catch (Exception ex)
		{
		    ex.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ex);
		}
		
    }         
	
}