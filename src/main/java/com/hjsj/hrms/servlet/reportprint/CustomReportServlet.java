package com.hjsj.hrms.servlet.reportprint;

import com.hjsj.hrms.businessobject.report.user_defined_reoprt.UserdefinedReport;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
/**
 * 自定义表格输出
 * <p>Title:CustomReportServlet.java</p>
 * <p>Description>:CustomReportServlet.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 11, 2010 10:39:13 AM</p>
 * <p>@version: 4.0</p>
 * <p>@author: s.xin
 */
public class CustomReportServlet  extends HttpServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		String isprivstr=request.getParameter("ispriv");//是否加有权限
		String report_id=request.getParameter("id");//自定义表格id
		UserView userView=(UserView)request.getSession().getAttribute(WebConstant.userView);
		Connection conn=null;
		boolean ispriv=false;
		if(isprivstr!=null&& "1".equals(isprivstr))
			ispriv=true;
		try{
			conn=AdminDb.getConnection();
			UserdefinedReport userdefinedReport=new UserdefinedReport(userView,conn,report_id,ispriv);
			String filename=userdefinedReport.analyseUserdefinedExcelReport();
			File file = new File(System.getProperty("java.io.tmpdir"), filename);
	        if (!file.exists()) {
	            throw new ServletException(
	                "File '" + file.getAbsolutePath() + "' does not exist"
	            );
	        }
	        if("doc".equalsIgnoreCase(file.getName().substring(file.getName().length()-3)))
		    	ServletUtilities.sendInlineOleFile(file,response);
	        else
		    	ServletUtilities.sendTempOleFile(file,response);
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		
		
		 
	}

}
