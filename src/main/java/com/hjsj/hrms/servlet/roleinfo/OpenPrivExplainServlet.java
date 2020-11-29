package com.hjsj.hrms.servlet.roleinfo;

import com.hjsj.hrms.servlet.ServletUtilities;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;



public class OpenPrivExplainServlet extends HttpServlet {
	 protected void doPost(HttpServletRequest req, HttpServletResponse resp)
       throws ServletException, IOException {
	 	String usertable=req.getParameter("usertable");
	 	String usernumber=req.getParameter("usernumber");
	 	String i9999=req.getParameter("i9999");
	 	HttpSession session = req.getSession(true);
	 	String filename="";
	 	try
		{
	 		if("k00".equalsIgnoreCase(usertable))
	 			filename=ServletUtilities.createKOleFile(usertable,usernumber,i9999,session);
	 		else if(usertable.length()>3&& "a00".equalsIgnoreCase(usertable.substring(usertable.length()-3)))
	 			filename=ServletUtilities.createOleFile(usertable,usernumber,i9999,session);
 	 	    File file = new File(System.getProperty("java.io.tmpdir"), filename);
	        if (!file.exists()) {
	            throw new ServletException(
	                "File '" + file.getAbsolutePath() + "' does not exist"
	            );
	    }
	    if("doc".equalsIgnoreCase(file.getName().substring(file.getName().length()-3)))
	    	ServletUtilities.sendInlineOleFile(file,resp);
	    else
	    	ServletUtilities.sendTempOleFile(file,resp);
		}catch(Exception e)
		{
	 		e.printStackTrace();
	 	}	
	  }
	  protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
        throws ServletException, IOException {
          doPost(arg0, arg1);
      }
}
