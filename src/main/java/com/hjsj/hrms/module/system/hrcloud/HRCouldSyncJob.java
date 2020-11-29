package com.hjsj.hrms.module.system.hrcloud;

import com.hjsj.hrms.module.system.hrcloud.util.SyncDataUtil;
import org.apache.log4j.Category;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class HRCouldSyncJob implements Job {

	private static Category log = Category.getInstance(HRCouldSyncJob.class.getName());
	
	@Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.debug("---HRCouldSyncJob---run--->");
		SyncDataUtil.startSync();
	}

}
