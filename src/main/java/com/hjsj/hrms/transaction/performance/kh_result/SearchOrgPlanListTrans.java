package com.hjsj.hrms.transaction.performance.kh_result;

import com.hjsj.hrms.businessobject.performance.kh_result.ResultBo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 查找部门考核计划列表
 * <p>Title:SearchOrgPlanListTrans.java</p>
 * <p>Description>:SearchOrgPlanListTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-10-6 下午02:06:01</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class SearchOrgPlanListTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			//非在职人员不允许使用改功能
			if(!"USR".equalsIgnoreCase(userView.getDbname())) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("notUsr.no.use.func")));
			}
			ResultBo bo = new ResultBo(this.getFrameconn());
			HashMap map=(HashMap)this.getFormHM().get("requestPamaHM");
			String distinctionFlag=(String)map.get("distinctionFlag");
			String model=(String)this.getFormHM().get("model");
			String object_id=bo.getManagePrivCode(this.userView);
			String modelType=(String)map.get("modelType");
			String year = "-1";
			if(map.get("year")!=null)
			{
				year=(String)map.get("year");
				map.remove("year");
			}
			ArrayList planList=bo.getOrgPlanList(object_id, distinctionFlag,modelType,year,this.userView);
			ArrayList yearList  = bo.getYearList(object_id, distinctionFlag, modelType);
			this.getFormHM().put("planList", planList);
			this.getFormHM().put("model",model);
			this.getFormHM().put("distinctionFlag", distinctionFlag);
			this.getFormHM().put("object_id",object_id);
			this.getFormHM().put("performanceYear", year);
			this.getFormHM().put("performanceYearList", yearList);
			this.getFormHM().put("modelType", modelType);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
