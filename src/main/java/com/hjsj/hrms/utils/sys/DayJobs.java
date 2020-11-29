package com.hjsj.hrms.utils.sys;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class DayJobs implements Job {
    /**
     * 需要被执行的class类路径数组
     * 这些类都需要实现CrontabJob接口
     */
    private static final String[] JOBCLASSARR = {"com.hjsj.hrms.utils.sys.VfsJob"};
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Class jobClass = null;
        CrontabJob job = null;
        try{
            if(JOBCLASSARR!=null &&JOBCLASSARR.length>0){
                //挨个实例化，然后执行默认方法
                for(String classStr: JOBCLASSARR){
                    try {
                        //避免一个后台作业报错，导致其他也不能正常执行，try、catch写在循环里
                        jobClass = Class.forName(classStr);
                        job = (CrontabJob)jobClass.newInstance();
                        job.executeJobs();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
