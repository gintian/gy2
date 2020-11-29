package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.attestation.bjxh.SyncDB2AD;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class BjxhSyncJob implements Job{

	@Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
		SyncDB2AD sda = new SyncDB2AD();
		sda.execute();
	}

}
