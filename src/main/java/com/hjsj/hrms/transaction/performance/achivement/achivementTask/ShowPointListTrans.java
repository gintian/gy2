package com.hjsj.hrms.transaction.performance.achivement.achivementTask;

import com.hjsj.hrms.businessobject.performance.achivement.AchivementTaskBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:获得绩效目标书指标数据列表</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 17, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class ShowPointListTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String target_id=(String)hm.get("target_id");
			AchivementTaskBo bo=new AchivementTaskBo(this.getFrameconn(),this.userView);
			ArrayList pointList=bo.getTargetPointList(target_id);
			
			this.getFormHM().put("pointList",pointList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}

	}

}
