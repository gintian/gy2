/**
 * 
 */
package com.hjsj.hrms.businessobject.sys.sso;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.Calendar;
import java.util.*;

/**
 *<p>Title:ScheduleJobBo</p> 
 *<p>Description:作业调度</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-5-17:下午02:25:39</p> 
 *@author cmq
 *@version 4.0
 */
public class ScheduleJobBo {
	private Connection conn=null;
	private Scheduler sched=null;
	public ScheduleJobBo(Connection conn) {
		super();
		this.conn = conn;
	}
	
	public Map getMapById(String id) {
		Map map = new HashMap();
		
		 RowSet rset = null;
			try {
				ContentDAO dao=new ContentDAO(this.conn);
				
				StringBuffer buf=new StringBuffer();
				buf.append("select * from t_sys_jobs where status=1 and job_id='"+id+"'");
				rset =dao.search(buf.toString());
				
				if (rset.next()) {
					int trigger_flag=rset.getInt("trigger_flag");
					String  jobname=rset.getString("description");
					String jobclass=rset.getString("jobclass");
					String jobtime=rset.getString("job_time");
					String job_id=rset.getString("job_id");
					
					map.put("job_id", job_id);
					map.put("job_time", jobtime);
					map.put("jobclass", jobclass);
					map.put("description", jobname);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (rset != null) {
						rset.close();
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			return map;
	}
	
	public Trigger getTriggerById(String id) {
		
		 Trigger trigger = null;
		 RowSet rset = null;
		try {
			ContentDAO dao=new ContentDAO(this.conn);
			
			StringBuffer buf=new StringBuffer();
			buf.append("select * from t_sys_jobs where status=1 and job_id='"+id+"'");
			rset =dao.search(buf.toString());
			
			if (rset.next()) {
				int trigger_flag=rset.getInt("trigger_flag");
				String  jobname=rset.getString("description");
				String jobclass=rset.getString("jobclass");
				String jobtime=rset.getString("job_time");
				String job_id=rset.getString("job_id");
				if(jobclass==null|| "".equalsIgnoreCase(jobclass)) {
                    return null;
                }
		        JobDetail job = new JobDetail(job_id,jobname+job_id,Class.forName(jobclass));
		        job.getJobDataMap().put("connection", this.conn);
		        
				/**=0简单触发器,=1复杂触发器*/
				if(trigger_flag==0)
				{
			        trigger =getTrigger(job_id,jobname,jobtime);
				}
				else
				{
			        trigger = new CronTrigger("trigger"+job_id, "group"+job_id, job_id,jobname+job_id, jobtime);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rset != null) {
					rset.close();
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return trigger;
	}
	/**
	 * 求简单触发器
	 * @param jobtime
	 * @return
	 */
	private  SimpleTrigger getTrigger(String job_id,String job_name,String jobtime)
	{
		SimpleTrigger trigger=null;
		if(jobtime==null|| "".equalsIgnoreCase(jobtime))
		{
	        // Trigger the job to run on the next round minute
	        Date runTime = TriggerUtils.getEvenMinuteDate(new Date());	
	        return new SimpleTrigger("trigger"+job_id, "group"+job_id, runTime);
		}
		Date start_date=null;
		Date end_date=null;
		int repeatCount=-1;
		long repeatInterval=10;
		String[] jobtimes=StringUtils.split(jobtime,"|");
		if(!"".equalsIgnoreCase(jobtimes[0].trim())){
			String date1 = jobtimes[0].substring(0,10);
			String date2 = jobtimes[0].substring(10);
			String[] date01 = date1.trim().split("-");
			String[] date02 = date2.trim().split(":");
			Calendar  ca = Calendar.getInstance();
			ca.set(Calendar.YEAR,Integer.parseInt(date01[0]));
			ca.set(Calendar.MONTH,Integer.parseInt(date01[1])-1);
			ca.set(Calendar.DAY_OF_MONTH,Integer.parseInt(date01[2]));
			ca.set(Calendar.HOUR_OF_DAY,Integer.parseInt(date02[0]));
			ca.set(Calendar.MINUTE ,Integer.parseInt(date02[1]));
			ca.set(Calendar.SECOND ,Integer.parseInt(date02[2]));
			//start_date=DateStyle.parseDate(jobtimes[0]);
			start_date = ca.getTime();
		}
		if(!"".equalsIgnoreCase(jobtimes[1].trim())){
			//end_date=DateStyle.parseDate(jobtimes[1]);
			String date1 = jobtimes[1].substring(0,10);
			String date2 = jobtimes[1].substring(10);
			String[] date01 = date1.trim().split("-");
			String[] date02 = date2.trim().split(":");
			Calendar  ca = Calendar.getInstance();
			ca.set(Calendar.YEAR,Integer.parseInt(date01[0]));
			ca.set(Calendar.MONTH,Integer.parseInt(date01[1])-1);
			ca.set(Calendar.DAY_OF_MONTH,Integer.parseInt(date01[2]));
			ca.set(Calendar.HOUR_OF_DAY,Integer.parseInt(date02[0]));
			ca.set(Calendar.MINUTE ,Integer.parseInt(date02[1]));
			ca.set(Calendar.SECOND ,Integer.parseInt(date02[2]));
			end_date = ca.getTime();
		}
		if(!"".equalsIgnoreCase(jobtimes[2].trim())) {
            repeatCount=Integer.parseInt(jobtimes[2]);
        }
		if(!"".equalsIgnoreCase(jobtimes[3].trim())) {
            repeatInterval=Long.parseLong(jobtimes[3])*1000*60;
        }
		trigger=new SimpleTrigger("trigger1_"+job_id, "group1_"+job_id,job_id,job_name+job_id,start_date,end_date,repeatCount,repeatInterval);
		return trigger;
	}
	/***
	 * 任务调度关闭
	 *
	 */
	public void close()
	{
		  try
		  {
			if(sched!=null) {
                sched.shutdown(true);
            }
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
		  }		
	}
	/***
	 * 取得对应的任务
	 * @throws GeneralException
	 */
	public void run() throws GeneralException
	{
		ContentDAO dao=new ContentDAO(this.conn);
		/*是否执行预警和后台作业参数 guodd 2018-01-18*/
		String warn_scan=SystemConfig.getPropertyValue("warn_scan");
		/*warn_scan=false时仍要执行的后台作业id guodd 2018-01-18*/
		String warn_scan_forcejob = SystemConfig.getPropertyValue("warn_scan_forcejob");
		List forceJobs = Arrays.asList(warn_scan_forcejob.split(","));
		try
		{
	        SchedulerFactory sf = new StdSchedulerFactory();
	        sched = sf.getScheduler();		        
			StringBuffer buf=new StringBuffer();
			buf.append("select * from t_sys_jobs where status=1");
			RowSet rset=dao.search(buf.toString());
			int trigger_flag=0;
			String jobname=null;
			String jobclass=null;
			String jobtime=null;
			String job_id=null;
			while(rset.next())
			{
				try 
				{
					trigger_flag=rset.getInt("trigger_flag");
					jobname=rset.getString("description");
					jobclass=rset.getString("jobclass");
					jobtime=rset.getString("job_time");
					job_id=rset.getString("job_id");
					
					/*当设置不执行整体后台做业，但此后台作业在例外参数中时，继续执行此后台作业 guodd 2018-01-18*/
					if("false".equals(warn_scan) && !forceJobs.contains(job_id)) {
						continue;
					}
					
					if(jobclass==null|| "".equalsIgnoreCase(jobclass)) {
                        continue;
                    }
			        JobDetail job = new JobDetail(job_id,jobname+job_id,Class.forName(jobclass));
			        job.getJobDataMap().put("connection", this.conn);
			        sched.addJob(job, true);
					/**=0简单触发器,=1复杂触发器*/
					if(trigger_flag==0)
					{
				        SimpleTrigger trigger =getTrigger(job_id,jobname,jobtime);
				        //sched.scheduleJob(job, trigger);
				        sched.scheduleJob(trigger);
					}
					else
					{
				        CronTrigger trigger = new CronTrigger("trigger"+job_id, "group"+job_id, job_id,jobname+job_id, jobtime);
				        //sched.scheduleJob(job, trigger);
				        sched.scheduleJob(trigger);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Category.getInstance("com.hjsj.hrms.businessobject.sys.sso.ScheduleJobBo").error(e.toString());
		        }
			}//while loop end.
	        // will run until the scheduler has been started
			//云同步  定时任务  start  wangb 2019-05-07
			try {
				String job_time = this.getHRCloudJobTime();//获取云 定时任务 时间规则
				if(job_time.length()>0){//时间规则 有数据 才添加到 定时任务
					JobDetail job = new JobDetail("HrCloud","HrCloud",Class.forName("com.hjsj.hrms.module.system.hrcloud.HRCouldSyncJob"));
			        job.getJobDataMap().put("connection", this.conn);
			        sched.addJob(job, true);
			        CronTrigger trigger = new CronTrigger("triggerHrCloud", "groupHrCloud", "HrCloud","HrCloud", job_time);
			        sched.scheduleJob(trigger);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Category.getInstance("com.hjsj.hrms.businessobject.sys.sso.ScheduleJobBo").error("HRCloud-->"+e.toString());
			}
			
			//云同步  定时任务  start
			
			
	        sched.start();
	        
	            // wait five minutes to show jobs 
	            //Thread.sleep(60L * 1000L); 
	            // executing...
	        

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}
	
	/**
	 * 重置任务（启用或修改执行参数后，重启任务；停用后，停止任务）
	 * @Title: resetJob   
	 * @Description: 重置任务（启用或修改执行参数后，重启任务；停用后，停止任务）   
	 * @param jobId 任务id   jobId 值为 HrCloud 表示云同步
	 * @throws GeneralException
	 */
	public void resetJob(String jobId) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            SchedulerFactory sf = new StdSchedulerFactory();
            sched = sf.getScheduler();  
            if("HrCloud".equalsIgnoreCase(jobId)){//云同步参数更新，定时任务也要更新  wangb 2019-05-07
            	JobDetail job = sched.getJobDetail("HrCloud", "HrCloud");
                //简单处理，不论是停用还是启用或修改job参数，原job都删除
                if (job != null) {
                    sched.pauseTrigger("triggerHrCloud", "groupHrCloud");
                    sched.unscheduleJob("triggerHrCloud", "groupHrCloud");
                    sched.unscheduleJob("trigger1_HrCloud", "group1_HrCloud");
                    sched.deleteJob("HrCloud", "HrCloud");
                }
                String job_time = this.getHRCloudJobTime();
                if(job_time.trim().length() == 0) {
                    return;
                }
                job = new JobDetail("HrCloud","HrCloud",Class.forName("com.hjsj.hrms.module.system.hrcloud.HRCouldSyncJob"));
                job.getJobDataMap().put("connection", this.conn);
                sched.addJob(job, true);
		        CronTrigger trigger = new CronTrigger("triggerHrCloud", "groupHrCloud", "HrCloud","HrCloud", job_time);
		        sched.scheduleJob(trigger);
            	return;
            }
            
            
            StringBuffer buf=new StringBuffer();
            buf.append("select * from t_sys_jobs where job_id=?");
            
            ArrayList params = new ArrayList();
            params.add(jobId);
            
            RowSet rset = dao.search(buf.toString(), params);
            int trigger_flag = 0;
            String jobname = null;
            String jobclass = null;
            String jobtime = null;
            String job_id = null;
            String jobStatus = null;
            String jobGroup = null;
            while(rset.next()) {
                try {
                    trigger_flag = rset.getInt("trigger_flag");
                    jobname = rset.getString("description");
                    jobclass = rset.getString("jobclass");
                    jobtime = rset.getString("job_time");
                    job_id = rset.getString("job_id");
                    jobStatus = rset.getString("status");
                    if(jobclass == null || "".equalsIgnoreCase(jobclass)) {
                        continue;
                    }
                    
                    jobGroup = jobname + job_id;
                    JobDetail job = sched.getJobDetail(job_id, jobGroup);
                    //简单处理，不论是停用还是启用或修改job参数，原job都删除
                    if (job != null) {
                        sched.pauseTrigger("trigger"+job_id, "group"+job_id);
                        boolean isok = sched.unscheduleJob("trigger"+job_id, "group"+job_id);
                        isok = sched.unscheduleJob("trigger1_"+job_id, "group1_"+job_id);
                        isok = sched.deleteJob(job_id, jobGroup);
                    }
                    
                    //停用
                    if(!"1".equals(jobStatus)) {
                        continue;
                    }
                    
                    job = new JobDetail(job_id,jobname+job_id,Class.forName(jobclass));
                    job.getJobDataMap().put("connection", this.conn);
                    sched.addJob(job, true);
                    /**=0简单触发器,=1复杂触发器*/
                    if(trigger_flag==0)
                    {
                        SimpleTrigger trigger = getTrigger(job_id,jobname,jobtime);
                        sched.scheduleJob(trigger);
                    }
                    else
                    {
                        CronTrigger trigger = new CronTrigger("trigger"+job_id, "group"+job_id, job_id,jobname+job_id, jobtime);
                        sched.scheduleJob(trigger);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Category.getInstance("com.hjsj.hrms.businessobject.sys.sso.ScheduleJobBo").error(e.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
	/**
	 * 获取云同步定时任务 时间规则      wangb 2019-06-07
	 */
	private String getHRCloudJobTime(){
		String job_time = "";
		RecordVo hrCloudVo = ConstantParamter.getConstantVo("HRCLOUD_CONFIG");
		if(hrCloudVo != null && hrCloudVo.getString("str_value") != null && hrCloudVo.getString("str_value").trim().length()>0){
			String str_value = hrCloudVo.getString("str_value");
			JSONObject jsonObject = JSONObject.fromObject(str_value);
			String used = jsonObject.getString("used");
			if("1".equalsIgnoreCase(used)){//启用 定时任务 同步
				JSONObject frequencyObject = jsonObject.getJSONObject("frequency");
				//{"time":"14:45","type":"week","week":"1"}
				String type = frequencyObject.getString("type");
				String time = frequencyObject.getString("time");//时间
				
				String minute = time.split(":")[1];
				minute = "00".equalsIgnoreCase(minute)? "0":minute;
				String hour = time.split(":")[0];
				hour = "00".equalsIgnoreCase(hour)? "0":hour;
				if("day".equalsIgnoreCase(type)){//每天
					job_time = "0 "+ minute +" "+ hour  +" * * ? *";
				}else if("week".equalsIgnoreCase(type)){//每周
					String week = frequencyObject.getString("week");
					job_time = "0 "+ minute +" "+ hour  +" ? * "+week+" *";
				}
			}
		}
		return job_time;
		
	}
}
