package com.hjsj.hrms.transaction.performance.achivement.achivementTask;

import com.hjsj.hrms.businessobject.performance.achivement.AchivementTaskBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:撤销任务</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 12, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class DelTaskTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String selectedIds=(String)this.getFormHM().get("selectedIds");
			String target_id=(String)this.getFormHM().get("target_id");
			AchivementTaskBo bo=new AchivementTaskBo(this.getFrameconn(),this.userView);
			
			
			bo.delTargetTask(selectedIds,target_id);
			ArrayList pointList=bo.getTargetPointList(target_id);
			String cycle=(String)this.getFormHM().get("cycle");
			ArrayList targetDataList=bo.getTargetDataList(pointList,target_id,"",cycle);
			this.getFormHM().put("selectedPointList",pointList);
			this.getFormHM().put("targetDataList",targetDataList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
