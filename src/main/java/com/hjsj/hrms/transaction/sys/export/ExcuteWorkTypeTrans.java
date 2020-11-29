package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.sso.ScheduleJobBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.jobs.FileScanJob;
import org.quartz.spi.TriggerFiredBundle;

import java.util.Date;
import java.util.Map;

public class ExcuteWorkTypeTrans extends IBusiness {

	public void execute() throws GeneralException {

		// 获取需要执行的作业id号
		String id = (String) this.getFormHM().get("id");

		// 返回信息
		String returnInfo = "执行成功！";
		try {
			ScheduleJobBo bo = new ScheduleJobBo(this.frameconn);
			Map map = bo.getMapById(id);
			JobDetail jobDetail = new JobDetail(id, map.get("description")
					.toString(), Class.forName(map.get("jobclass").toString()));
			jobDetail.getJobDataMap().put("connection", null);

			TriggerFiredBundle bundle = new TriggerFiredBundle(jobDetail,
					bo.getTriggerById(id), null, true, new Date(), new Date(),
					new Date(), new Date());
			JobExecutionContext context = new JobExecutionContext(null, bundle,
					new FileScanJob());

			Object obj = Class.forName(map.get("jobclass").toString())
					.newInstance();
			Job job = (Job) obj;
			job.execute(context);

		} catch (Exception e) {
			e.printStackTrace();
			String errorMsg=e.getMessage();
	        int index_i=errorMsg.indexOf("description:");
	        if(index_i!=-1)
	        {
		        String   message=errorMsg.substring(index_i+12);	
				returnInfo = "执行失败！失败原因：" + message;
	        }
	        else
	        	returnInfo = "执行失败！失败原因：" + e.getMessage();
		}

		this.getFormHM().put("info", returnInfo);

	}
}
