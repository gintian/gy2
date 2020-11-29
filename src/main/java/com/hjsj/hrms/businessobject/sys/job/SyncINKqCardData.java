package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.kq.machine.SyncCardData;
import com.hjsj.hrms.businessobject.param.DocumentSyncBo;
import com.hjsj.hrms.businessobject.param.DocumentSyncXML;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
/**
 * 从外部系统同步考勤数据
 * <p>Title:SyncINKqCardData.java</p>
 * <p>Description>:SyncINKqCardData.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Nov 18, 2010 9:15:44 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class SyncINKqCardData implements Job {
	private static Category cat = Category.getInstance("com.hjsj.hrms.businessobject.sys.job.SyncINKqCardData");
	@Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
		Connection conn=null;
		
		try {
			conn = (Connection) AdminDb.getConnection();
			int repeatInterval=this.getTrigger(conn);
			repeatInterval=repeatInterval+30;//向前再加三十分钟
			SyncCardData syncCardData=new SyncCardData(conn);
			System.out.println("考勤同步--"+DateUtils.format(new Date(), "yyyy.MM.dd hh:mm:ss"));
			DocumentSyncBo bo = new DocumentSyncBo(conn);
			DocumentSyncXML xml = new DocumentSyncXML(conn, bo.getConnXML());
			// 获得数据库信息
			List list = xml.getBeanList("/datasources/datasource");
			if(!list.isEmpty())
			{
				for (int i = 0; i < list.size(); i++) {
					LazyDynaBean bean = (LazyDynaBean) list.get(i);
					String status = (String) bean.get("status");
					status = status == null ? "0" : status;
					if("1".equals(status) && syncCardData.isAllowSync(bean)) {
						Date date=new Date();
						String end_date=DateUtils.format(date, "yyyy.MM.dd");
						String _time=DateUtils.format(date, "HH:mm");
						//得到开始时间
						Calendar can = Calendar.getInstance();
						can.add(Calendar.MINUTE, repeatInterval*-1);					
						java.text.SimpleDateFormat sf_date   = new java.text.SimpleDateFormat( "yyyy.MM.dd");
						java.text.SimpleDateFormat sf_time   = new java.text.SimpleDateFormat( "HH:mm");
						String start_date   =sf_date.format(can.getTime()); 
						String _star_time=sf_time.format(can.getTime());
						//Date _start_date=DateUtils.addDays(date, -1);
						//String start_date=DateUtils.format(_start_date, "yyyy.MM.dd");
						syncCardData.sycnCardData(start_date,_star_time,end_date,_time);
					}
				}
			}else
			{
				if(syncCardData.isAllowSync())
				{
					Date date=new Date();
					String end_date=DateUtils.format(date, "yyyy.MM.dd");
					String _time=DateUtils.format(date, "HH:mm");
					Date _start_date=DateUtils.addDays(date, -1);
					String start_date=DateUtils.format(_start_date, "yyyy.MM.dd");
					syncCardData.sycnCardData(start_date,_time,end_date,_time);
				}
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			try
			   {
				
				if(conn!=null) {
					conn.close();
				}
			   }
			   catch(Exception ex)
			   {
				   ex.printStackTrace();
			   }
		}
	}
	public int getTrigger(Connection conn)
	{
		String sql="select job_time from t_sys_jobs where jobclass='com.hjsj.hrms.businessobject.sys.job.SyncINKqCardData'";
		ContentDAO dao=new ContentDAO(conn);
		RowSet rs=null;
		int repeatInterval=60;
		try{
			String jobtime="";
			rs=dao.search(sql);
			if(rs.next())
			{
				jobtime=rs.getString("job_time");
			}
			if(jobtime!=null&&jobtime.length()>0)
			{
				String[] jobtimes=StringUtils.split(jobtime,"|");		
				if(!"".equalsIgnoreCase(jobtimes[3].trim()))
				{
					repeatInterval=Integer.parseInt(jobtimes[3]);		
				}	
			}			
		}catch(Exception e)
		{
			e.printStackTrace();
		}	
		return repeatInterval;
	}
}
