/*
 * Created on 2005-5-27
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.updownfile;


import com.hjsj.hrms.actionform.updownfile.HtmlFileForm;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.DateStyle;
import com.hrms.frame.utility.IDGenerator;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HtmlFileAction extends Action {

	  /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
   public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response)
   
    throws Exception
   {
	   Connection con =null;
	   PreparedStatement ps=null;
	   Connection con1=null;
	   FileInputStream fin=null;
	   OutputStream streamOut=null;
	   InputStream streamIn=null;
   try
   {
   
   		//  取得窗体对象
   			HtmlFileForm hff=(HtmlFileForm)form;
   			FormFile file=hff.getFile();
  
			//Connection con = (Connection)AdminDb.getConnection();
			
			String sDBDriver = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
			String sConnStr = "jdbc:microsoft:sqlserver://127.0.0.1:1433;DatabaseName=ykchr";
			String DBuser= "sa";
			String DBpassword= "";
			
			
		 
		  
				try {
					Class.forName(sDBDriver); 
					}
				catch(java.lang.ClassNotFoundException e) {
					System.err.println("bbsreg(): " + e.getMessage());
					}
			con = DriverManager.getConnection(sConnStr,DBuser,DBpassword); 
			String dir=servlet.getServletContext().getRealPath("/upload");
			 String fname=file.getFileName();
		   	 //大小
		   	 String size=Integer.toString(file.getFileSize())+"bytes";
		   	 streamIn=file.getInputStream();
		   	 streamOut=new FileOutputStream(dir+"/"+fname);
		   	 int filelength1=file.getFileSize();
		   	 
		   	 int bytesRead=0;
		   	 byte[] buffer=new byte[8192];
		   	 while((bytesRead=streamIn.read(buffer,0,8192))!=-1)
		   	 {
		   	 	streamOut.write(buffer,0,bytesRead);
		   	 }
		   	  streamOut.close();
		   	  streamIn.close();
   	
		   	File f=new File(dir+"/"+fname);
		   	 long filelength=f.length();
		   	 fin=new FileInputStream(f);
   	
		   	System.out.println("-------->HtmlFileAction filelong "+filelength);
   	 //id号码操作
   	// IDGenerator idg=new IDGenerator(2,con);
   	 
    // String contentid=idg.getId("resource_list.id");
           
            
   	if(file==null)
   	 {
   	 	return mapping.findForward("ERROR");
   	 }
   	 //文件名
   //	 String fname=file.getFileName();
   	 //大小
   
   	 //  InputStream streamIn=file.getInputStream();
	   int filelengthInt=(int)file.getFileSize();
	   
   	   int lastp=fname.lastIndexOf(".");
   	   String ext=fname.substring(lastp-1,(int)filelengthInt);

   	 con1=AdminDb.getConnection();
     IDGenerator idg=new IDGenerator(2,con1);
     String  id=idg.getId("resource_list.id");
     int idInt=Integer.parseInt(id);

     String sql="insert into resource_list(contentid,[id],name,description,createdate,status,content,ext) values (?,?,?,?,?,?,?,?)";
   	 ps=con.prepareStatement(sql);
   	 
   	 ps.setInt(1,idInt);
   	 ps.setInt(2,1);
   	 ps.setString(3,fname);
   	 ps.setString(4,fname);
   	 ps.setString(5,DateStyle.getSystemTime());
   	 ps.setInt(6,1);
    	
   	 ps.setBinaryStream(7,fin,(int)filelength);

   	 ps.setString(8,"1");
   	 
   	 ps.executeUpdate();
   	 

  }
  catch(Exception ex)
  {
  	ex.printStackTrace();
  }finally{
	  PubFunc.closeIoResource(streamOut);
	  PubFunc.closeIoResource(streamIn);
	  PubFunc.closeResource(fin);
	   PubFunc.closeResource(ps);
	   PubFunc.closeResource(con1);
	   PubFunc.closeResource(con);
  }
   	 return mapping.findForward("uploadsucess");
   	 
   	 
   }
   
   
   
   
   
}
