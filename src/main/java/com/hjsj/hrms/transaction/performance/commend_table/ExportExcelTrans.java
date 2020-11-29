package com.hjsj.hrms.transaction.performance.commend_table;

import com.hjsj.hrms.businessobject.performance.commend_table.CommendTableBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class ExportExcelTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			String ext=(String)this.getFormHM().get("ext");
			String b0110=(String)this.getFormHM().get("b0110");
			CommendTableBo ctb = new CommendTableBo(this.getFrameconn(),this.getUserView(),1);
			//ctb.searchResult(b0110);
			//String fileName=ctb.getExcelFile(b0110);
			String fileName=ctb.getNewLeaderExcel(b0110);
			fileName=fileName.replace(".xls","#");
			this.getFormHM().put("outName",PubFunc.encrypt(fileName));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
