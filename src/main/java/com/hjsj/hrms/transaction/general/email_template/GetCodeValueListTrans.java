package com.hjsj.hrms.transaction.general.email_template;

import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class GetCodeValueListTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String itemid=(String)this.getFormHM().get("itemid");
			EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
			ArrayList list = bo.getCodeList(itemid);
			HashMap map = bo.getItemInfo(itemid);
			String itemtype = (String)map.get("itemtype");
			String codesetid=(String)map.get("codesetid");
			this.getFormHM().put("codesetid",codesetid);
			this.getFormHM().put("itemtype",itemtype);
			this.getFormHM().put("codelist",list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
