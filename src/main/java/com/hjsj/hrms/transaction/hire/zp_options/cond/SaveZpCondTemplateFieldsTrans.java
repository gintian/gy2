package com.hjsj.hrms.transaction.hire.zp_options.cond;

import com.hjsj.hrms.businessobject.hire.zp_options.ZpCondTemplateXMLBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveZpCondTemplateFieldsTrans extends IBusiness {
	public void execute() throws GeneralException{
		try{
			ArrayList fieldSetList = (ArrayList)this.getFormHM().get("fieldSetList");
			String type=(String)this.getFormHM().get("zp_cond_template_type");
			ZpCondTemplateXMLBo bo = new ZpCondTemplateXMLBo(this.getFrameconn());
			bo.insertParam(fieldSetList,type);
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		
	}

}
