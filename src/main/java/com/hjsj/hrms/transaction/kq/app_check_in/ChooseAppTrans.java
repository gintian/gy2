package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class ChooseAppTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		 ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("infolist");
		 HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		 if(selectedinfolist==null||selectedinfolist.size()<0)
			 return;
		 String table=(String)this.getFormHM().get("table");
		 String audit_flag=(String)hm.get("audit_flag");
		 this.getFormHM().put("audit_flag",audit_flag);
		 this.getFormHM().put("selectedinfolist",selectedinfolist);
		 this.getFormHM().put("table",table);

	}

}
