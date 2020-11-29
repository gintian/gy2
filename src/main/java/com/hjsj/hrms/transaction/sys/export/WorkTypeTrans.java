package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.export.ExportSearchSQLStr;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class WorkTypeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		ExportSearchSQLStr sqlstring = new ExportSearchSQLStr();
		String[] sql=sqlstring.sqlStr();
		hm.put("sql",sql[0]);
		hm.put("where",sql[1]);
		hm.put("column",sql[2]);
		hm.put("orderby",sql[3]);
		
	}
}
