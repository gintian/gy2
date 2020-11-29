package com.hjsj.hrms.businessobject.sys.sso;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 水利厅同步
 *  
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 31, 2008</p> 
 *@author sxin
 *@version 5.0
 */
public class SyncShuiLiTPlugin implements Job {    
	@Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
		Connection sync_conn=null;
		Connection hr_conn=null;
		try
		{
			if(is_SsoSync())
			{
				if(sync_conn==null) {
					sync_conn=getConnection();
				}
				if(sync_conn!=null)
				{
					System.out.println("取得链接成功");
					hr_conn=AdminDb.getConnection(); 
					Sync_ShuiLT_UserBo sync_ShuiLT_UserBo=new Sync_ShuiLT_UserBo(sync_conn);
					System.out.println("查找新增人");
					ArrayList inser_list=sync_ShuiLT_UserBo.syncInsert(hr_conn);
					if(inser_list!=null&&inser_list.size()>0) {
						System.out.println("有新增人");
					} else {
						System.out.println("没有增人");
					}
					if(syncInsertHrOperUser(hr_conn,inser_list))
					{
						//System.out.println("新增人成功");
						sync_ShuiLT_UserBo.signSync(sync_ShuiLT_UserBo.getSync_insert_list());//标记新增人员
					}
					ArrayList del_list=sync_ShuiLT_UserBo.syncDelete();
					if(syncDelHrOperUser(hr_conn,del_list))
					{
						//System.out.println("有减少人");
						sync_ShuiLT_UserBo.signSync(sync_ShuiLT_UserBo.getSync_del_list());//标记删除人员
					}
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try {
				if(sync_conn!=null) {
					sync_conn.close();
				}
				if(hr_conn!=null) {
					hr_conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 是否同步
	 * @return
	 */
	private boolean is_SsoSync()
	{
	  boolean bsync=false;
	  try
	  {
		//System.out.println("开始调用");
		String sso_sync=SystemConfig.getProperty("sso_sync");
		sso_sync=sso_sync!=null?sso_sync:"";
		if("true".equalsIgnoreCase(sso_sync)) {
			bsync=true;
		}
//		bsync=Boolean.parseBoolean(sso_sync);
	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
	  }
	  return bsync;
	}
	/**
	 * 得到数据库链接
	 * @return
	 */
	private Connection getConnection()
	{
		Connection conn=null;
		try
		{
			String url=SystemConfig.getProperty("sso_db_url");
			String user_id=SystemConfig.getProperty("sso_db_user");
			String pwd=SystemConfig.getProperty("sso_db_pwd");
			Class.forName(SystemConfig.getProperty("sso_db_driver"));
			conn=DriverManager.getConnection(url,user_id,pwd);
		}
		catch(ClassNotFoundException ee)
		{
			ee.printStackTrace();
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
		return conn;		
	}
	/**
	 * 同步数据插入operuser表中（增加）
	 * @param conn
	 * @param list
	 */
	private boolean syncInsertHrOperUser(Connection conn,ArrayList list)
	{
		StringBuffer insert=new StringBuffer();
		insert.append("insert into OperUser ");
		insert.append("(UserName,GroupID,RoleID,UserFlag)");
		insert.append("values(?,?,0,12)");
		ContentDAO dao=new ContentDAO(conn);
		String sql="";
		RowSet rs=null;
		boolean isCorrect=true;
		ArrayList newlist=new ArrayList();
		try
		{
			if(list==null||list.size()<=0) {
				return false;
			}
			for(int i=0;i<list.size();i++)
			{
				ArrayList ol=(ArrayList)list.get(i);
				String username=(String)ol.get(0);
				sql="select 1 from operuser where UserName='"+username+"'";
				System.out.println(sql);
				rs=dao.search(sql);
				if(!rs.next()) {
					newlist.add(ol);
				}
			}
			dao.batchInsert(insert.toString(), newlist);
		}catch(Exception e)
		{
			e.printStackTrace();
			isCorrect=false;
		}
		return isCorrect;
	}
	/**
	 * 同步数据插入operuser表中(删除)
	 * @param conn
	 * @param list
	 * @return
	 */
	private boolean syncDelHrOperUser(Connection conn,ArrayList list)
	{
		StringBuffer delsql=new StringBuffer();
		delsql.append("delete from operuser where username=?");
		ContentDAO dao=new ContentDAO(conn);
		boolean isCorrect=true;
		try
		{
			dao.batchUpdate(delsql.toString(), list);
		}catch(Exception e)
		{
           e.printStackTrace();	
           isCorrect=false;
		}
		return isCorrect;
	}
}
