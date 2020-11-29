package com.hjsj.hrms.transaction.performance.perAnalyse;

import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class InitMcontrastAnalyseTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		String planIds=(String)this.getFormHM().get("planIds");
		this.getFormHM().put("plan_ids",planIds);
		ChartParameter chartParam=new ChartParameter();
		chartParam.setChartTitle(ResourceFactory.getProperty("label.performance.noData"));
		this.getFormHM().put("chartParam", chartParam);
		this.getFormHM().put("dataMap", new HashMap());
		this.getFormHM().put("pointToNameList",new ArrayList());
		
	}

}
