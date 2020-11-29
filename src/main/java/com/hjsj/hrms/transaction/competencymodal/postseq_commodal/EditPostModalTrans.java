package com.hjsj.hrms.transaction.competencymodal.postseq_commodal;

import com.hjsj.hrms.businessobject.competencymodal.PostModalBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class EditPostModalTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String codeitemid=(String)map.get("codeitemid");
			String object_type=(String)map.get("object_type");
			String point_id=(String)map.get("point_id");
			String codesetid=(String)map.get("codesetid");
			PostModalBo pmb = new PostModalBo(this.getFrameconn(),this.getUserView());
			ArrayList editPostModalList=pmb.getPostModalInfo(object_type, codeitemid, point_id, codesetid);
			this.getFormHM().put("codesetid", codesetid);
			this.getFormHM().put("codeitemid",codeitemid);
			this.getFormHM().put("object_type",object_type);
			this.getFormHM().put("editPostModalList", editPostModalList);
			this.getFormHM().put("pointCode", point_id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
