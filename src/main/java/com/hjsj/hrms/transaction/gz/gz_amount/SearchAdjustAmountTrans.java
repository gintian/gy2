package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GrossManagBo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchAdjustAmountTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map=(HashMap)this.getFormHM().get("requestPamaHM");
			String ocode=(String)map.get("ocode");
			String year=(String)map.get("oyear");
			String setid=(String)map.get("setid");
			GrossManagBo gmb = new GrossManagBo(this.getFrameconn());
			ArrayList adjustList=gmb.getAdjustAmountList(ocode, setid, year);
			this.getFormHM().put("orgDesc", AdminCode.getCodeName(ocode.substring(0,2), ocode.substring(2)));
			this.getFormHM().put("adjustList", adjustList);
			this.getFormHM().put("tableHeaderList", gmb.getTableHeaderList());
			this.getFormHM().put("yearnum",year);
			this.getFormHM().put("code",ocode);
			this.getFormHM().put("isHasAdjustSet", setid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
