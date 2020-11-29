package com.hjsj.hrms.transaction.train.resource;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class CheckUpLoadTrans extends IBusiness {
	public void execute() throws GeneralException {
		String check = "no";
		try{
			ConstantXml constant = new ConstantXml(this.frameconn, "TR_PARAM");
			String diyType = constant.getNodeAttributeValue(
				"/param/diy_course", "codeitemid"); //获取课程分类
			if(null != diyType && !"".equals(diyType.trim())){
				check = "yes";
			}
			this.getFormHM().put("check", check);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
