package com.hjsj.hrms.transaction.performance.evaluation.dealWithBusiness;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/** 
 *<p>Title:AdjustGradeTrans.java</p> 
 *<p>Description:调整等级</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jun 14, 2008</p> 
 *@author JinChunhai
 *@version 4.0
 */

public class AdjustGradeTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			String opt=(String)this.getFormHM().get("opt");   //1:提高等级   2:降低等级
			String object_id=(String)this.getFormHM().get("object_id");
			object_id = PubFunc.decrypt(object_id);
			String planid=(String)this.getFormHM().get("planid");
			String evalRemark = (String)this.getFormHM().get("evalRemark");
			PerEvaluationBo bo=new PerEvaluationBo(this.getFrameconn());
			String info=bo.adjustGrade(opt,object_id,planid);
			evalRemark = SafeCode.decode(evalRemark);
			if(info.indexOf("!")==-1)
			{
				ContentDAO dao = new ContentDAO(this.frameconn);
				String sql = "update PER_RESULT_" + planid +" set evalRemark=? where object_id = ?";
				ArrayList list = new ArrayList();
				list.add(evalRemark);
				list.add(object_id);
				dao.update(sql,list);
			}			
			
			this.getFormHM().put("info",SafeCode.encode(info));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
