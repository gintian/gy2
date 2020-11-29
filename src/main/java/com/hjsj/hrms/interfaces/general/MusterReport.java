/**
 * 
 */
package com.hjsj.hrms.interfaces.general;

import com.lowagie.text.Document;

import java.sql.Connection;
import java.util.ArrayList;
/**
 * <p>
 * Title:MusterReport
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2006-4-29:11:51:07
 * </p>
 * 
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class MusterReport {
	private Connection conn;
	private String sql;
	private ArrayList fieldlist;
	private ReportStyle reportstyle;
	/**每页行数*/
	private int pagerows=20;
	/**最大行数*/
	private int maxrows=0;
	/**pdf文档对象*/
	private Document document;	
	/**
	 * @param conn
	 * @param fieldlist
	 * @param sql
	 */
	public MusterReport(Connection conn, ArrayList fieldlist, String sql,
			ReportStyle reportstyle) {
		super();
		this.conn = conn;
		this.fieldlist = fieldlist;
		this.sql = sql;
		this.reportstyle = reportstyle;
	}

	public void createReportPDF(ReportStyle style) {
		this.reportstyle = reportstyle;
	}

}
