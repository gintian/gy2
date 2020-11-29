package com.hjsj.hrms.businessobject.attestation.zjz;

import com.hrms.struts.constant.SystemConfig;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionFromOA {
	public Connection getConnection()
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
}
