package com.hjsj.hrms.utils.components.emailtemplate.transaction;

import com.hjsj.hrms.utils.components.emailtemplate.businessobject.TemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:DeleteEmailAttachTrans</p>
 * <p>Description:删除附件</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 3, 2015 2:03:49 PM</p>
 * @author sunming
 * @version 1.0
 */
public class DeleteEmailAttachTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String id=(String)this.getFormHM().get("id");
			String templateId=(String)this.getFormHM().get("templateId");
			TemplateBo tempBo = new TemplateBo(this.frameconn, new ContentDAO(this.frameconn), this.getUserView());
			tempBo.deleteAttach(id);
			this.getFormHM().put("id",templateId);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
