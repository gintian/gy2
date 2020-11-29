package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GrossManagBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class EditAdjustAmountTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)map.get("optType");
			String ocode=(String)map.get("ocode");
			String year=(String)map.get("oyear");
			String setid=(String)map.get("setid");
			String i9999=(String)map.get("i9999");
			GrossManagBo gmb = new GrossManagBo(this.getFrameconn());
			ArrayList fieldList = gmb.getAdjustRecord(opt, year, ocode, i9999, setid);
			this.getFormHM().put("fieldList", fieldList);
			this.getFormHM().put("yearnum",year);
			this.getFormHM().put("code",ocode);
			this.getFormHM().put("isHasAdjustSet", setid);
			this.getFormHM().put("optType", opt);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
