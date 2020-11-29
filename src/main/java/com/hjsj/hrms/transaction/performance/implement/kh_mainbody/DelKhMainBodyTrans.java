package com.hjsj.hrms.transaction.performance.implement.kh_mainbody;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:DelKhMainBodyTrans.java</p>
 * <p>Description:考核实施/指定考核主体/删除考核主体</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-01 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class DelKhMainBodyTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		//String delStr = (String) hm.get("deletestr");
		String delStr = (String)this.getFormHM().get("paramStr");
		delStr = delStr.substring(0, delStr.length() - 1);
		String[] ids = delStr.split("@");//格式: mainbody_id:objectID
		String plan_id = (String)this.getFormHM().get("planid");
		
		PerformanceImplementBo bo = new PerformanceImplementBo (this.getFrameconn(),this.getUserView());
		bo.delKhMainBody(ids,plan_id); 
		String objectid=(String)hm.get("objectid");
		String method=(String) this.getFormHM().get("method");
		if("1".equals(method)){
			bo.agreeSubjectNumber(plan_id, objectid, "per_pointpriv_"+plan_id);
		}
    }

}
