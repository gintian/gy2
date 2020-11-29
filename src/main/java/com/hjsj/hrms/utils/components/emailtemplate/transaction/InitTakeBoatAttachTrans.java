package com.hjsj.hrms.utils.components.emailtemplate.transaction;

import com.hjsj.hrms.utils.components.emailtemplate.businessobject.TemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:InitTakeBoatAttachTrans</p>
 * <p>Description:插入附件的列表展示</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 3, 2015 2:05:08 PM</p>
 * @author sunming
 * @version 1.0
 */
public class InitTakeBoatAttachTrans extends IBusiness {
	  public void execute() throws GeneralException {
		  HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		  	String templateId = (String)this.getFormHM().get("templateId");
	        try {

				TemplateBo tempBo = new TemplateBo(this.frameconn, new ContentDAO(this.frameconn), this.getUserView());
				ArrayList attachlist = tempBo.getAttachList(templateId);
				boolean existPath = VfsService.existPath();
				this.getFormHM().put("attachList",attachlist);
				this.getFormHM().put("id",templateId);
				this.getFormHM().put("isok","2");
				this.getFormHM().put("rootPath",existPath);
	        } catch (Exception e) {
	            e.printStackTrace();
	            throw GeneralExceptionHandler.Handle(e);
	        }
	    }

}
