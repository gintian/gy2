/*
 * Created on 2005-6-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.stat;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchStatViewPhoto extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
	 	// TODO Auto-generated method stub
           //System.out.println("s");
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		String photo_other_view=sysbo.getValue(Sys_Oth_Parameter.PHOTO_OTHER_VIEW);
		if(photo_other_view==null||photo_other_view.length()<=0)
			photo_other_view="";
		this.getFormHM().put("photo_other_view", photo_other_view);
	}

}
