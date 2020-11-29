package com.hjsj.hrms.transaction.general.query;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class HandworkSelectInitTrans extends IBusiness {
	
	
	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String managerstr=" ";
		if(userView.getManagePrivCodeValue()!=null&&!"".equals(userView.getManagePrivCodeValue()))
		{
			managerstr=userView.getManagePrivCodeValue();
		}
		String type=(String)hm.get("type");
		String show_dbpre=(String)hm.get("show_dbpre");
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
		String rootdesc=sysoth.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
		rootdesc = rootdesc == null || rootdesc == ""?"组织机构":rootdesc;
		this.getFormHM().put("managerstr",managerstr);
		this.getFormHM().put("infor",type);
		this.getFormHM().put("dbpre_arr",show_dbpre);
		this.getFormHM().put("rootdesc", rootdesc);

	}

}
