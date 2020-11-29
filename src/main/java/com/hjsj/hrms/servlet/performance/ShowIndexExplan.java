package com.hjsj.hrms.servlet.performance;

import com.hjsj.hrms.businessobject.performance.markStatus.MarkStatusBo;
import com.hrms.frame.utility.AdminDb;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;

public class ShowIndexExplan extends HttpServlet {
	
	public ShowIndexExplan() {
		super();
	}

	public void destroy() {
		super.destroy();
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		OutputStream out = response.getOutputStream();
		String plan_id = request.getParameter("plan_id");		
		Connection con =null;
		try {
			con = (Connection) AdminDb.getConnection();	
			MarkStatusBo markStatusBo = new MarkStatusBo(con);
			String  ext_name=markStatusBo.getIndexExplanExt(plan_id);
			response.setContentType(getContentType(ext_name.substring(1)));
			markStatusBo.showIndexExplain(plan_id,out);

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

	
	
	public String getContentType(String ext)
	{
		String MimeType="";
		if("doc".equalsIgnoreCase(ext))
    		MimeType="application/msword";
    	else if("bmp".equalsIgnoreCase(ext))
    		MimeType="image/bmp";
    	else if("xls".equalsIgnoreCase(ext))
    		MimeType="application/vnd.ms-excel";   	   	
    	else if("ppt".equalsIgnoreCase(ext))
    		MimeType="application/vnd.ms-powerpoint";   	    
    	else if("pdf".equalsIgnoreCase(ext))
    		MimeType="application/pdf";
    	else if("vcd".equalsIgnoreCase(ext))
    		MimeType="application/x-cdlink";
    	else if("zip".equalsIgnoreCase(ext))
    		MimeType="application/zip";
    	else if("rar".equalsIgnoreCase(ext))
    		MimeType="application/zip";   
    	else if("gif".equalsIgnoreCase(ext))
    		MimeType="image/gif";
    	else if("jpeg".equalsIgnoreCase(ext))
    		MimeType="image/jpeg";
    	else if("jpg".equalsIgnoreCase(ext))
    		MimeType="image/jpeg";
    	else if("jpe".equalsIgnoreCase(ext))
    		MimeType="image/jpeg";    	
    	else if("bmp".equalsIgnoreCase(ext))
    		MimeType="image/x-MS-bmp";
    	else if("htm".equalsIgnoreCase(ext))
    		MimeType="text/html";
    	else if("txt".equalsIgnoreCase(ext))
    		MimeType="text/plain";
    	else if("html".equalsIgnoreCase(ext))
    		MimeType="text/html";   	
    	else if("uri".equalsIgnoreCase(ext))
    		MimeType="text/uri-list";
    	else if("wav".equalsIgnoreCase(ext))
    		MimeType="audio/x-wav";  
    	else if("docx".equalsIgnoreCase(ext))
    		MimeType="application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    	else if("pptx".equalsIgnoreCase(ext))
    		MimeType="application/vnd.openxmlformats-officedocument.presentationml.presentation";
    	else if("xlsx".equalsIgnoreCase(ext))
    		MimeType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";		
    	else
    		MimeType="multipart/form-data";
        return MimeType;
		
	}
	
	
	
	
	
	
	
	
	public void init() throws ServletException {

	}

}
