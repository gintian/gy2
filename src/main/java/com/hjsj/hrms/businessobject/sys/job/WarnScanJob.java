package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.transaction.sys.warn.ScanTrans;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 作业类预警
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jun 18, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class WarnScanJob implements Job {

	private Logger log = LoggerFactory.getLogger(WarnScanJob.class);
	@Override
    public void execute(JobExecutionContext context) throws JobExecutionException
	{
		log.info("[预警提示后台作业]任务开始");
		long start = System.currentTimeMillis();
		ScanTrans.setFrequency(true);
		ScanTrans.doScan(10);
		log.info("[预警提示后台作业]任务结束===[consume time is {} ms]===", (System.currentTimeMillis() - start));
	}

}
