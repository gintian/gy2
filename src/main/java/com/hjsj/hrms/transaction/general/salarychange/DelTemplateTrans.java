package com.hjsj.hrms.transaction.general.salarychange;

import com.hjsj.hrms.businessobject.general.salarychange.ChangeFormulaBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class DelTemplateTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
			String id = (String)this.getFormHM().get("id");
			id=id!=null&&id.trim().length()>0?id:"";
			
			String tableid = (String)this.getFormHM().get("tableid");
			tableid=tableid!=null&&tableid.trim().length()>0?tableid:"";
			
			ContentDAO dao = new ContentDAO(this.frameconn);
			ChangeFormulaBo changebo = new ChangeFormulaBo();

			if(id.length()>0&&tableid.length()>0){
				changebo.delTemp(tableid,id,dao);
//				this.getFormHM().put("formulatemp",SafeCode.encode(changebo.tableTemp(tableid,dao)));
				this.getFormHM().put("tableid",tableid);
			}
	}

}
