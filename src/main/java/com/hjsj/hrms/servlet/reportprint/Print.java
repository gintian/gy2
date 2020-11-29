package com.hjsj.hrms.servlet.reportprint;

import com.hjsj.hrms.businessobject.report.ReportPrint;
import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;

public class Print extends HttpServlet {

	public Print() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		OutputStream out = response.getOutputStream();
		ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();
		//表号
		String tabid = request.getParameter("tabid");
		Connection con = null;
		try {
			UserView userview = (UserView)request.getSession().getAttribute(WebConstant.userView);
			String username = request.getParameter("username");
			String operateObject = request.getParameter("operateObject");
			String unitcode = "";
			TnameBo bo = null;
			con = (Connection) AdminDb.getConnection();
			if ("1".equals(operateObject.trim())) {
				bo = new TnameBo(con, tabid, "", username, " ");
			} else {

				unitcode = request.getParameter("unitcode");
				bo = new TnameBo(tabid, unitcode, username, "", con);
			}
			bo.setUserview(userview);
			response.setContentType("application/pdf");
			response.setHeader("content-disposition", "attachment;filename= Print.pdf");//xiegh 20170628 bug:28453  问题：导出的pdf没有.pdf后缀
			ReportPrint report_print = new ReportPrint(bo, con, operateObject);
			report_print.setUnitcode(unitcode);
			report_print.createPDF(arrayOut);
			report_print.getDocument().close();
			response.setContentLength(arrayOut.size());
			//System.out.println("---" + arrayOut.size());
			arrayOut.writeTo(out);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if (con != null){
					con.close();
				}
			}catch (SQLException sql){
				sql.printStackTrace();
			}
		}
	}

	public void init() throws ServletException {

	}

}
