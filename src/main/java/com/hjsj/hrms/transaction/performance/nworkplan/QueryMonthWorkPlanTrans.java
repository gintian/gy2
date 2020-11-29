package com.hjsj.hrms.transaction.performance.nworkplan;

import com.hjsj.hrms.businessobject.performance.nworkplan.NworkPlanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class QueryMonthWorkPlanTrans extends IBusiness{
	public void execute() throws GeneralException  {
		try{
			HashMap rMap = (HashMap)this.getFormHM().get("requestPamaHM");
			//String queryContent = "";
			String personPage =(String)this.getFormHM().get("personPage");
			String state = (String)this.getFormHM().get("state");
	        String backurl = (String)this.getFormHM().get("backurl");
	        String isChuZhang = (String)this.getFormHM().get("isChuZhang");
	        String belong_type = (String)this.getFormHM().get("belong_type");
	        ArrayList list = new ArrayList();
			if(rMap.get("init")!=null&& "init".equals((String)rMap.get("init")))
			{
				this.getFormHM().put("queryContent", "");
				this.getFormHM().put("queryDataList", list);
			}else if(rMap.get("init")!=null&& "query".equals((String)rMap.get("init")))
			{
				String queryContent = (String)this.getFormHM().get("queryContent");
				NworkPlanBo bo = new NworkPlanBo(this.getFrameconn(),this.userView,(String)this.userView.getHm().get("nbase"),(String)this.userView.getHm().get("a0100"));
				if(!"".equals(queryContent))
				list = bo.getQueryDataList(belong_type,personPage,state,queryContent,isChuZhang,(String)this.userView.getHm().get("opt"));
				this.getFormHM().put("queryDataList", list);
				this.getFormHM().put("queryContent", queryContent);
			}else if(rMap.get("init")!=null&& "refresh".equals((String)rMap.get("init")))
			{
				String queryContent = (String)this.getFormHM().get("queryContent");
				NworkPlanBo bo = new NworkPlanBo(this.getFrameconn(),this.userView,(String)this.userView.getHm().get("nbase"),(String)this.userView.getHm().get("a0100"));
				if(!"".equals(queryContent))
				list = bo.getQueryDataList(belong_type,personPage,state,queryContent,isChuZhang,(String)this.userView.getHm().get("opt"));
				this.getFormHM().put("queryDataList", list);
				this.getFormHM().put("queryContent", queryContent);
			}
			this.getFormHM().put("personPage", personPage);
			this.getFormHM().put("state", state);
			this.getFormHM().put("isChuZhang", isChuZhang);
			this.userView.getHm().put("backurl", backurl);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}	
}
