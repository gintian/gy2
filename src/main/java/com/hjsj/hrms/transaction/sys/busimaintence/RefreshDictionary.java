package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class RefreshDictionary extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		DataDictionary.refresh();
	}

}
