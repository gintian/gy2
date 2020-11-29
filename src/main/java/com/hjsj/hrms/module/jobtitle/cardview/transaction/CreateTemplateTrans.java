package com.hjsj.hrms.module.jobtitle.cardview.transaction;

import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.servlet.http.HttpSession;

/**
 * 创建文件并返回文件名
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class CreateTemplateTrans extends IBusiness {

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		
		String tp_id = (String)this.getFormHM().get("tp_id");
		try{
	       HttpSession session = (HttpSession)this.getFormHM().get("session");
		   String filename=ServletUtilities.createTemplateFile(tp_id,"0",session);
		   filename=PubFunc.encrypt(filename);
		   this.getFormHM().put("templatefile",filename);
		}catch(Exception e)
		{
			e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		}
	}
}
