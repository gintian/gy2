package com.hjsj.hrms.transaction.general.inform.emp.e_archive;

import com.hjsj.hrms.businessobject.general.inform.e_archive.E_ArchiveBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteLogOutTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			E_ArchiveBo bo = new E_ArchiveBo(this.getFrameconn());
			int logid=bo.getMaxid();
			if(logid==0)
				return;
			String logouttime=format.format(new Date());
			bo.updateLog(logid, logouttime);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
		
	}

}
