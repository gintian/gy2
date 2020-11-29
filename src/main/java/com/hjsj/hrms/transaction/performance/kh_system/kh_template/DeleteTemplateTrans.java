package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class DeleteTemplateTrans extends IBusiness{


	public void execute() throws GeneralException {
		try
		{
			String template_id = (String)this.getFormHM().get("templateid");
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn());
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			bo.deleteTemplate(template_id, dao);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
