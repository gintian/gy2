package com.hjsj.hrms.transaction.performance;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:SearchHandSelTrans.java</p>
 * <p>Description:绩效手工选择通用</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-07-17 11:11:11</p> 
 * @author JinChunhai
 * @version 5.0
 */

public class SearchHandSelTrans extends IBusiness
{
	
	public void execute() throws GeneralException
	{
		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String busitype = (String) hm.get("busitype");  // 业务分类 =0(绩效考核); =1(能力素质)
		hm.remove("busitype");
		String planid = (String) hm.get("planid");
		CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
        if(!_bo.isHavePriv_self(this.userView, planid)){	
        	return;
        } 
		hm.remove("planid");
		String dispPlanName = (String) hm.get("dispPlanName");
		hm.remove("dispPlanName");
		dispPlanName=dispPlanName==null?"0":dispPlanName;

		String opt = (String) hm.get("opt");
		hm.remove("opt");
		String flag0=(String) hm.get("flag0");//flag0=1,审批关系手工选人
		hm.remove("flag0");
		
		String object_type="2";
		String planName="";
		if(planid!=null && planid.length()>0)
		{
			RecordVo vo = new RecordVo("per_plan");
			vo.setString("plan_id", planid);
			ContentDAO dao = new ContentDAO(this.frameconn);
			try 
			{
				vo = dao.findByPrimaryKey(vo);
			} catch (Exception e) 
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			
			object_type=Integer.toString(vo.getInt("object_type"));
			if("1".equals(dispPlanName))
				planName="计划： "+vo.getString("name");
		}	
		
		this.getFormHM().put("planName", planName);
		this.getFormHM().put("object_type", object_type);
		this.getFormHM().put("objName", "");
		this.getFormHM().put("objsStr", "");
		this.getFormHM().put("aplanid", planid);
		this.getFormHM().put("busitype", busitype);
		this.getFormHM().put("opt", opt);		

	}
}
