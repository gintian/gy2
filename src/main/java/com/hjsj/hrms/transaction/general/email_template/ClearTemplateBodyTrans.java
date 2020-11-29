package com.hjsj.hrms.transaction.general.email_template;

import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class ClearTemplateBodyTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String id = (String)this.getFormHM().get("id");
			EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
			bo.clearTemplateBody(id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
