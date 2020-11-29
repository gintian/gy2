package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hjsj.hrms.businessobject.gz.piecerate.PieceRateFormulaBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class PieceRateFormulaCondTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		
		String formulaid = (String)reqhm.get("formulaid");
		formulaid=formulaid!=null&&formulaid.length()>0?formulaid:"";
		reqhm.remove("formulaid");		
	
		PieceRateFormulaBo formulabo = new PieceRateFormulaBo(this.getFrameconn(),formulaid,this.userView);	
		
		hm.put("itemid","");
		hm.put("itemlist",formulabo.conditionsList(this.frameconn,"S05"));
		hm.put("conditions",formulabo.getCond());
	}

}
