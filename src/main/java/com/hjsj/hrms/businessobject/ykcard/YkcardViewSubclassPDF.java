package com.hjsj.hrms.businessobject.ykcard;

import java.sql.Connection;

public class YkcardViewSubclassPDF {

	private Connection conn;
	public YkcardViewSubclassPDF()
	{
		
	}
    public YkcardViewSubclassPDF(Connection conn)
    {
    	this.conn=conn;
    }
}
