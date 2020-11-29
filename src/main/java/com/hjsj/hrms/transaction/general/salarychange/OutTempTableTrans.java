package com.hjsj.hrms.transaction.general.salarychange;

import com.hjsj.hrms.businessobject.general.salarychange.ChangeFormulaBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class OutTempTableTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String tableid = (String)this.getFormHM().get("tableid");
		tableid=tableid!=null&&tableid.trim().length()>0?tableid:"";
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		ChangeFormulaBo changebo = new ChangeFormulaBo();

		this.getFormHM().put("tableid",tableid);
		this.getFormHM().put("formulatemp",SafeCode.encode(tableid.length()>0?changebo.tableTemp(tableid,dao):""));
	}

}
