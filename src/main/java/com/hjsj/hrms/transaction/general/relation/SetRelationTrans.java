package com.hjsj.hrms.transaction.general.relation;


import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>
 * Title:SetPlanIdTrans.java
 * </p>
 * <p>
 * Description:考核实施 绩效评估 设置考核计划
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-06-26 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 */
public class SetRelationTrans extends IBusiness
{

	public void execute() throws GeneralException
	{

		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String relationid=(String)hm.get("relationid");
		String actor_type =(String)hm.get("actor_type");
		this.getFormHM().put("relationid",relationid);
		this.getFormHM().put("actor_type",actor_type);
		//清理form中得值
		hm.remove("code");
		hm.remove("opt");
		hm.remove("codeset");
		this.getFormHM().put("objSelected","");
		if(actor_type!=null&& "4".equals(actor_type))
		{

			String groupid = this.userView.getGroupId();
			if (groupid == null||groupid.length()<=0) {
				throw new GeneralException(ResourceFactory.getProperty("sys.user.admin.err"));
			} else {}
		
		}
		
	}

}
