package com.hjsj.hrms.transaction.general.muster;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SaveDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("muster_set_table");
		cat.debug("table name="+name);
		ArrayList list=(ArrayList)hm.get("muster_set_record");

		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			dao.updateValueObject(list);
		}catch(Exception ex){
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
