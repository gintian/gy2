/*
 * Created on 2005-8-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.servlet.media;

import com.hjsj.hrms.servlet.ServletUtilities;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;



/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ShowMediaInfo extends HttpServlet {
	 protected void doPost(HttpServletRequest req, HttpServletResponse resp)
       throws ServletException, IOException {
	 	String usertable=req.getParameter("usertable");
	 	String usernumber=req.getParameter("usernumber");
	 	String i9999=req.getParameter("i9999");
	 	String fileid = "";
	 	try{
			fileid = ServletUtilities.getFileId(usertable,usernumber,i9999);
			if(StringUtils.isBlank(fileid)){
				throw new ServletException("文件不存在！");
			}else{
				resp.sendRedirect("/servlet/vfsservlet?fileid=" + fileid);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	  }
	  protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
        throws ServletException, IOException {
          doPost(arg0, arg1);
      }
}
