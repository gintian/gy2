package com.hjsj.hrms.module.template.signature.transaction;

import com.hjsj.hrms.module.template.signature.businessobject.SignatureBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveToHtmlSignatureTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		String signaturehtmlid = (String) this.getFormHM().get("signaturehtmlid");
		String documentid = (String) this.getFormHM().get("documentid");
		String markpath = (String) this.getFormHM().get("markpath");
		String tabid = (String) this.getFormHM().get("tabid");
		String signatureid = (String) this.getFormHM().get("signatureid");
		SignatureBo bo = new SignatureBo(this.frameconn,this.userView);
		try {
			documentid = tabid+"_"+documentid;
			bo.saveToHtmlSignature(documentid,signaturehtmlid,markpath,signatureid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
