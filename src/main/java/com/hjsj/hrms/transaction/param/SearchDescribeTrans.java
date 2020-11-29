package com.hjsj.hrms.transaction.param;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchDescribeTrans extends IBusiness{

	public void execute() throws GeneralException {
		ConstantXml constantXml = new ConstantXml(this.getFrameconn(),"SYS_OTH_PARAM","param");
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		String staff_info=constantXml.getTextValue(Sys_Oth_Parameter.STAFF_INFO);
		staff_info = staff_info == null?"":staff_info;
		if (staff_info.trim().length() <= 0) {
			staff_info=sysbo.getValue(Sys_Oth_Parameter.STAFF_INFO_NUM);
			staff_info = staff_info == null?"":staff_info;
		}
		this.getFormHM().put("staff_info",staff_info);
	}

}
