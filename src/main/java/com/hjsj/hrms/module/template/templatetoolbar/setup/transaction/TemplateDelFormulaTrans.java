package com.hjsj.hrms.module.template.templatetoolbar.setup.transaction;

import com.hjsj.hrms.businessobject.general.salarychange.ChangeFormulaBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class TemplateDelFormulaTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		
			ArrayList groupId_array = (ArrayList) this.getFormHM().get("groupId_array");
			
			String tableid = (String)this.getFormHM().get("tableid");
			tableid=tableid!=null&&tableid.trim().length()>0?tableid:"";
			
			ContentDAO dao = new ContentDAO(this.frameconn);
			ChangeFormulaBo changebo = new ChangeFormulaBo();

			for (int i = 0; i < groupId_array.size(); i++) {
				String id=groupId_array.get(i).toString();
				if(id.length()>0&&tableid.length()>0){
					changebo.delTemp(tableid,id,dao);
				}
			}
	}

}
