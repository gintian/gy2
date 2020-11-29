package com.hjsj.hrms.transaction.general.inform.synthesisbrowse;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.servlet.http.HttpSession;

public class SearchSynthesisDetailTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		 HttpSession session=(HttpSession)this.getFormHM().get("session");
 		 session.setAttribute("changtab_synthesis","browse");
	}

}
