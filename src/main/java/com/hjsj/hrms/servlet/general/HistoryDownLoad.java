package com.hjsj.hrms.servlet.general;

import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.WebConstant;
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

public class HistoryDownLoad extends HttpServlet {

	final static String[] conReadFile = { "doc", "xls", "pdf", "txt", "html", "htm", "rar" ,"zip"};

	public void doGet(HttpServletRequest httpservletrequest,
			HttpServletResponse httpservletresponse) throws ServletException,
			IOException {
		doPost(httpservletrequest, httpservletresponse);
	}

	public static boolean checkCanOpenFile(String ext) {
		if (ext == null)
			return false;
		for (int i = 0; i < conReadFile.length; i++) {
			if (ext.equalsIgnoreCase(conReadFile[i])) {
				return true;
			}
		}
		return true;
	}

	public void doPost(HttpServletRequest httpservletrequest,
			HttpServletResponse httpservletresponse) throws ServletException,
			IOException {
		UserView userview=(UserView) httpservletrequest.getSession().getAttribute(WebConstant.userView);	
		//liuyz bug25986 未登录用户可以下载文件
		if(userview==null)
		{
			//httpservletresponse.sendRedirect("/templates/index/hcmlogon.jsp");
			return;
		}
		String ext = "";
		String s = "";
		String type = "";
		StringBuffer sb = new StringBuffer("");
		String txtAppend = "";
		Connection connection = null;
		ResultSet resultset = null;
		InputStream inputstream = null;
		ServletOutputStream servletoutputstream = httpservletresponse.getOutputStream();
		try {
			// 判断term用来区分是否是全文检索页面的请求
			s = httpservletrequest.getParameter("id");
			/**安全平台改造,将id转换回来xcs2014-9-25**/
			s = PubFunc.decrypt(s);
			type = httpservletrequest.getParameter("type");
			connection = (Connection) AdminDb.getConnection();
			String sql = "";
			if(type==null)
				type = "";
			
				sql = "select content_ext,content_pdf from template_archive where id="
					+ s + "";
			String name = PubFunc.getStrg();
			ContentDAO dao = new ContentDAO(connection);
//			httpservletrequest.setCharacterEncoding("GB2312");
			resultset = dao.search(sql);
			// 从数据库中取得数据
			if (resultset.next()) {
				
					ext = resultset.getString("content_ext");
					name += "."+ext;					
					inputstream = resultset.getBinaryStream("content_pdf");

				
			} else {
				/*CommonBusiness comnbus = new CommonBusiness(connection);
				comnbus.updateViewCount(s);*/
				return;
			}
			if (inputstream == null) {
				return;
			}
			//servletoutputstream = httpservletresponse.getOutputStream();
			String mime = ServletUtilities.getMimeType("." + ext);
			// 设置http头部声明为下载模式
			//if (checkCanOpenFile(ext)) {
				httpservletresponse.setContentType(mime);	
				name=new String(name.getBytes("gb2312"),"ISO8859_1");

				httpservletresponse.setHeader("Content-Disposition", "attachment;filename=\"" + name + "\"");
			int len;
			byte buf[] = new byte[512];
			int sumlen=0;
			if (!"".equals(sb.toString().trim())) {
				servletoutputstream.write(sb.toString().getBytes());
			}
			while ((len = inputstream.read(buf)) != -1) {
				sumlen=sumlen+len;
				servletoutputstream.write(buf, 0, len);
			}

			httpservletresponse.setContentLength(sumlen);
			if (!"".equals(txtAppend.trim())) {
				servletoutputstream.write(txtAppend.getBytes());
			}
			servletoutputstream.flush();
			/*CommonBusiness comnbus = new CommonBusiness(connection);
			comnbus.updateViewCount(s);*/
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			/**
			 * 关闭各种资源
			 */
			try {

				if (servletoutputstream != null)
					servletoutputstream.close();
				PubFunc.closeIoResource(inputstream);
				if (resultset != null)
					resultset.close();
				if (connection != null)
					connection.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return;
	}

}
