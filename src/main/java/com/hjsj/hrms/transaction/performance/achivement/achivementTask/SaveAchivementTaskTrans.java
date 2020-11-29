package com.hjsj.hrms.transaction.performance.achivement.achivementTask;

import com.hjsj.hrms.businessobject.performance.achivement.AchivementTaskBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:保存绩效任务书</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 9, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class SaveAchivementTaskTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String[] right_fields=(String[])this.getFormHM().get("right_fields"); 
			ArrayList targetColumnList=(ArrayList)this.getFormHM().get("targetColumnList");
			AchivementTaskBo bo=new AchivementTaskBo(this.getFrameconn(),this.userView);
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("opt");   // edit/new
			String root_url=bo.SaveAchivementTask(opt,right_fields,targetColumnList);
			this.getFormHM().put("root_url",root_url);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
