package com.hjsj.hrms.businessobject.sys.sso;


import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class SsoDbConnection {

	public SsoDbConnection()
	{
		
	}
	private static Connection conn=null;
	private static ResultSet rs=null;
	public static void getConnection()
	{
		Connection aconn=null;
		try {
			String className=SystemConfig.getProperty("sso_db_driver");
			if(className!=null) {
                className=className.trim();
            }
			String url=SystemConfig.getProperty("sso_db_url");
			String username=SystemConfig.getProperty("sso_db_user");
			String password=SystemConfig.getProperty("sso_db_pwd"); 
			Class.forName(className);			
			aconn= DriverManager.getConnection(url,username,password);	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		conn=aconn;
	}	
	public static void getStatement()
	{
		getConnection();
		if(conn!=null)
		{
			
		}
	}
	public static ResultSet getResultSet(String sql)
	{
		ResultSet rs=null;
		try
		{
			ContentDAO dao = new ContentDAO(conn);
			rs=dao.search(sql);
//			if(stat!=null)
//				stat.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return rs;
	}
	public static void close()
	{
		try
		{
			
			if(conn!=null) {
                conn.close();
            }
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
