package com.hjsj.hrms.servlet;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;

public class DownLoadPhotoServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Connection connection = null;
		ResultSet resultset = null;
		InputStream inputstream = null;
		ServletOutputStream servletoutputstream = null;

		try {
			connection = (Connection) AdminDb.getConnection();
			String userbase = request.getParameter("userbase");
			String A0100 = request.getParameter("a0100");
			String name = request.getParameter("name");
			String ext = "";
			// String selectSql = "select * from "+ userbase +"A00 where A0100
			// ='"+A0100+"' and flag = 'P' ";
			// 【7930】员工管理——信息浏览——下载照片，下载的照片名字是登录账 号，应该是当前被浏览人的姓名 jingq upd
			// 2015.03.11
			ContentDAO dao = new ContentDAO(connection);
			// zhangcq 2016-06-06 下载照片如果硬盘上存在则从硬盘中下载
			boolean photoSuccess = false;
			PhotoImgBo pib = new PhotoImgBo(connection);// 头像图片操作类
			String absPath = "";
			try {
				absPath = pib.getPhotoRootDir();// 获取多媒体路径
			} catch (Exception e) {

			}
			// 如果有照片有附件路径
			if (absPath != null && absPath.length() > 0) {
				try {
					absPath += pib.getPhotoRelativeDir(userbase, A0100);
				} catch (Exception e) {

				}
				String fileWName = pib
						.getPersonImageWholeName(absPath, "photo");
				// 如果不存在文件，创建文件
				if (fileWName.length() < 1)
					fileWName = pib.createPersonPhoto(absPath, connection,
							userbase, A0100, "photo");
				if (fileWName != null && !"".equals(fileWName)) {
					absPath += fileWName;
					inputstream = new FileInputStream(absPath);
					String[] files = fileWName.split("\\.");
					String fileName = " ";
					for (int i = 0; i < files.length; i++) {
						fileName = files[1];
					}
					
					StringBuffer selectSql = new StringBuffer();
					selectSql.append("select A0101");
					selectSql.append(" from " + userbase + "A01");
					selectSql.append(" where A0100 = '"	+ A0100 + "'");
					
					resultset = dao.search(selectSql.toString());
					if (resultset.next()) {
						String userName = resultset.getString("A0101");
						response.setContentType(ServletUtilities.getMimeType(fileName));
						name = new String((userName + "." + fileName).getBytes("GB2312"), "ISO8859_1");
						response.setHeader("Content-disposition", "attachment;filename=\"" + name + "\"");
					}
					
					photoSuccess = true;
				}
			}
			
			if(!photoSuccess) {
				StringBuffer photoSql = new StringBuffer();
				photoSql.append("select a.A0101,b.EXT,b.OLE from " + userbase);
				photoSql.append("A01 a join " + userbase);
				photoSql.append("A00 b on a.A0100 = b.A0100 where b.A0100 = '");
				photoSql.append(A0100 + "' and b.Flag = 'P'");
		/*		String photoSql = "select a.A0101,b.EXT,b.OLE from " + userbase
						+ "A01 a join " + userbase
						+ "A00 b on a.A0100 = b.A0100 where b.A0100 = '"
						+ A0100 + "' and b.Flag = 'P'";*/
				resultset = dao.search(photoSql.toString());
				if (resultset.next()) {
					name = resultset.getString("A0101");
					ext = resultset.getString("ext");
					inputstream = resultset.getBinaryStream("ole");// getBinaryStream(2);
					response.setContentType(ServletUtilities.getMimeType(ext));
					name = new String((name + ext).getBytes("GB2312"),
							"ISO8859_1");// 加上这句，支持中文文件名，但IE6却多出了一个窗口
					response.setHeader("Content-disposition", "attachment;filename=\"" + name + "\"");
				} else {
					response.setContentType("text/html; charset=UTF-8");
					response.getWriter().print(
							"<script type=\"text/javascript\">"
									+ "alert('您暂时还没有上传照片！'); window.close();"
									+ "</script>");
					return;
				}
			}
			/**linbz  20160903     在浏览器显示对象文件*/
			String openflag=request.getParameter("openflag");
			if("true".equalsIgnoreCase(openflag)){ //直接打开 
				response.setHeader("Content-Disposition","inline;filename=" + name);
			}
			
			servletoutputstream = response.getOutputStream();
			int len;
			byte buf[] = new byte[1024];
			while ((len = inputstream.read(buf)) != -1) {
				servletoutputstream.write(buf, 0, len);
			}
			response.setStatus(HttpServletResponse.SC_OK);
			response.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputstream != null) {
					inputstream.close();
					inputstream = null;
				}
				if (servletoutputstream != null) {
					servletoutputstream.flush();
					servletoutputstream.close();
					servletoutputstream = null;
				}

				if (resultset != null) {
					resultset.close();
					resultset = null;
				}
				if (connection != null) {
					connection.close();
					connection = null;
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}