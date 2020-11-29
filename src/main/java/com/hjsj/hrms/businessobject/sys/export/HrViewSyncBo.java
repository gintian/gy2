package com.hjsj.hrms.businessobject.sys.export;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;
/**
 * 
 *<p>Title:HrViewSyncBo.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 15, 2008</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class HrViewSyncBo implements Job {

	@Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
		Connection conn=null;
		try {
			conn = (Connection) AdminDb.getConnection();
			HrSyncBo hsb = new HrSyncBo(conn);
			hsb.creatHrTable("t_hr_view");
			String dbnamestr = hsb.getTextValue(hsb.BASE);
			String[] fields;
			String fieldstr=hsb.getTextValue(hsb.FIELDS);	
			hsb.HrSync(dbnamestr,fieldstr);
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(conn);
		}
		
	}

}
