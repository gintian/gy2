/*
 * Created on 2005-8-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.servlet.duty;

import com.hjsj.hrms.servlet.PhotoFileDeleter;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 * 
 *<p>Title:ShowMediaInfo.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 7, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class ShowMediaInfo extends HttpServlet {
	 protected void doPost(HttpServletRequest req, HttpServletResponse resp)
       throws ServletException, IOException {
	 	//String usertable=req.getParameter("usertable");
	 	String usernumber=req.getParameter("usernumber");
	 	usernumber=PubFunc.decrypt(usernumber);
	 	String i9999=req.getParameter("i9999");
	 	String kind= req.getParameter("kind");
	 	HttpSession session = req.getSession(true);
	 	String filename="";
	 	 ResultSet rs = null;
         Connection conn = null;
         InputStream in = null;
         FileOutputStream fout = null;
	 	try
		{
	 		 //tempFile = File.createTempFile(ServletUtilities.tempFilePrefix, rs.getString("ext"),
             //        new File(System.getProperty("java.io.tmpdir")));
	 		//filename=ServletUtilities.createOleFile(usertable,usernumber,i9999,session);
	 		 File tempFile = null;
	         //String filename="";
	         ServletUtilities.createTempDir();
	        
             StringBuffer strsql = new StringBuffer();
             strsql.append("select ext,Ole,title from ");
             if("0".equalsIgnoreCase(kind)){
	             strsql.append("k00");
	             strsql.append(" where e01a1='");
	             strsql.append(usernumber);
             }
             else if("2".equalsIgnoreCase(kind)){//if(kind.equalsIgnoreCase("1")){
            	 strsql.append("b00");
	             strsql.append(" where b0110='");
	             strsql.append(usernumber);
             }else {
            	 strsql.append("h00");
	             strsql.append(" where h0100='");
	             strsql.append(usernumber);
             }
             strsql.append("' and i9999= '");
             strsql.append(i9999+"'");
             
             conn = AdminDb.getConnection();
             ContentDAO dao = new ContentDAO(conn);
             
             rs = dao.search(strsql.toString());
             if (rs.next()) {
                 tempFile = File.createTempFile(ServletUtilities.tempFilePrefix, rs.getString("ext"),
                         new File(System.getProperty("java.io.tmpdir")));
                 in = rs.getBinaryStream("Ole");                
                 fout = new java.io.FileOutputStream(tempFile);
                 
                 int len;
                 byte buf[] = new byte[1024];
             
                 while ((len = in.read(buf, 0, 1024)) != -1) {
                     fout.write(buf, 0, len);
                
                 }
                 fout.close();
              
                 if (session != null) {
                     registerPhotoForDeletion(tempFile, session);
                 }
                 //System.out.println("fieldname " + userNumber);
                 filename= tempFile.getName();                
             }
	 		
 	 	    File file = new File(System.getProperty("java.io.tmpdir"), filename);
             if(StringUtils.isNotBlank(rs.getString("title"))){
				 File newfile = new File(System.getProperty("java.io.tmpdir"),rs.getString("title")+rs.getString("ext"));
				 if(newfile.exists()){
				 	newfile.delete();
				 }
				 file.renameTo(newfile);
				 file=newfile;
			 }
	        if (!file.exists()) {
	            throw new ServletException(
	                "File '" + file.getAbsolutePath() + "' does not exist"
	            );
	        }
	        /**显示对象文件*/
	        //resp.sendRedirect("/servlet/DisplayOleContent?filename="+filename);
	    ServletUtilities.sendTempOleFile(file,resp);
		}catch(Exception e)
		{
	 		e.printStackTrace();
	 	} finally {
	 		PubFunc.closeIoResource(fout);
        	PubFunc.closeIoResource(in);
        	PubFunc.closeResource(rs);
        	PubFunc.closeResource(conn);
        }
	  }
	  protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
        throws ServletException, IOException {
          doPost(arg0, arg1);
      }
	  private  void registerPhotoForDeletion(File tempFile,
	            HttpSession session) {
	        //  Add chart to deletion list in session
	        if (session != null) 
	        {
	            PhotoFileDeleter photoDeleter = (PhotoFileDeleter) session
	                    .getAttribute("Ole_Deleter");
	            if (photoDeleter == null) 
	            {
	                photoDeleter = new PhotoFileDeleter();
	                session.setAttribute("Ole_Deleter", photoDeleter);
	            }
	            photoDeleter.addTempFile(tempFile.getName());
	        } 
	        else 
	        {
	            System.out.println("Session is null - photo will not be deleted");
	        }
	    }
}
