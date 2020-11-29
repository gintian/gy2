/**
 * 
 */
package com.hjsj.hrms.businessobject.sys.sso;

import com.hrms.frame.utility.AdminDb;

import java.sql.Connection;
import java.util.TimerTask;

/**
 *<p>Title:SyncLogonInfoTimerTask</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-4-13:下午01:55:58</p> 
 *@author cmq
 *@version 4.0
 */
public class SyncLogonInfoTimerTask extends TimerTask {
	/**数据库连接*/
	private Connection conn=null;
	
	@Override
    public void run() {
	 try
	 {
		conn=AdminDb.getConnection();  
		Sso_Sync_UserBo syncbo=new Sso_Sync_UserBo(conn);
		syncbo.sync_logon_user();//建银投资
	 }
	 catch(Exception ex)
	 {
		 ;
	 }
	 finally
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

	public SyncLogonInfoTimerTask() {
		super();
	}

}
