package com.hjsj.hrms.servlet.jp_contest.personinfo;

import com.hjsj.hrms.servlet.PhotoFileDeleter;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminDb;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
/**
 * 
 *<p>Title:ShowStuffInfo.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 24, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class ShowStuffInfo extends HttpServlet {
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String id=req.getParameter("id");
	 	HttpSession session = req.getSession(true);
//	 	String filename="";
	 	ResultSet rs = null;
	 	Connection conn = null;
	 	PreparedStatement pstmt=null;
	 	InputStream in = null;
	 	try
		{
	 		 //File tempFile = null;
	 		 File file=null;
	         StringBuffer strsql = new StringBuffer();
	         strsql.append("select ext,name,content from zp_apply_file ");
	         strsql.append("where fileid ='"+id+"'");
	         conn = AdminDb.getConnection();
	         pstmt=conn.prepareStatement(strsql.toString());
	         String name = "";
	         String ext = "";
	         rs=pstmt.executeQuery();
	         if (rs.next()) {
	             java.io.FileOutputStream fout = null;
	             name = rs.getString("name");
	             ext = rs.getString("ext");
	             name=new String(name.getBytes("gb2312"),"ISO8859_1");
//	             if(name.length()<3)
//	            	 name = "stuff";
//	             tempFile = File.createTempFile(name, ext,new File(System.getProperty("java.io.tmpdir")));
	             in = rs.getBinaryStream("content");   
	             try{	            	 
	            	 fout = new java.io.FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+name+ext);
	            	 
	            	 int len;
	            	 byte buf[] = new byte[1024];
	            	 
	            	 if(in!=null)
	            		 while ((len = in.read(buf, 0, 1024)) != -1) {
	            			 fout.write(buf, 0, len);
	            			 
	            		 }
	             }finally{
	            	 PubFunc.closeIoResource(fout);
	             }
	             file = new File(System.getProperty("java.io.tmpdir"), name+ext);
	             if (session != null) {
	                 registerPhotoForDeletion(file, session);
	            }
	             //filename= tempFile.getName();                
	         }
		 	
		 	   //File file = new File(System.getProperty("java.io.tmpdir"), name+ext);
		       if (!file.exists()) {
		           throw new ServletException(
		               "File '" + file.getAbsolutePath() + "' does not exist"
		           );
		       }
		       /**显示对象文件*/
		    ServletUtilities.sendTempOleFile(file,resp);
			}catch(Exception e)
			{
		 		e.printStackTrace();
		 	} 
			finally {
				PubFunc.closeIoResource(in);
				PubFunc.closeResource(rs);
				PubFunc.closeResource(pstmt);
				PubFunc.closeResource(conn);
			}
	  }
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
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
