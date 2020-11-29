/*
 * Created on 2005-4-14
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author qq
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DbManager {
	
	
	/**
	 * 
	 */
	public DbManager() {
		
	}
	public Connection getConnection(int dbserver)
	{
		Connection conn=null;
		try
		{
			switch(dbserver)
			{
			case 2:
				Class.forName("oracle.jdbc.driver.OracleDriver");
				conn=DriverManager.getConnection("jdbc:oracle:thin:@192.192.100.124:1521:ykchr","yksoft","yksoft1919");
				break;
			case 3:
				Class.forName("com.ibm.db2.jcc.DB2Driver");
				conn=DriverManager.getConnection("jdbc:db2://kf:50000/ykchr","db2admin","db2admin");
				break;
			case 1:
				Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
				conn=DriverManager.getConnection("jdbc:microsoft:sqlserver://127.0.0.1:1433;databasename=ykchr","sa","");
				break;
			}
		}
		catch(ClassNotFoundException ee)
		{
			ee.printStackTrace();
		}
		catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		return conn;		
	}
}
