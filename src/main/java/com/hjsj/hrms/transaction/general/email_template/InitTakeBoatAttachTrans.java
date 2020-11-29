package com.hjsj.hrms.transaction.general.email_template;

import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class InitTakeBoatAttachTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String templateId = (String)this.getFormHM().get("id");
			EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
			ArrayList attachlist = bo.getAttachList(templateId);
			this.getFormHM().put("attachlist",attachlist);
			this.getFormHM().put("id",templateId);
			this.getFormHM().put("isok","2");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
