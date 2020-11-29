package com.hjsj.hrms.servlet.pos.police;

import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * 打开工作制度
 * <p>Title:OpenWorkfileServlet.java</p>
 * <p>Description>:OpenWorkfileServlet.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Feb 9, 2010 11:55:32 AM</p>
 * <p>@version: 4.0</p>
 * <p>@author: s.xin
 */
public class OpenWorkfileServlet extends HttpServlet {
	 protected void doPost(HttpServletRequest req, HttpServletResponse resp)
     throws ServletException, IOException {
            String flag=req.getParameter("flag");
		 	String type=req.getParameter("type");
		 	try {
				String filename=createFile(flag,type);
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
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 }
	 public static String createFile(String flag,String type) throws Exception {
	        File tempFile = null;
	        String filename="";
	        ServletUtilities.createTempDir();
	        ResultSet rs = null;
	        Connection conn = null;
	        InputStream in = null;
	        java.io.FileOutputStream fout = null;
	        ContentDAO dao = new ContentDAO(conn);
	        try {
	        	 StringBuffer sql= new StringBuffer();
	        	 sql.append("select name,content,ext from resource_list where ");
	        	 if("1".equalsIgnoreCase(flag))
	        	 {
	        		 sql.append(" name='分监区工作流程'");
	        	 }else if("2".equalsIgnoreCase(flag))
	        	 {
	        		 sql.append(" name='独立工作环节'");
	        	 }else if("3".equalsIgnoreCase(flag))
	             {
	        		 sql.append(" name='专项教育活动'");
	             }
	        	 sql.append("order by contentid desc");
	            rs = dao.search(sql.toString());
	            if (rs.next()) {
	                
	                String prefix = rs.getString("name");
	                if(prefix==null){
	                	prefix="";
	                }
	                if(prefix.length()<4)
	                	prefix = "media";
	                tempFile = File.createTempFile(prefix+"-", rs.getString("ext"),
	                        new File(System.getProperty("java.io.tmpdir")));
	                in = rs.getBinaryStream("content");                
	                fout = new java.io.FileOutputStream(tempFile);
	                
	                int len;
	                byte buf[] = new byte[1024];
	            
	                while ((len = in.read(buf, 0, 1024)) != -1) {
	                    fout.write(buf, 0, len);
	               
	                }
	                //System.out.println("fieldname " + userNumber);
	                filename= tempFile.getName();                
	            }
	        } catch (SQLException sqle) {
	            sqle.printStackTrace();

	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	        	PubFunc.closeResource(fout);//资源释放 jingq 2014.12.29
	        	PubFunc.closeIoResource(in);
	            if (rs != null)
	                rs.close();
	            if (conn != null)
	                conn.close();
	        }
	        return filename;
	    }
}
