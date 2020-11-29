package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SearchHrSyncFiled extends IBusiness {

	public void execute() throws GeneralException {
		
		HrSyncBo hsb = new HrSyncBo(this.frameconn);
		String fields = hsb.getTextValue(hsb.FIELDS);
		ArrayList itemlist=hsb.getSimpleFields(fields);
		this.getFormHM().put("itemlist",itemlist);
	}
}
