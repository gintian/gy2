package com.hjsj.hrms.transaction.general.salarychange;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class TemplateTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm = (HashMap)this.getFormHM().get("requestPamaHM");
		String tableid = (String)reqhm.get("tableid");
		reqhm.remove("tableid");
		tableid=tableid!=null&&tableid.trim().length()>0?tableid:"";
		String id = (String)reqhm.get("id");
		reqhm.remove("id");
		id=id!=null&&tableid.trim().length()>0?id:"";
		
		this.getFormHM().put("tableid",tableid);
		this.getFormHM().put("id",id);
	}

}
