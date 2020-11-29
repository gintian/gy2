package com.hjsj.hrms.transaction.general.approve.personinfo;

import com.hjsj.hrms.businessobject.general.approve.personinfo.BackMessage;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowBackMessageTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm = (HashMap) hm.get("requestPamaHM");
		UserView uv=this.getUserView();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String a0100=(String) reqhm.get("a01001");
		String pdbflag=(String) reqhm.get("pdbflag1");
		BackMessage bm=new BackMessage();
		ArrayList mylist=bm.getMessage(uv,a0100,pdbflag,dao);
		hm.put("bcmessage",mylist);
	}

}
