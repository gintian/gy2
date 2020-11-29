package com.hjsj.hrms.transaction.performance.implement;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:SetPlanIdTrans.java</p>
 * <p>Description:考核实施 绩效评估 设置考核计划</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-06-26 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SetPlanIdTrans extends IBusiness
{

	public void execute() throws GeneralException
	{

		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String plan_id=(String)hm.get("plan_id");
		plan_id =PubFunc.decryption(plan_id);
		String busitype=(String)hm.get("busitype");	// 业务分类字段 =0(绩效考核); =1(能力素质)	
		
		if(plan_id!=null && plan_id.trim().length()>0 && "~".equalsIgnoreCase(plan_id.substring(0,1))) // JinChunhai 2012-08-07 如果是通过转码传过来的需解码
        { 
			String _temp = SafeCode.decode(plan_id);
			plan_id = _temp.substring(1); 
        }
		if(busitype==null || busitype.trim().length()<=0)
			busitype = "0";
		
		this.getFormHM().put("planid",plan_id);
		this.getFormHM().put("busitype",busitype);
		
		// 查询当前计划的全部主体打分确认标识 by 刘蒙
		PerformanceImplementBo pb=new PerformanceImplementBo(this.getFrameconn());
		this.formHM.put("optMap", pb.getOptMap(plan_id));
		
	}

}
