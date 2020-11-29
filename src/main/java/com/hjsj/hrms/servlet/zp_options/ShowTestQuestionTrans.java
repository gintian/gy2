/*
 * Created on 2005-9-6
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.servlet.zp_options;

import com.hjsj.hrms.servlet.ServletUtilities;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;

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

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ShowTestQuestionTrans extends HttpServlet {
	public ShowTestQuestionTrans() {
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
			String s;
			s = httpservletrequest.getParameter("a_testid");
			connection = (Connection) AdminDb.getConnection();
			String sql = "select ext,test_questions from zp_pos_test where test_id=" + s;
			
			ContentDAO dao = new ContentDAO(connection);
			resultset = dao.search(sql);
			inputstream = null;
			String ext = "";
			if (resultset.next()) {
				ext = resultset.getString("ext");
				inputstream = resultset.getBinaryStream("test_questions");
			} else {
				return;
			}
			servletoutputstream = httpservletresponse.getOutputStream();
			
			          
			httpservletresponse.setContentType(ServletUtilities.getMimeType("."+ext));			
            int len;
            byte buf[] = new byte[1024];
			while ((len = inputstream.read(buf)) != -1) {
				servletoutputstream.write(buf,0,len);
			}			    
		} 
		catch (IOException ex) {
			ex.printStackTrace();
		} catch (SQLException ex) {
			ex.printStackTrace() ;
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
}
