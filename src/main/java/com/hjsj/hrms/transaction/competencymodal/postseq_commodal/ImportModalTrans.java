package com.hjsj.hrms.transaction.competencymodal.postseq_commodal;

import com.hjsj.hrms.businessobject.competencymodal.PostModalBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class ImportModalTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String import_type=(String)this.getFormHM().get("import_type");
			String object_type=(String)this.getFormHM().get("object_type");
			String codesetid=(String)this.getFormHM().get("codesetid");
			String object_id=(String)this.getFormHM().get("object_id");
			PostModalBo pmb = new PostModalBo(this.getFrameconn(),this.userView);
			String historyDate = (String)this.getFormHM().get("historyDate");
			String info = pmb.importPoint(object_id, object_type, Integer.parseInt(import_type), codesetid,historyDate);
			this.getFormHM().put("object_id",object_id);
			this.getFormHM().put("info",info);
			this.getFormHM().put("object_type",object_type);
			this.getFormHM().put("codesetid", codesetid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
