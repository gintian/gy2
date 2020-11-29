package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveItemRankTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			ArrayList itemList = (ArrayList)this.getFormHM().get("itemList");
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn());
			bo.saveItemRank(itemList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
