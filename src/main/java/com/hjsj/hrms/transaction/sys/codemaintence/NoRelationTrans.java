package com.hjsj.hrms.transaction.sys.codemaintence;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class NoRelationTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String sqlstr = "select  codesetid,codesetdesc";
		String column = "codesetid,codesetdesc";
		StringBuffer where =new StringBuffer();
		where.append("FROM CodeSet where CodeSetId not in(select CodeSetId from fielditem where codesetid<>'0' and codesetid is not null union select CodeSetId from t_hr_busifield where codesetid<>'0' and codesetid is not null)");
		this.getFormHM().put("sqlstr", sqlstr);
		this.getFormHM().put("column", column);
		this.getFormHM().put("where", where.toString());
	}

}
