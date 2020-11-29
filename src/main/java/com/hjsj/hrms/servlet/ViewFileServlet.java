/**
 * <p>Title:File view</p>
 * 浏览文件
 * <p>Company:hjsj</p>
 * <p>create time:2005-6-4:15:43:02</p>
 * @author FengXiBin
 * @version 1.0
 * 
 */
package com.hjsj.hrms.servlet;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewFileServlet extends HttpServlet {

	public ViewFileServlet() {
	}

	public void doGet(HttpServletRequest httpservletrequest,
			HttpServletResponse httpservletresponse) throws ServletException,
			IOException {
		doPost(httpservletrequest, httpservletresponse);
	}

	public void doPost(HttpServletRequest httpservletrequest,
			HttpServletResponse httpservletresponse) throws ServletException,
			IOException 
	{
		Connection connection = null;
		ResultSet resultset = null;
		InputStream inputstream = null;
		ServletOutputStream servletoutputstream = null;
		try {
			String a0100 =a0100 = httpservletrequest.getParameter("a0100");
			String dbpre;
			String i9999;
			String kind;
			i9999 = httpservletrequest.getParameter("i9999");
			kind = httpservletrequest.getParameter("kind");
			connection = (Connection) AdminDb.getConnection();
			StringBuffer sql = new StringBuffer();
			if("6".equals(kind))
			{
				dbpre = httpservletrequest.getParameter("dbpre");
				sql.append(" select title,ext,ole from "+dbpre+"a00 ");	
				sql.append(" where a0100='"+a0100+"'");
			}else if("0".equals(kind))
			{
				sql.append(" select title,ext,ole from k00 ");	
				sql.append(" where e01a1='"+a0100+"'");
				
			}else if("9".equals(kind))
			{
				sql.append(" select title,ext,ole from H00 ");	
				sql.append(" where h0100='"+a0100+"'");
				
			}else
			{
				sql.append(" select title,ext,ole from b00 ");	
				sql.append(" where b0110='"+a0100+"'");
			}
//			sql.append(" select title,ext,ole from "+dbpre+"a00 ");	
//			sql.append(" where a0100='"+a0100+"'");
			sql.append(" and i9999="+i9999);
			ContentDAO dao = new ContentDAO(connection);
			resultset = dao.search(sql.toString());
			inputstream = null;
			String ext = "";
			String name="download";
			if (resultset.next()) {
				ext = resultset.getString("ext");
				name= resultset.getString("title");				
				inputstream = resultset.getBinaryStream("ole");
				if(!"".equals(ext)&&ext!=null){//兼容附件类型没有带“.”的，添加“.”到类型名  赵国栋  2013-12-12
					if(ext.indexOf(".")==-1){
						ext="."+ext;
					}
				}
			} else {
				return;
			}
			servletoutputstream = httpservletresponse.getOutputStream();
			httpservletresponse.setContentType(ServletUtilities.getMimeType(ext));	
			String pre=name;
            String IllegalString = "/\\:*?\"<>|";
            for(int i=0;i<pre.length();i++){
        		if(IllegalString.indexOf(pre.substring(i,i+1))>=0){
        			name =  name.replace(pre.substring(i,i+1), "");
        		}
        	}
			name=new String((name+ext).getBytes("gb2312"),"ISO8859_1");
			httpservletresponse.setHeader("Content-disposition", "attachment;filename=\"" + name + "\"");
			httpservletresponse.addHeader("Content-description",  name);
            int len;
            byte buf[] = new byte[1024];
			while ((len = inputstream.read(buf)) != -1) {
				servletoutputstream.write(buf,0,len);
			}			
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			try {
				if (inputstream != null)
					inputstream.close();
				if (servletoutputstream != null)
					servletoutputstream.flush();
				if (servletoutputstream != null)
					servletoutputstream.close();
				if (resultset != null)
					resultset.close();
				if (connection != null)
					connection.close();
			}

			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return;
	}
	
	void doFileDirectOper()
	{
		/**
		 * 新建下载文件夹的处理
		 */
		File file2=new File(getServletContext().getRealPath("") + "/temp/temp");
		
		if(file2.isDirectory())
		{
			
		}
		else
		{
			file2.mkdirs();
			
		}
		File file3=new File(getServletContext().getRealPath("") + "/temp/temp");
		
		File filelist [] =file3.listFiles();
		if(filelist.length>0)
		{
			for(int i=0;i<filelist.length;i++)
			{
			//System.out.println("------>"+filelist[i].lastModified());
			//Calendar calendar=Calendar.getInstance();
			//System.out.println("---->"+calendar.getTimeInMillis());
			
			}
		}
		
	}
}