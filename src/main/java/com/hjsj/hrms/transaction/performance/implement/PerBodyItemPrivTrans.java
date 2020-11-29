package com.hjsj.hrms.transaction.performance.implement;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:PerBodyItemPrivTrans.java</p>
 * <p>Description:考核主体项目权限</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-12-14 11:11:11</p>
 * @author JinChunhai
 * @version 1.0 
 */

public class PerBodyItemPrivTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
    	
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String planid=(String)hm.get("planid");
		String objectid=(String)hm.get("objectid");
		
		PerformanceImplementBo pb=new PerformanceImplementBo(this.getFrameconn());	
		String planStatus = pb.getPlanStatus(planid);
		HashMap map=pb.getItemPriv(objectid,planid);
		ArrayList pointItemList = (ArrayList)map.get("pointItemList");
		ArrayList itemprivList = (ArrayList)map.get("itemPrivList");
		this.getFormHM().put("itemprivList",itemprivList);
		this.getFormHM().put("pointItemList",pointItemList);
		this.getFormHM().put("planid",planid);
		this.getFormHM().put("planStatus",planStatus);
		
		ExamPlanBo bo = new ExamPlanBo(this.frameconn);
		String khobjname = bo.getKhObjName(objectid, planid);
		this.getFormHM().put("khObject", khobjname);
	
	
    }
}
