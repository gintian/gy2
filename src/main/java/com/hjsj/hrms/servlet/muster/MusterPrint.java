package com.hjsj.hrms.servlet.muster;


import com.hjsj.hrms.businessobject.general.muster.ExecuteExcel;
import com.hrms.frame.utility.AdminDb;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;

public class MusterPrint extends HttpServlet {

	public MusterPrint() {
		super();
	}

	public void destroy() {
		super.destroy();
	}
	
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		OutputStream out = response.getOutputStream();
		String mustername=request.getParameter("mustername");
		Connection con=null;
		try {			
			con = (Connection) AdminDb.getConnection();
			response.setContentType("application/vnd.ms-excel");
			ExecuteExcel executeExcel=new ExecuteExcel(con);
			executeExcel.createExcel(out,mustername);
					
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(con!=null)
					con.close();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
	}

	public void init() throws ServletException {

	}


	
}
