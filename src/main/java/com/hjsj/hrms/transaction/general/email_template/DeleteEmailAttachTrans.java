package com.hjsj.hrms.transaction.general.email_template;

import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class DeleteEmailAttachTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String id=(String)map.get("id");
			String templateId=(String)map.get("templateId");
			EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
			//xus 20/4/29 vfs 改造
			bo.deleteAttach(id,this.getUserView().getUserName());
			this.getFormHM().put("id",templateId);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
