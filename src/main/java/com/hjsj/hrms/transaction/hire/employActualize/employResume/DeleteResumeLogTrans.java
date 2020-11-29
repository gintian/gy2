package com.hjsj.hrms.transaction.hire.employActualize.employResume;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.io.File;

public class DeleteResumeLogTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
			String filePath = System.getProperty("java.io.tmpdir")+"\\ResumeImportLog.txt";
			File file = new File(filePath);
			file.delete();
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		
	}
}
