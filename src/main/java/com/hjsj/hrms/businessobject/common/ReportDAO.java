package com.hjsj.hrms.businessobject.common;

import java.sql.ResultSet;
import java.sql.Statement;

public class ReportDAO {
	
	private Statement stmt = null;
	
	public ReportDAO(Statement stmt) {
		super();
		this.stmt = stmt;
	}
	
	public Statement getStmt() {
		return stmt;
	}

	public void setStmt(Statement stmt) {
		this.stmt = stmt;
	}

	public ResultSet getTsort() {
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
