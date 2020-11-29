package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 *<p>Title:SearchHrSyncCodeFiled.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Mar 9, 2009:10:40:13 AM</p> 
 *@author huaitao
 *@version 1.0
 */
public class SearchHrSyncCodeFiled extends IBusiness {

	public void execute() throws GeneralException {
		
		HrSyncBo hsb = new HrSyncBo(this.frameconn);	
		String fields = hsb.getTextValue(hsb.FIELDS);
		fields = hsb.filtration(fields);
		ArrayList itemlist=hsb.getSimpleFields(fields);
		this.getFormHM().put("setlist",itemlist);
		String codefields = hsb.getTextValue(hsb.CODE_FIELDS);
		ArrayList setlist=hsb.getSimpleFields(codefields);
		this.getFormHM().put("itemlist",setlist);
		
	}

}
