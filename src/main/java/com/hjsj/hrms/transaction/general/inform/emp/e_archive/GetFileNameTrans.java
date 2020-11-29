package com.hjsj.hrms.transaction.general.inform.emp.e_archive;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class GetFileNameTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String filename=(String)map.get("filename");
			String width=(String)map.get("w");
			String height=(String)map.get("h");
			this.getFormHM().put("filename",filename);
			this.getFormHM().put("width",width);
			this.getFormHM().put("height",height);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
