/**
 * <p>Title:File down</p>
 * 表格及流程文件下载
 * <p>Company:hjsj</p>
 * <p>create time:2005-6-4:15:43:02</p>
 * @author luangaojiong
 * @version 1.0
 * 
 */
package com.hjsj.hrms.servlet;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DownLoadFileServlet extends HttpServlet {

	public DownLoadFileServlet() {
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
        UserView userView = (UserView)httpservletrequest.getSession().getAttribute("userView");
        // 安全检查：未登录不允许下载
        if (userView == null) {
            return;
        }
        
		Connection connection = null;
		ResultSet resultset = null;
		InputStream inputstream = null;
		ServletOutputStream servletoutputstream = null;
		try {
			String s = httpservletrequest.getParameter("id");
			
			connection = (Connection) AdminDb.getConnection();
			String sql = "select name,ext,content from resource_list where contentid=" + s;
			ContentDAO dao = new ContentDAO(connection);
			resultset = dao.search(sql);
			
			String ext = "";
			String name="download";
			if (resultset.next()) {
				ext = resultset.getString("ext");
				name= resultset.getString("name");				
				inputstream = resultset.getBinaryStream("content");//getBinaryStream(2);
			} else {
				return;
			}

			httpservletresponse.setContentType(ServletUtilities.getMimeType("."+ext));	
			name=new String((name+"."+ext).getBytes("GB2312"),"ISO8859_1");//加上这句，支持中文文件名，但IE6却多出了一个窗口
			httpservletresponse.setHeader("Content-disposition", "attachment;filename=\"" +  name + "\"");
			servletoutputstream = httpservletresponse.getOutputStream();			
			//httpservletresponse.addHeader("Content-description",  name);

			int len;
            byte buf[] = new byte[1024];
			while ((len = inputstream.read(buf)) != -1) {
				servletoutputstream.write(buf,0,len);
			}		
			httpservletresponse.setStatus(HttpServletResponse.SC_OK);
			httpservletresponse.flushBuffer();
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
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			PubFunc.closeDbObj(resultset);
			PubFunc.closeDbObj(connection);
		}
		return;
	}
}