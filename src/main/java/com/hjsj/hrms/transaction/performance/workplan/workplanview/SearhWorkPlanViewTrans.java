package com.hjsj.hrms.transaction.performance.workplan.workplanview;

import com.hjsj.hrms.businessobject.performance.WorkPlanViewBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SearhWorkPlanViewTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		try
		{
			if(this.userView.getA0100()==null || this.userView.getA0100().trim().length()<=0)
				throw new GeneralException(ResourceFactory.getProperty("selfservice.module.pri"));
			
			HashMap rMap = (HashMap)this.getFormHM().get("requestPamaHM");
			//2016/1/28 wangjl  工作纪实新增一个查询条件
			String searchterm=(String)this.getFormHM().get("searchterm");
			String searchflag = (String) rMap.get("searchflag");
			searchterm = "请输入批示、内容".equals(searchterm)?"":searchterm;
			searchterm = PubFunc.keyWord_filter(searchterm);
			if(!"view".equals(searchflag)){
				searchterm = "";
				this.getFormHM().put("searchterm",searchterm);
			}
			rMap.remove("searchflag");
			String workType=(String)this.getFormHM().get("workType");
			String state=(String)this.getFormHM().get("state");
			String year="";
			String month="";
			WorkPlanViewBo bo = new WorkPlanViewBo(this.getFrameconn(),this.userView,state,this.userView.getDbname(),this.userView.getA0100());
			
			if(rMap.get("init")!=null&& "init".equals((String)rMap.get("init")))
			{
				year=Calendar.getInstance().get(Calendar.YEAR)+"";
				month=(Calendar.getInstance().get(Calendar.MONTH)+1)+"";
				bo.analyseParameter();//刚进来时，加载一次参数设置
				rMap.remove("init");
			}else 
			{
				year = (String)this.getFormHM().get("year");
				month=(String)this.getFormHM().get("month");
			}
			ArrayList yearList = bo.getYearList();
			String status="";
			String a0101="";
			ArrayList monthList =WorkPlanViewBo.getMonthList();
			ArrayList planList=bo.getWorkList(year, month, status, a0101, searchterm);
			this.getFormHM().put("workType", workType);
			this.getFormHM().put("state", state);
			this.getFormHM().put("year",year);
			this.getFormHM().put("month",month);
			this.getFormHM().put("yearList", yearList);
			this.getFormHM().put("planList",planList);
			this.getFormHM().put("monthList",monthList);
			this.getFormHM().put("workNbase",(String)WorkPlanViewBo.workParametersMap.get("nbase"));
			this.getFormHM().put("sp_relation",(String)WorkPlanViewBo.workParametersMap.get("sp_relation"));
			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}	

}
