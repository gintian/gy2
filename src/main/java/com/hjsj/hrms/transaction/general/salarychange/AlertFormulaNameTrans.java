package com.hjsj.hrms.transaction.general.salarychange;

import com.hjsj.hrms.businessobject.general.salarychange.ChangeFormulaBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class AlertFormulaNameTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String tableid = (String)this.getFormHM().get("tableid");
		tableid=tableid!=null&&tableid.trim().length()>0?tableid:"";
		
		String id = (String)this.getFormHM().get("id");
		id=id!=null&&id.trim().length()>0?id:"";
		
		String chz = (String)this.getFormHM().get("chz");
		chz=chz!=null&&chz.trim().length()>0?chz:"";
		
		ChangeFormulaBo formulabo = new ChangeFormulaBo();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		formulabo.alertItemName(dao,tableid,id,chz);
		this.getFormHM().put("info","ok");
	}

}
