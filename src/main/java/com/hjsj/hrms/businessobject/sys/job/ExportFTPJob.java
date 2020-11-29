package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.sys.ExportXmlBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;
import java.util.Calendar;

public class ExportFTPJob implements Job {

	@Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		Connection conn=null;
		String start_time = Calendar.getInstance().get(Calendar.YEAR)+"-"
		+(Calendar.getInstance().get(Calendar.MONTH)+1)+"-"
		+Calendar.getInstance().get(Calendar.DATE);

		//定义要打包的文件名
		String file = "yksoft_"+start_time+".rar";
		String pathname = System.getProperty("java.io.tmpdir");
		try {
			conn = (Connection) AdminDb.getConnection();
			ExportXmlBo exportxml = new ExportXmlBo(conn,"SYS_EXPORT");
				exportxml.export("true",pathname,file);
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(conn);
		}
	}
}
