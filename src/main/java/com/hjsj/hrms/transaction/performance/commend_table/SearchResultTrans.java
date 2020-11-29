package com.hjsj.hrms.transaction.performance.commend_table;

import com.hjsj.hrms.businessobject.performance.commend_table.CommendTableBo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchResultTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			HashMap map=(HashMap)this.getFormHM().get("requestPamaHM");
			String acode=(String)map.get("a_code");
			CommendTableBo ctb = new CommendTableBo(this.getFrameconn(),this.getUserView(),1);
			ArrayList list =ctb.getNewLeaderResult(acode.substring(2));
			this.getFormHM().put("oneList", list);
			this.getFormHM().put("unit", AdminCode.getCodeName("UN",acode.substring(2)));
			this.getFormHM().put("unitCode", acode.substring(2));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
