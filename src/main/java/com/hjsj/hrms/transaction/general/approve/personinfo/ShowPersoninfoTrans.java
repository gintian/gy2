package com.hjsj.hrms.transaction.general.approve.personinfo;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class ShowPersoninfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
//		UserView uv =this.getUserView();
		HashMap hm = this.getFormHM();
//		ArrayList mylist=new ArrayList();
//		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap reqhm = (HashMap) hm.get("requestPamaHM");
		String a0100=(String) reqhm.get("a01001");
		String pdbflag=(String)reqhm.get("pdbflag1");
		reqhm.remove("a01001");
		reqhm.remove("pdbflag1");
		CheckPrivSafeBo checkPrivSafeBo = new CheckPrivSafeBo(this.frameconn,userView);
		pdbflag=checkPrivSafeBo.checkDb(pdbflag);
        a0100=checkPrivSafeBo.checkA0100("", pdbflag, a0100, "");
		hm.put("a0100",a0100);
		hm.put("pdbflag",pdbflag);
	}

}
