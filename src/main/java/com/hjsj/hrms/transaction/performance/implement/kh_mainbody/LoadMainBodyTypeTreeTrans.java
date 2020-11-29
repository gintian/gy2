package com.hjsj.hrms.transaction.performance.implement.kh_mainbody;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:LoadMainBodyTypeTreeTrans.java</p>
 * <p>Description:考核实施/指定考核主体/加载主体类别树</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-08-21 09:05:07</p>
 * @author JinChunhai
 * @version 5.0
 * 
 */


public class LoadMainBodyTypeTreeTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");	
		String objectIds = (String) hm.get("objIDs");
		PerformanceImplementBo bo = new PerformanceImplementBo (this.getFrameconn(),this.getUserView());
		String[] objs = objectIds.split("@");
		String planid=(String)this.getFormHM().get("planid");//1:团队(对单位|部门) 2:人员
		if(hm.get("fenfaPlanId")!=null)
		{
			planid=(String)hm.get("fenfaPlanId");
			hm.remove("fenfaPlanId");
		}
		CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
        if(!_bo.isHavePriv(this.userView, planid)){	
        	return;
        } 
		this.getFormHM().put("planid", planid);
		
		this.getFormHM().put("object_type", bo.getPlanVo(planid).getString("object_type"));
		
		ArrayList objectList=bo.getKhObjectsList(objectIds,planid);
		this.getFormHM().put("khObjectList", objectList);
		if(objs.length>1)
		    this.getFormHM().put("khObject", "all");
		else if((objs.length==1))
		    this.getFormHM().put("khObject",objs[0]);	
    }

}
