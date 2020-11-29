package com.hjsj.hrms.transaction.org.autostatic;

import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveViewScanTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String msg="ok";
		try{
			String flag = (String)this.getFormHM().get("flag");
			String view_scan=(String)this.getFormHM().get("view_scan");
			PosparameXML pos = new PosparameXML(this.frameconn); 
			pos.setAttributeValue("/params/view_scan", flag, view_scan);
			pos.saveParameter();
		}catch(Exception e){
			msg="error";
			e.printStackTrace();
		}finally{
			this.getFormHM().put("msg", msg);
		}
	}

}
