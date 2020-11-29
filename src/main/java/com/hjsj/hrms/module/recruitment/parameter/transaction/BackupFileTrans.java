package com.hjsj.hrms.module.recruitment.parameter.transaction;

import com.hjsj.hrms.businessobject.hire.ParameterSetBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class BackupFileTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try
		{
			String path=(String)this.getFormHM().get("path");
			ParameterSetBo bo = new ParameterSetBo(this.getFrameconn());
			String filename=bo.inputZip(SafeCode.decode(path), "UserFiles");
			this.getFormHM().put("filename",SafeCode.encode(PubFunc.encrypt(filename+".zip")));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
