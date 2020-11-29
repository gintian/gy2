package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.sys.dataimport.DataImportBo;
import com.hrms.frame.utility.AdminDb;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;

/**
 * <p>
 * Title:ImportDataJob
 * </p>
 * <p>
 * Description:后台作业中执行的类，将其他系统数据导入到hr系统中
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-12-16
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class ImportDataJob implements Job {

	@Override
    public void execute(JobExecutionContext context)
			throws JobExecutionException {

//		System.out.println("name------" + context.getJobDetail().getName());
//		System.out.println("key-------" + context.getJobDetail().getKey());
		
		// 作业类id
		String jobId = context.getJobDetail().getName();
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			DataImportBo bo = new DataImportBo(conn);
			bo.importData(conn, jobId, "", "","",null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {

	}

}
