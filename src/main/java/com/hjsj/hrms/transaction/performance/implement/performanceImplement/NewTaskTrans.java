package com.hjsj.hrms.transaction.performance.implement.performanceImplement;

import com.hjsj.hrms.businessobject.performance.kh_plan.KhTemplateBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:NewTaskTrans.java</p>
 * <p>Description:考核实施/目标卡制定/新建任务</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-12-07 14:21:56</p> 
 * @author JinChunhai
 * @version 5.0
 */

public class NewTaskTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String itemid=(String)hm.get("itemid");
			String taskcontent = SafeCode.decode((String)hm.get("taskcontent"));
			String type=(String)hm.get("type");
			hm.remove("type");
			
			if(itemid==null || itemid.trim().length()<=0 || "null".equalsIgnoreCase(itemid))
			{
				itemid = (String)this.getFormHM().get("beforeItemid");
			}
			
			String objCode=(String)this.getFormHM().get("objCode");
			String planid=(String)this.getFormHM().get("planid");

			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn(),"1",objCode,planid,"targetCard");	
			bo.insertTargetTask(taskcontent,itemid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
