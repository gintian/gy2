package com.hjsj.hrms.servlet.module.jobtitle.qualification;

import com.hjsj.hrms.utils.PubFunc;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class AttachmentDownLoad extends HttpServlet{

	/**
	 * Constructor of the object.
	 */
	public AttachmentDownLoad() {
		super();
	}

	 public void service(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
	 {
		 InputStream inputStream = null;
		 OutputStream outputStream = null;
	  try
	  {
	   String downFilename=request.getParameter("filename");
	   String filepath=request.getParameter("path");
	         response.setContentType("text/plain");
	         response.setHeader("Location",downFilename);
	         response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(downFilename, "UTF-8"));
	         outputStream = response.getOutputStream();
	         inputStream = new FileInputStream(filepath+downFilename);
	         byte[] buffer = new byte[1024];
	         int i = -1;
	         while ((i = inputStream.read(buffer)) != -1) {
	          outputStream.write(buffer, 0, i);
	         }
	         outputStream.flush();
	         outputStream.close();
	  }catch(FileNotFoundException e1)
	  {
	   System.out.println("");
	  }
	  catch(Exception e)
	  {
	   System.out.println("系统错误，请及时与管理员联系");
	  }finally {
		PubFunc.closeResource(outputStream);
		PubFunc.closeResource(inputStream);
	}
	     }
}
