package com.hjsj.hrms.transaction.performance.achivement.achivementTask;

import com.hjsj.hrms.businessobject.performance.achivement.AchivementTaskBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:保存绩效任务书标准值</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 12, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class SaveTaskDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String target_id=(String)this.getFormHM().get("target_id");
			ArrayList targetDataList=(ArrayList)this.getFormHM().get("targetDataList");
			ArrayList standardlist=(ArrayList)this.getFormHM().get("selectedList");
			AchivementTaskBo bo=new AchivementTaskBo(this.getFrameconn(),this.userView);
			ArrayList pointList=bo.getTargetPointList(target_id);
			if(pointList.size()>0){
				bo.saveTargetData(targetDataList, pointList, target_id);
			}
			this.getFormHM().put("selectedPointList",pointList);
			this.getFormHM().put("targetDataList",targetDataList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
