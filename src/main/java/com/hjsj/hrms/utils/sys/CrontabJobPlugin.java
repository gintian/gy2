package com.hjsj.hrms.utils.sys;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CrontabJobPlugin implements PlugIn {
    private Scheduler sched = null;
    @Override
    public void destroy() {

    }
    @Override
    public void init(ActionServlet actionServlet, ModuleConfig moduleConfig) throws ServletException {
        try{
            SchedulerFactory sf = new StdSchedulerFactory();
            sched = sf.getScheduler();
            //job集合，可以添加按不同时间执行的job，例如24小时执行一次的job，12小时执行一次的job
            List<JobDetail>  jobDetailList = new ArrayList<JobDetail>();
            List<SimpleTrigger>  triggerList = new ArrayList<SimpleTrigger>();
            //每日0点开始，每隔24小时执行一次的job
            JobDetail job = new JobDetail("1000","DayJobs1000",Class.forName("com.hjsj.hrms.utils.sys.DayJobs"));
            //TODO 目前为了质保部测试，修改成了30分钟执行一次
            String job_time  = "2020-03-11 00:00:00| |-1|30";
            //定义执行时间规则
            SimpleTrigger trigger = getTrigger("1000","DayJobs",job_time);
            //如果以后要添加其他job，例如每隔1小时执行一次的job，加到集合里即可
            jobDetailList.add(job);
            triggerList.add(trigger);
            for(int x=0;x<jobDetailList.size();x++){
                sched.addJob(jobDetailList.get(x), true);
                sched.scheduleJob(triggerList.get(x));
            }
            //job.getJobDataMap().put("connection", this.conn);
            sched.start();
        }catch(Exception e ){
            e.printStackTrace();
        }finally {

        }
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
            Date runTime = TriggerUtils.getEvenMinuteDate(new Date());
            return new SimpleTrigger("trigger"+job_id, "group"+job_id, runTime);
        }
        Date start_date=null;
        Date end_date=null;
        int repeatCount=-1;
        long repeatInterval=10;
        String[] jobtimes= StringUtils.split(jobtime,"|");
        if(!"".equalsIgnoreCase(jobtimes[0].trim())){
            String date1 = jobtimes[0].substring(0,10);
            String date2 = jobtimes[0].substring(10);
            String[] date01 = date1.trim().split("-");
            String[] date02 = date2.trim().split(":");
            Calendar ca = Calendar.getInstance();
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
            Calendar ca = Calendar.getInstance();
            ca.set(Calendar.YEAR,Integer.parseInt(date01[0]));
            ca.set(Calendar.MONTH,Integer.parseInt(date01[1])-1);
            ca.set(Calendar.DAY_OF_MONTH,Integer.parseInt(date01[2]));
            ca.set(Calendar.HOUR_OF_DAY,Integer.parseInt(date02[0]));
            ca.set(Calendar.MINUTE ,Integer.parseInt(date02[1]));
            ca.set(Calendar.SECOND ,Integer.parseInt(date02[2]));
            end_date = ca.getTime();
        }
        if(!"".equalsIgnoreCase(jobtimes[2].trim()))
            repeatCount=Integer.parseInt(jobtimes[2]);
        if(!"".equalsIgnoreCase(jobtimes[3].trim()))
            repeatInterval=Long.parseLong(jobtimes[3])*1000*60;
        trigger=new SimpleTrigger("trigger1"+job_id, "group1"+job_id,job_id,job_name+job_id,start_date,end_date,repeatCount,repeatInterval);
        return trigger;
    }
}
