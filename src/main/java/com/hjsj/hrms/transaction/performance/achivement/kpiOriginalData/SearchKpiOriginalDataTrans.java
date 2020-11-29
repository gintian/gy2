package com.hjsj.hrms.transaction.performance.achivement.kpiOriginalData;

import com.hjsj.hrms.businessobject.performance.achivement.kpiOriginalData.KpiOriginalDataBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchKpiOriginalDataTrans.java</p>
 * <p>Description:KPI原始数据录入</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-07-25 10:09:23</p>
 * @author JinChunhai
 * @version 5.0
 */

public class SearchKpiOriginalDataTrans extends IBusiness
{

    public void execute() throws GeneralException
    {  
    	
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
		String a_code=(String)hm.get("a_code");
		String refreshKey=(String)hm.get("refreshKey");
		String refreshData=(String)hm.get("refreshData");	
		hm.remove("a_code");		
		hm.remove("refreshKey");							
		hm.remove("refreshData");
		
		String unionOrgCode = (String)this.getFormHM().get("unionOrgCode");
		if(refreshData!=null && refreshData.trim().length()>0 && "yesOk".equalsIgnoreCase(refreshData))
		{
			a_code=unionOrgCode;
		}				
		
		String creatDate = PubFunc.getStringDate("yyyy-MM-dd"); // 获得系统当前时间
		// 查询的参数
		String cycle = (String)this.getFormHM().get("cycle");	// 考核周期	
		String noYearCycle = (String)this.getFormHM().get("noYearCycle");	// 非年度考核周期	
		String objectType = (String) this.getFormHM().get("objectType"); // 对象类别：1 单位 2 人员
		String year = (String) this.getFormHM().get("year");

		if(cycle==null || cycle.trim().length()<=0 || "-1".equalsIgnoreCase(cycle))
			cycle = "0";	
		if((noYearCycle==null || noYearCycle.trim().length()<=0) || (refreshKey!=null && refreshKey.trim().length()>0 && "changeCycle".equalsIgnoreCase(refreshKey)) )
			noYearCycle = "01";
		if("0".equalsIgnoreCase(cycle))
			noYearCycle = "";		
		if(year==null || year.trim().length()<=0)
			year = creatDate.substring(0, 4);
		
		String checkName = (String)this.getFormHM().get("checkName");				
		if(checkName.indexOf("'")!=-1)				
			checkName = checkName.replaceAll("'","‘"); 									
		
		try
		{	   
			KpiOriginalDataBo bo = new KpiOriginalDataBo(this.getFrameconn(),this.userView);
			
			// 新建KPI指标表	  在启动时会自动维护此表，所以在此处去掉		 
//			bo.builtKpiTargetTable();
			
			// 新建业绩数据采集表	 在启动时会自动维护此表，所以在此处去掉		 
//			bo.builtAchievementTable();
						
		    ArrayList setlist = bo.searchKpiOriginalData(cycle,objectType,year,noYearCycle,checkName,a_code);
		   	
		    if(setlist==null || setlist.size()<=0)
		    {
		    	this.getFormHM().put("setlist", new ArrayList());
			    this.getFormHM().put("object_ids", "");
		    }else
		    {
		    	this.getFormHM().put("setlist", setlist.get(0));
			    this.getFormHM().put("object_ids", setlist.get(1));
		    }		    
		    this.getFormHM().put("cycle",cycle);
		    this.getFormHM().put("year",year);
		    this.getFormHM().put("noYearCycle",noYearCycle);
		    this.getFormHM().put("yearList", bo.getYears(objectType));
		    this.getFormHM().put("objecTypeList", bo.getObjecType());
		    this.getFormHM().put("cycleList", bo.getCycles());
		    ArrayList noYearCycleList=bo.getCycleList(cycle);     // 获得考核周期下的非年度数据					
			this.getFormHM().put("noYearCycleList",noYearCycleList);
			this.getFormHM().put("unionOrgCode",a_code);
			this.getFormHM().put("refreshKey",refreshKey);
			
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			String onlyFild = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
			this.getFormHM().put("onlyFild",onlyFild);
		    
		} catch (Exception ex)
		{
		    ex.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ex);
		}
		
    }         
	
}