package com.hjsj.hrms.transaction.general.muster.hmuster;

import com.hjsj.hrms.interfaces.general.HmusterXML;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SetSortTrans extends IBusiness {


	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String tabid = (String)this.getFormHM().get("tabid");
		tabid=tabid!=null&&tabid.trim().length()>0?tabid:"0";
		
		HmusterXML hmxml = new HmusterXML(this.getFrameconn(),tabid);
		String sortitem = (String)this.getFormHM().get("sortitem");
		sortitem=sortitem!=null&&sortitem.trim().length()>0?sortitem:"";
		hmxml.setValue(HmusterXML.SORTSTR,sortitem);
		hmxml.saveValue();
	}

}
