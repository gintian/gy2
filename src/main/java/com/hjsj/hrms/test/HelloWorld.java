/**
 * 
 */
package com.hjsj.hrms.test;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-5-17:下午03:35:02</p> 
 *@author cmq
 *@version 4.0
 */
public class HelloWorld implements Job {

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		System.out.println("aaaaaaaaaaaaaaaaaaaaa");
//		String jobName = context.getJobDetail().getFullName();
//		Connection conn=null;//(Connection)context.getJobDetail().getJobDataMap().get("connection");
//
//		try
//		{
//			conn = (Connection) AdminDb.getConnection();			
//			if(conn!=null)
//			{
//				ContentDAO dao=new ContentDAO(conn);
//				RowSet rset=dao.search("select username from operuser");
//				while(rset.next())
//				{
//					System.out.println("--->"+rset.getString("username"));
//				}
//			}
//		}
//		catch(Exception ex)
//		{
//			ex.printStackTrace();
//		}
//		finally
//		{
//			try
//			{
//				if(conn!=null)
//					conn.close();
//			}
//			catch(Exception ex)
//			{
//				ex.printStackTrace();
//			}
//		}
//		System.out.println("--->"+jobName);
		
		 

	}
	
	public static void main(String[] args) {
		try {
			new HelloWorld().execute(null);
		} catch (JobExecutionException e) {
			e.printStackTrace();
		}
	}

}
