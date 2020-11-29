package com.hjsj.hrms.transaction.general.muster.hmuster;

import com.hjsj.hrms.businessobject.gz.gz_analyse.GzFormulaXMLBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SetCondTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String tabid = (String)hm.get("tabid");
		tabid=tabid!=null?tabid:"";
		
		GzFormulaXMLBo xmlbo = new GzFormulaXMLBo(this.getFrameconn(),tabid);
		
		this.getFormHM().put("seivelist",xmlbo.getSeiveItem());
		this.getFormHM().put("tabID",tabid);
	}
}
