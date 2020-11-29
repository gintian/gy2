package com.hjsj.hrms.transaction.hire.parameterSet.configureParameter;

import com.hjsj.hrms.businessobject.hire.ParameterSetBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class BackupFileTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String path=(String)this.getFormHM().get("path");
			ParameterSetBo bo = new ParameterSetBo(this.getFrameconn());
			String filename=bo.inputZip(SafeCode.decode(path), "UserFiles");
			//xus 20/4/29 vfs 改造
//			this.getFormHM().put("filename",SafeCode.encode(PubFunc.encrypt(filename+".zip")));
			this.getFormHM().put("filename",PubFunc.encrypt(filename+".zip"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
