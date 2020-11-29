package com.hjsj.hrms.transaction.general.deci.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.LeadarParamXML;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveLoadtypeTrans extends IBusiness {
	public void execute() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
        String id = (String)this.getFormHM().get("id");
        String loadname = "";
        if("0".equalsIgnoreCase(id))
        	loadname="默认结构";
        if("1".equalsIgnoreCase(id))
        	loadname="显示到部门";
        if("2".equalsIgnoreCase(id))
        	loadname="显示到集团";
        LeadarParamXML leadarParamXML=new LeadarParamXML(this.getFrameconn());
        leadarParamXML.setTextValue(leadarParamXML.LOADTYPE,id);
		leadarParamXML.saveParameter();
		this.getFormHM().put("mess",loadname);
	}
}
