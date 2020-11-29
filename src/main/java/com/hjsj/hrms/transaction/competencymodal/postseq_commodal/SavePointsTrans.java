package com.hjsj.hrms.transaction.competencymodal.postseq_commodal;

import com.hjsj.hrms.businessobject.competencymodal.PostModalBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SavePointsTrans extends IBusiness{

	public void execute() throws GeneralException {
		String object_id=(String)this.getFormHM().get("object_id");
		String object_type=(String)this.getFormHM().get("object_type");
		String points=(String)this.getFormHM().get("points");
		PostModalBo pmb = new PostModalBo(this.getFrameconn(),this.getUserView());
		pmb.savePoints(object_id, object_type, points);
		this.getFormHM().put("object_type",object_type);
		this.getFormHM().put("object_id",object_id);
		this.getFormHM().put("codesetid",(String)this.getFormHM().get("codesetid"));
	}

}
