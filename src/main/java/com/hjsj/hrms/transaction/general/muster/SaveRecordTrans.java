package com.hjsj.hrms.transaction.general.muster;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 0521010021
 * <p>Title:SaveRecordTrans.java</p>
 * <p>Description>:SaveRecordTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 6, 2010 7:34:29 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class SaveRecordTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			ArrayList list=(ArrayList)this.getFormHM().get("muster_set_record");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.updateValueObject(list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
