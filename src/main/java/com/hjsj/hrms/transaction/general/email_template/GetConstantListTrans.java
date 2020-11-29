package com.hjsj.hrms.transaction.general.email_template;

import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
//0202030027
public class GetConstantListTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
			ArrayList list = bo.getConstantList();
			this.getFormHM().put("constantList",list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
