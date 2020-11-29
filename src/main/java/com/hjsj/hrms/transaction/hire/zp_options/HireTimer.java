package com.hjsj.hrms.transaction.hire.zp_options;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

/**
 * 每天凌晨定时清空外网登入失败的信息
 * @author akuan
 *
 */
public  class HireTimer implements ServletContextListener{ 

	private Timer timer = null; 

    public void contextDestroyed(ServletContextEvent arg0) { 
        timer.cancel(); 
    } 

    public void contextInitialized(ServletContextEvent arg0) { 
        timer = new Timer(true); 
        //设置任务计划，启动和间隔时间 
        //时间间隔
         long PERIOD_DAY =  24*60*60*1000;
         Calendar calendar = Calendar.getInstance();  
               
         /*** 定制每日00:00:00执行方法 ***/ 
         calendar.set(Calendar.HOUR_OF_DAY, 0);
         calendar.set(Calendar.MINUTE, 0);
         calendar.set(Calendar.SECOND, 0);
         
         Date date=calendar.getTime(); //第一次执行定时任务的时间
         
         //如果第一次执行定时任务的时间 小于 当前的时间
         //此时要在 第一次执行定时任务的时间 加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。
         if (date.before(new Date())) {
             date = this.addDay(date, 1);
         }        
        timer.schedule(new HireApplicationInit(arg0.getServletContext()), date,PERIOD_DAY);

    } 


    // 增加或减少天数
    public Date addDay(Date date, int num) {
     Calendar startDT = Calendar.getInstance();
     startDT.setTime(date);
     startDT.add(Calendar.DAY_OF_MONTH, num);
     return startDT.getTime();
    }


} 



