package com.hjsj.hrms.module.recruitment.parameter.transaction;

import com.hjsj.hrms.module.recruitment.parameter.businessobject.ZpCondTemplateXMLBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class DeleteComplexTemplateTrans extends IBusiness{

	@Override
    public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String ids=(String)map.get("id");
			String zp_cond_template_type=(String)this.getFormHM().get("zp_cond_template_type");
			ZpCondTemplateXMLBo bo=new ZpCondTemplateXMLBo(this.getFrameconn());
			bo.deleteComplexTemplate(ids);
			this.getFormHM().put("zp_cond_template_type",zp_cond_template_type);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
