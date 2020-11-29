package com.hjsj.hrms.transaction.sys.options.param;

import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveTaxis extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
        /*改为ajax请求后，参数格式发生变化 原来参数是Array，旧的ajax不支持传array数据，故修改为String*/
		String tagorder = (String)this.getFormHM().get("tagorder");
		String[] tagorderArray = tagorder.split(",");
		SaveInfo_paramXml infoxml = new SaveInfo_paramXml(this.getFrameconn());
		infoxml.saveInfo_paramNode("order",tagorderArray,this.getFrameconn());
	}
}
