package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class ImportCurrentTemplateTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String templateID = (String)this.getFormHM().get("id");
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn());
			String outName=bo.importCurrentTemplate(templateID);
			outName=SafeCode.encode(PubFunc.encrypt(outName));
			this.getFormHM().put("outName",outName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
