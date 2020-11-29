/**
 * 
 */
package com.hjsj.hrms.servlet.sys;

import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
/**
 * <p>Title:DownloadLnlp</p>
 * <p>Description:动态生成jnlp协议文件</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-12-27:15:21:47</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class DownloadJnlp extends HttpServlet {

	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		doPost(arg0, arg1);
	}

	protected void doPost(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
	     String protocol = arg0.getScheme();    
	     String ip = arg0.getServerName();    
	     int port = arg0.getServerPort(); 
	     String app_flag=arg0.getParameter("app");
	     String ctrl_flag=arg0.getParameter("ctrl");
	     if(app_flag==null|| "".equals(app_flag))
	    	 app_flag="1";
	     String app = arg0.getContextPath();
	     StringBuffer  codebase=new StringBuffer();
	     codebase.append(protocol);
	     codebase.append("://");
	     codebase.append(ip);
	     codebase.append(":");
	     codebase.append(port);
	     codebase.append("/");
	     codebase.append(app);
	     codebase.append("cs_deploy");
	     UserView userview=(UserView)arg0.getSession().getAttribute(WebConstant.userView);
	     EncryptLockClient lockclient=(EncryptLockClient)arg0.getSession().getServletContext().getAttribute("lock");
	     String license=lockclient.getLicenseCount();
	     if(license==null|| "0".equals(license)|| "".equals(license))
	    	 ctrl_flag="99";
	     else
	    	 ctrl_flag="98";	 

	 //   if(userview!=null)
	 //   {
		     CreateJnlpXml jnlpxml=new CreateJnlpXml(userview.getUserId(),userview.getPassWord(),app_flag,ctrl_flag);
		     arg1.setContentType("application/x-java-jnlp-file");    
		     PrintWriter out = arg1.getWriter();    	     
		     out.println(jnlpxml.outPutJnlpXml(codebase.toString(),lockclient.getWboc()));
		     out.flush();
		     out.close();
	 //   }
	}
}
