package com.hjsj.hrms.transaction.general.email_template;

import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * <p>Title:DeleteTemplateTrans.java</p>
 * <p>Description:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-10-16 下午01:48:44</p>
 * @author LiZhenWei
 * @version 4.0
 */

public class DeleteTemplateTrans extends IBusiness{
	public void execute() throws GeneralException 
	{
		try
		{
			String id=(String)this.getFormHM().get("templateId");
			EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
			bo.deleteTemplate(id,"email_name");
			bo.deleteTemplate(id,"email_field");
			bo.deleteTemplate(id,"email_content");
			bo.deleteTemplate(id,"email_attach");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		
	}

}
