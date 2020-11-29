package com.hjsj.hrms.transaction.performance.achivement.achivementTask;

import com.hjsj.hrms.businessobject.performance.achivement.AchivementTaskBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 9, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class InitAchivementTaskTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("opt");   // edit/new
			String target_id=(String)hm.get("target_id");
			AchivementTaskBo bo=new AchivementTaskBo(this.getFrameconn(),this.userView);
			ArrayList targetColumnList=bo.getTargetColumnList(target_id, opt);
			ArrayList pointClassList=bo.getPointClassList2();
			ArrayList pointList=new ArrayList();
			if("edit".equals(opt))
				pointList=bo.getTargetPointList(target_id);
			this.getFormHM().put("selectedPointList",pointList);
			this.getFormHM().put("pointClassList",pointClassList);
			this.getFormHM().put("targetColumnList",targetColumnList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
