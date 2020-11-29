package com.hjsj.hrms.transaction.param;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveDescribeNewsTrans extends IBusiness{
	public void execute() throws GeneralException {
		String staff_info = (String)this.getFormHM().get("staff_info");
		staff_info = PubFunc.keyWord_reback(staff_info);
		ConstantXml constantXml = new ConstantXml(this.getFrameconn(),"SYS_OTH_PARAM","param");
		constantXml.setTextValue(Sys_Oth_Parameter.STAFF_INFO,staff_info);//明星照片描述
		constantXml.saveStrValue();
	}
}
