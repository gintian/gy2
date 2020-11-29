package com.hjsj.hrms.transaction.gz.templateset.standard;

import com.hjsj.hrms.businessobject.gz.templateset.SalaryStandardBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:SaveSelectUnitTrans.java</p>
 * <p>Description>:SaveSelectUnitTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jul 16, 2011  2:13:38 PM </p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class SaveSelectUnitTrans extends IBusiness{
	
	public void execute() throws GeneralException {
		try
		{
			String id=(String)this.getFormHM().get("id");
			String pkg_id=(String)this.getFormHM().get("pkg_id");
			String content = SafeCode.decode((String)this.getFormHM().get("content"));
			SalaryStandardBo bo = new SalaryStandardBo(this.getFrameconn());
			bo.updateGsUnit(id, content,pkg_id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

	
}
