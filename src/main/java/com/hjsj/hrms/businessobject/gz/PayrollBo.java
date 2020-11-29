package com.hjsj.hrms.businessobject.gz;

import java.sql.Connection;

public class PayrollBo {
	private Connection conn=null;
	/**薪资类别号*/
	private int salaryid=-1;
	public PayrollBo(Connection conn, int salaryid){
		this.conn = conn;
		this.salaryid = salaryid;
	}
}
