package com.hjsj.hrms.transaction.competencymodal.postseq_commodal;

import com.hjsj.hrms.businessobject.competencymodal.PostModalBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SavePostModalTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String point_id=(String)this.getFormHM().get("pointCode");
			String object_type=(String)this.getFormHM().get("object_type");
			String object_id=(String)this.getFormHM().get("codeitemid");
			ArrayList list =(ArrayList)this.getFormHM().get("editPostModalList");
			PostModalBo pmb = new PostModalBo(this.getFrameconn(),this.getUserView());
			pmb.savePostModal(list, object_id, object_type, point_id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
