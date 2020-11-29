package com.hjsj.hrms.transaction.performance.achivement.achivementTask;

import com.hjsj.hrms.businessobject.performance.achivement.AchivementTaskBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:ShowAchivementTaskTrans.java</p> 
 *<p>Description:业绩任务书</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 9, 2008</p> 
 *@author JinChunhai
 *@version 4.0
 */

public class ShowAchivementTaskTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		try
		{
			
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String target_id=(String)hm.get("target_id");
			String queryDesc=(String)hm.get("b_search");
			AchivementTaskBo bo=new AchivementTaskBo(this.getFrameconn(),this.userView);
			String sql_whl=(String)this.getFormHM().get("sql_whl");
			String cycle="-1";
			if("query1".equals(queryDesc))
				cycle=(String)this.getFormHM().get("cycle");
			ArrayList pointList=bo.getTargetPointList(target_id);
			ArrayList targetDataList=bo.getTargetDataList(pointList,target_id,sql_whl,cycle);
			RecordVo perTargetVo=bo.getPerTargetVo(target_id);
			ArrayList cycleList=bo.getCycleList(perTargetVo.getInt("cycle"));			
			String targetDataListSql = bo.getTargetDataListSql(pointList,target_id,sql_whl,cycle); // 取得查询目标任务书数据的Sql语句
			this.getFormHM().put("cycle",cycle);
			this.getFormHM().put("cycleList",cycleList);
			this.getFormHM().put("perTargetVo",perTargetVo);
			this.getFormHM().put("selectedPointList",pointList);
			this.getFormHM().put("target_id", target_id);
			this.getFormHM().put("targetDataList",targetDataList);
			this.getFormHM().put("targetDataListSql",targetDataListSql);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
