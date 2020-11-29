package com.hjsj.hrms.transaction.performance.kh_result;

import com.hjsj.hrms.businessobject.performance.kh_result.ResultBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 *<p>Title:ExecuteExcelTrans.java</p> 
 *<p>Description:生成评测表excel</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 1, 2008</p> 
 *@author JinChunhai
 *@version 4.0
 */

public class ExecuteExcelTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			String distinctionFlag = (String)this.getFormHM().get("distinctionFlag");
			String object_id = PubFunc.decrypt((String)this.getFormHM().get("object_id"));
			String plan_id = (String)this.getFormHM().get("plan_id");
			
			ResultBo bo=new ResultBo(this.getFrameconn(),this.userView);
			String fileName = bo.excecuteExcel(distinctionFlag, object_id, plan_id);
			fileName = PubFunc.encrypt(fileName);
			fileName = SafeCode.encode(fileName);	
			this.getFormHM().put("fileName", fileName);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
