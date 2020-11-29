package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SendEmailBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DeleteEmailTemplateTrans extends IBusiness{

	public void execute() throws GeneralException 
	{
		try
		{
			String id=(String)this.getFormHM().get("id");
			SendEmailBo bo = new SendEmailBo(this.getFrameconn());
			bo.deleteTemplate(id,"email_name");
			bo.deleteTemplate(id,"email_field");
			bo.deleteTemplate(id,"email_content");
			bo.deleteTemplate(id,"email_attach");
			ArrayList list=bo.getEmailTemplateList();
			this.getFormHM().put("templateList",list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		
	}

}
