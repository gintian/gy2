package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.sys.DeleteFile;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.File;
import java.sql.Connection;

/**
 * 清空临时文件夹 temp
 * 
 * <p>Title: DeleteDempFileJob </p>
 * <p>Company: hjsj</p>
 * <p>create time  2014-9-15 上午09:39:56</p>
 * @author guoby
 * @version 1.0
 */
public class DeleteDempFileJob implements Job {
	
	@Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

		Connection conn = null;
		try {
			String filePath = System.getProperty("java.io.tmpdir");
			
			if ("weblogic".equalsIgnoreCase(SystemConfig.getPropertyValue("webserver"))) {
				int index = filePath.indexOf('~');
				if (index != -1) {
					filePath = System.getProperty("user.home")+ filePath.substring(index + 2);
				}
			}
			DeleteFile deleteFile = new DeleteFile();
			deleteFile.deleteDirs(new File(filePath));
			if ("oracle".equalsIgnoreCase(SystemConfig.getPropertyValue("dbserver"))) {
				conn = AdminDb.getConnection();
				ContentDAO dao = new ContentDAO(conn);
				dao.update("Purge recyclebin");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(conn);
		}
	}

}
