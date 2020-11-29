package com.hjsj.hrms.transaction.performance.interview;

import com.hjsj.hrms.businessobject.performance.interview.PerformanceInterviewBo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchInterviewListTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String plan_id=(String)map.get("plan_id");
			/**=0不加计划限制=1加计划限制*/
			String  type=(String)map.get("type");
			String khObjWhere=(String)map.get("khObjWhere");
			/*if(type.equals("0"))
				plan_id="-1";*/
			if((this.userView.getA0100()==null|| "".equals(this.userView.getA0100()))&& "0".equals(type))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("employ.no.use.model")));
			PerformanceInterviewBo bo = new PerformanceInterviewBo(this.getFrameconn());
			ArrayList dbname= new ArrayList();
			dbname.add("usr");
			ArrayList personList = bo.getUnderlingEmployeeList(this.userView.getUserPosId(), dbname,this.userView,plan_id,type,khObjWhere);
			this.getFormHM().put("personList",personList);
			ArrayList planList = bo.getPlanList(this.userView);
			this.getFormHM().put("planList", planList);
			this.getFormHM().put("plan_id",plan_id);
			this.getFormHM().put("type", type);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
