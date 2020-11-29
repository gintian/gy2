package com.hjsj.hrms.transaction.performance.implement.dataGather;

import com.hjsj.hrms.businessobject.performance.singleGradeBo_new;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 *<p>Title:ExcecuteExcelTrans.java</p> 
 *<p>Description:导出Excel</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jun 20, 2008</p> 
 *@author JinChunhai
 *@version 4.0
 */

public class ExcecuteExcelTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			
			String planid=(String)this.getFormHM().get("planid");
			String    mainbody_id=PubFunc.decrypt((String)this.getFormHM().get("mainbody_id"));
			String object_id=PubFunc.decrypt((String)this.getFormHM().get("object_id"));
			singleGradeBo_new bo=new singleGradeBo_new(this.getFrameconn(),planid,this.getUserView());
			if(planid!=null&&planid.length()>0)
			{
				
				String fileName=bo.getDataGatherExcel(object_id,mainbody_id);
				fileName = PubFunc.encrypt(fileName);
				fileName = SafeCode.encode(fileName);	
				this.getFormHM().put("filename",fileName);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
