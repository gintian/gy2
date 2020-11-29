package com.hjsj.hrms.transaction.performance.commend_table;

import com.hjsj.hrms.businessobject.performance.commend_table.CommendTableBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveNewLeaderTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			
			String record=SafeCode.decode((String)this.getFormHM().get("record"));
			String status = (String)this.getFormHM().get("status");//=0save=1submit
			CommendTableBo bo = new CommendTableBo(this.getFrameconn(),this.getUserView());
			bo.saveNewLeader(record, status);
			this.getFormHM().put("status", status);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
