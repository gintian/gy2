package com.hjsj.hrms.transaction.performance.batchGrade;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeSinglePointBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 *<p>Title:SavePointValueTrans.java</p> 
 *<p>Description:单指标多人考评 保存或提交</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-06-09 10:15:35</p> 
 *@author Administrator
 *@version 5.0
 */

public class SavePointValueTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			String opt=(String)this.getFormHM().get("opt"); // 1：保存下一题 2：提交 3保存
			String obj_values=(String)this.getFormHM().get("obj_values");
			String plan_id=(String)this.getFormHM().get("plan_id");
			String totalNumber=(String)this.getFormHM().get("totalNumber");
			String point_index=(String)this.getFormHM().get("point_index");
			String point_id=(String)this.getFormHM().get("point_id");
			BatchGradeSinglePointBo bo=new BatchGradeSinglePointBo(this.getFrameconn(),plan_id,this.userView);
			
			int _totalNumber=Integer.parseInt(totalNumber);
			int _point_index=Integer.parseInt(point_index);
			if("1".equals(opt))
			{
				if(_point_index<(_totalNumber-1))
					_point_index++;
				this.getFormHM().put("point_index", String.valueOf(_point_index));
			}
			this.getFormHM().put("opt",opt);
			if("3".equals(opt))
				opt="1";
			bo.saveScore(opt,obj_values,point_id);
						
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
