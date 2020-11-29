package com.hjsj.hrms.transaction.performance.objectiveManage.myObjective;

import com.hjsj.hrms.businessobject.performance.objectiveManage.MyObjectiveBo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchMyObjectivePlanTrans.java</p>
 * <p> Description:我的目标</p>
 * <p>Company:hjsj</p>
 * <p> create time:2008-08-08 11:11:11</p> 
 * @author JinChunhai
 * @version 1.0 
 */

public class SearchMyObjectivePlanTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		
		try
		{
			if(this.userView.getA0100()==null|| "".equals(this.userView.getA0100()))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("employ.no.use.model")));
			//非在职人员不允许使用改功能
			if(!"USR".equalsIgnoreCase(userView.getDbname())) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("notUsr.no.use.func")));
			}
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			String returnflag=(String)hm.get("returnflag");
			this.getFormHM().put("returnflag", returnflag);
			
			String opt=(String)hm.get("opt");
			String year=/*Calendar.getInstance().get(Calendar.YEAR)+*/"-1";
			String quarter="-1";
			String month="-1";
			String status="-2";
	        String a0100=this.userView.getA0100();
	        MyObjectiveBo bo = new MyObjectiveBo(this.getFrameconn(),this.userView);
			if("1".equals(opt))
			{				
			}
			else if("2".equals(opt))
			{
				year = (String)this.getFormHM().get("year");
				quarter = (String)this.getFormHM().get("quarter");
				status = (String)this.getFormHM().get("status");
				month=(String)this.getFormHM().get("month");
			}
			else if("3".equals(opt))
			{
				year = (String)hm.get("year");
				quarter = (String)hm.get("quarter");
				status = (String)hm.get("status");
				month=(String)hm.get("month");
			}
			ArrayList planList = bo.getOrgPlanList(a0100, year, quarter, month, status,null);
			ArrayList yearList = bo.getYearList();
			ArrayList quarterList = bo.getQuarterList();
			ArrayList monthList = bo.getMonthList();
			ArrayList statusList = bo.getStatusList();
			this.getFormHM().put("myPlanList",planList);
			this.getFormHM().put("year", year);
			this.getFormHM().put("yearList", yearList);
			this.getFormHM().put("month", month);
			this.getFormHM().put("monthList", monthList);
			this.getFormHM().put("quarter", quarter);
			this.getFormHM().put("quarterList", quarterList);
			this.getFormHM().put("status",status);
			this.getFormHM().put("statusList", statusList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
