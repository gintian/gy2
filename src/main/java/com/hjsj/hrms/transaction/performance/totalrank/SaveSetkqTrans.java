package com.hjsj.hrms.transaction.performance.totalrank;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveSetkqTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String khtitle = (String)this.getFormHM().get("khtitle");
		khtitle=khtitle!=null?khtitle:"";
		
		String khvalue = (String)this.getFormHM().get("khvalue");
		khvalue=khvalue!=null?khvalue:"";
		
		ConstantXml cx = new ConstantXml(this.getFrameconn(),"ZYXY_PARAM","Params");
		if("kh_set".equalsIgnoreCase(khtitle))
			cx.setTextValue("/Params/kh_set",khvalue);
		else
			cx.setTextValue("/Params/kh_set_look",khvalue);
		cx.saveStrValue();
	}

}
