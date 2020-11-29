package com.hjsj.hrms.transaction.performance.achivement.achivementTask;

import com.hjsj.hrms.businessobject.performance.achivement.dataCollection.GetdataTrans;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:ExportExcelTrans.java</p>
 * <p>Description:下载模板</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-11-10 13:00:00</p>
 * @author JinChunhai
 * @version 5.0
 */

public class DonloadTempTrans extends IBusiness
{
	public void execute()throws GeneralException
	{
		try
		{
			String targetid=(String)this.getFormHM().get("target_id");
			String cycle=(String)this.getFormHM().get("cycle");
			String sql_whl2=(String)this.getFormHM().get("sql_whl2");
			GetdataTrans gt=new GetdataTrans(this.getFrameconn(),this.userView,targetid);
			String outname=gt.outexcel(sql_whl2, cycle);
			
			this.getFormHM().put("outName", outname);
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	} 	
}
