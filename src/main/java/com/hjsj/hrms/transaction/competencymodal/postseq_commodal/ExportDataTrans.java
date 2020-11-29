package com.hjsj.hrms.transaction.competencymodal.postseq_commodal;

import com.hjsj.hrms.businessobject.competencymodal.PostModalBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 90100170039
 * <p>Title:ExportDataTrans.java</p>
 * <p>Description>:ExportDataTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 21, 2011  10:50:54 AM </p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class ExportDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String object_type=(String)this.getFormHM().get("object_type");
			String object_id=(String)this.getFormHM().get("object_id");
			String codesetid=(String)this.getFormHM().get("codesetid");
			String historyDate=(String)this.getFormHM().get("historyDate");
			PostModalBo pmb = new PostModalBo(this.getFrameconn(),this.userView);
			String fileName=pmb.exportData(object_type, object_id, codesetid,historyDate);
			fileName = PubFunc.encrypt(fileName);
			fileName = SafeCode.encode(fileName);	
			this.getFormHM().put("fileName", fileName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
