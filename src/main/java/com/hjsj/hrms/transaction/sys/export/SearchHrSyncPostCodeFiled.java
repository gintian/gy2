package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 岗位设置
 * <p>Title:SearchHrSyncPostCodeFiled.java</p>
 * <p>Description>:SearchHrSyncPostCodeFiled.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jun 21, 2010 10:33:32 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class SearchHrSyncPostCodeFiled extends IBusiness {

public void execute() throws GeneralException {
		
		HrSyncBo hsb = new HrSyncBo(this.frameconn);	
		String fields = hsb.getTextValue(hsb.POST_FIELDS);
		fields = hsb.filtration(fields);
		ArrayList itemlist=hsb.getSimpleFields(fields);
		this.getFormHM().put("setlist",itemlist);
		String codefields = hsb.getTextValue(hsb.POST_CODE_FIELDS);
		ArrayList setlist=hsb.getSimpleFields(codefields);
		this.getFormHM().put("itemlist",setlist);
		
	}
}
