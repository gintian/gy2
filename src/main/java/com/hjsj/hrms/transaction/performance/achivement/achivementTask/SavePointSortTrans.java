package com.hjsj.hrms.transaction.performance.achivement.achivementTask;

import com.hjsj.hrms.businessobject.performance.achivement.AchivementTaskBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:保存指标排序</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 17, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class SavePointSortTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String target_id=(String)this.getFormHM().get("target_id");
			String[] right_fields=(String[])this.getFormHM().get("right_fields");
			AchivementTaskBo bo=new AchivementTaskBo(this.getFrameconn(),this.userView);
			if(right_fields!=null)
				bo.savePointSort(right_fields,target_id);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
