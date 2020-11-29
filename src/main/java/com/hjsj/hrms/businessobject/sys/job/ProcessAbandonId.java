/**
 * 处理长时间未使用的工号
 */
package com.hjsj.hrms.businessobject.sys.job;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

/**
 * <p>Title: ProcessAbandonId </p>
 * <p>Description:处理长时间未使用的工号</p>
 * <p>Company: hjsj</p>
 * <p>create time  2013-11-28 上午10:04:31</p>
 * @author pjf
 * @version 1.0
 */
public class ProcessAbandonId implements Job {

	@Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		Connection conn=null;
		ResultSet rs = null;
		try {
			// 作业类id
			String jobId = context.getJobDetail().getName();
			conn=AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search("select job_param from t_sys_jobs where job_id="+ jobId);
			String param = "2";//默认是2个月,避免用户没有设置
			if (rs.next()) {
				param = rs.getString("job_param");  //获取参数
			}
			StringBuffer buf=new StringBuffer();
			Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
			int year = c.get(Calendar.YEAR); 
			int month = c.get(Calendar.MONTH)+1; 
			int day = c.get(Calendar.DATE); 
			if(month<=Integer.parseInt(param)){ //用当前时间减去2(param)个月
				year = year-1;
				month = month+12-Integer.parseInt(param);
			} else {
				month = month-Integer.parseInt(param);
			}
			buf.append(" update staff_id_pool set Is_used=0,create_time=null where Is_used=2 and ( ");
		    buf.append(Sql_switcher.year("create_time")+ "<"+ year +" or ");
		    buf.append("("+Sql_switcher.year("create_time")+ "="+ year+" and ");
		    buf.append(Sql_switcher.month("create_time")+ "<"+ month +") or ");
		    buf.append("("+Sql_switcher.year("create_time")+ "="+ year+" and ");
		    buf.append(Sql_switcher.month("create_time")+ "="+ month +" and ");
		    buf.append(Sql_switcher.day("create_time")+ "<="+ day +") )");
		    dao.update(buf.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(rs!=null) {
					rs.close();
				}
				if(conn!=null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
