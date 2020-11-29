package com.hjsj.hrms.module.template.templatetoolbar.importmen.transaction;

import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 
* <p>Title: TemplateImprotTrans</p>
* <p>Description: 预警引入模板，同步表结构</p>
* <p>Company: HJSOFT</p> 
* @author dengc
* @date 2016-10-17 下午05:28:10
 */
public class SyncTemplateStrutTrans extends IBusiness  {
	@Override
    public void execute() throws GeneralException {
		try
		{
			String tabid=(String)this.getFormHM().get("tabid"); 
			TemplateBo templateBo=new TemplateBo(this.getFrameconn(),this.userView,Integer.parseInt(tabid));
			templateBo.createTempTemplateTable(this.userView.getUserName());	 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}

	}


}
