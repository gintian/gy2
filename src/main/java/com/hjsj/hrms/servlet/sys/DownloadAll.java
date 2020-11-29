package com.hjsj.hrms.servlet.sys;

import com.hjsj.hrms.servlet.PhotoFileDeleter;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
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
import java.sql.ResultSet;
/**
 * 
 *<p>Title:DownloadAll.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 31, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class DownloadAll extends HttpServlet {
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String id=req.getParameter("id");//要查询的id号，PK值
		String fileid = req.getParameter("fileid");//在表中的ID字段名称
		String tablename = req.getParameter("tablename");//表名
		String filecolumn = req.getParameter("filenamecolumn");//文件名字段名称
		String extcolumn  = req.getParameter("ext");//ext的字段名称
		String content = req.getParameter("content");//存储文件内容字段名称
	 	HttpSession session = req.getSession(true);
	 	ResultSet rs = null;
	 	Connection conn = null;
	 	InputStream in=null;
	 	java.io.FileOutputStream fout = null;
	 	try
		{
	 		 //File tempFile = null;
	 		 File file=null;
	         StringBuffer strsql = new StringBuffer();
	         strsql.append("select "+extcolumn+","+filecolumn+","+content+" from "+tablename+" ");
	         strsql.append("where "+fileid+" ='"+id+"'");
	         conn = AdminDb.getConnection();
	         ContentDAO dao  = new ContentDAO(conn);
	         rs=dao.search(strsql.toString());
	         String name = "";
	         String ext = "";
	         if (rs.next()) {
	             name = rs.getString(filecolumn);
	             ext = rs.getString(extcolumn);
	             name=new String(name.getBytes("gb2312"),"ISO8859_1");
	             in = rs.getBinaryStream(content);                
	             fout = new java.io.FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+name+"."+ext);
	             
	             int len;
	             byte buf[] = new byte[1024];
	         
	             if(in!=null)
	             while ((len = in.read(buf, 0, 1024)) != -1) {
	                 fout.write(buf, 0, len);
	            
	             }
	             fout.close();
	             file = new File(System.getProperty("java.io.tmpdir"), name+"."+ext);
	             if (session != null) {
	                 registerPhotoForDeletion(file, session);
	            }
	         }
		 	
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
				PubFunc.closeResource(rs);
				PubFunc.closeResource(conn);
				PubFunc.closeIoResource(fout);
				PubFunc.closeIoResource(in);
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
