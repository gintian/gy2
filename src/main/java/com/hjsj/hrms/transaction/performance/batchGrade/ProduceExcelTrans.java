package com.hjsj.hrms.transaction.performance.batchGrade;

import com.hjsj.hrms.businessobject.performance.batchGrade.ExcelBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:ProduceExcelTrans.java</p>
 * <p>Description>:多人考评导出Excel</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 03, 2010 09:15:57 AM</p>
 * <p>@author: JinChunhai
 * <p>@version: 5.0</p>
 */

public class ProduceExcelTrans extends IBusiness 
{
	
	public void execute() throws GeneralException 
	{
		// TODO Auto-generated method stub
		
		try
		{
			String planid=(String)this.getFormHM().get("planid");
			CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
			boolean _flag = _bo.isPlanIdPriv(planid);
			if(!_flag){
				return;
			}
			ExcelBo bo=new ExcelBo(this.getFrameconn(),planid,this.userView);
			String fileName=bo.getExcelFileName();
			fileName = PubFunc.encrypt(fileName);
			//20/3/6 xus vfs改造
//			fileName = SafeCode.encode(fileName);
			this.getFormHM().put("fileName",fileName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}