package com.hjsj.hrms.transaction.performance.implement.performanceImplement;

import com.hjsj.hrms.businessobject.performance.performanceImplement.ExamActualizeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:ImplementWeightInsertTran.java</p>
 * <p>Description:保存设置的主体权重数据</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 4, 2008:11:53:46 AM</p>
 * @author JinChunhai
 * @version 1.0
 */

public class ImplementWeightInsertTran extends IBusiness
{

	public void execute() throws GeneralException 
	{
		// TODO Auto-generated method stub
		try
		{
			String planid=(String)this.getFormHM().get("planid");
			ExamActualizeBo pe=new ExamActualizeBo(this.getFrameconn(),planid);
			ArrayList purviewList=(ArrayList)this.getFormHM().get("purviewList");
			pe.saveWeightValue(purviewList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}

}
