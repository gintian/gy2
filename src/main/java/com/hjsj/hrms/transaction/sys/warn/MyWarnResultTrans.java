package com.hjsj.hrms.transaction.sys.warn;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class MyWarnResultTrans extends IBusiness {

	public MyWarnResultTrans() {
		super();
	}

	public void execute() throws GeneralException {
		ScanTotal st = new ScanTotal( getUserView() );
		ArrayList wlist = st.myWarnResult();
		getFormHM().put("warnList", wlist);

	}

}
