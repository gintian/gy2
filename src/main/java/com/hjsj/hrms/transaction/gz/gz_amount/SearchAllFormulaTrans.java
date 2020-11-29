package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GrossManagBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SearchAllFormulaTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String setname=(String)map.get("setid");
			String year = (String)map.get("year");
			GrossManagBo gross = new GrossManagBo(this.getFrameconn(),this.getUserView());
			HashMap hm = gross.getAllFormulaInfor(setname);
			String tableStr=(String)hm.get("1");
			String sortStr=(String)hm.get("2");
			this.getFormHM().put("tableStr", tableStr);
			this.getFormHM().put("sortStr", sortStr);
			this.getFormHM().put("unit_type", "3");
			this.getFormHM().put("year", year);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
