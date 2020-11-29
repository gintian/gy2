package com.hjsj.hrms.transaction.performance.achivement.achivementTask;

import com.hjsj.hrms.businessobject.performance.achivement.AchivementTaskBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:保存绩效任务书单一对象标准值</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 4, 2010</p> 
 *@author JinChunhai
 *@version 5.0
 */
public class SaveSingletObjectTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String target_id=(String)this.getFormHM().get("target_id");
			ArrayList objectCycleList=(ArrayList)this.getFormHM().get("objectCycleList");
			AchivementTaskBo bo=new AchivementTaskBo(this.getFrameconn(),this.userView);
			ArrayList pointList=bo.getTargetPointList(target_id);
			if(pointList.size()>0){
				bo.saveTargetData(objectCycleList, pointList, target_id);
			}
			this.getFormHM().put("objectPointList",pointList);
			this.getFormHM().put("objectCycleList",objectCycleList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
