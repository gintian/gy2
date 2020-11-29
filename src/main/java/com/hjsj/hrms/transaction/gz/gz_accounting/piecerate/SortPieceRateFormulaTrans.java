package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hjsj.hrms.businessobject.gz.piecerate.PieceRateFormulaBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SortPieceRateFormulaTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		try{	
			HashMap hm=this.getFormHM();
			HashMap reqhm=(HashMap) hm.get("requestPamaHM");
			String busiid = (String)reqhm.get("busiid");
			busiid=busiid!=null&&busiid.trim().length()>0?busiid:"";
			reqhm.remove("salaryid");
	
			PieceRateFormulaBo formulabo = new PieceRateFormulaBo(this.getFrameconn(),"",this.userView);
			hm.put("sortlist",formulabo.sortList(this.frameconn,busiid));
			
			hm.put("busiid",busiid);
		}catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}



}
