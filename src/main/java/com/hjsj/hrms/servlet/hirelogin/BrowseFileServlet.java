package com.hjsj.hrms.servlet.hirelogin;

import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeFileBo;
import com.hjsj.hrms.servlet.DownLawBase;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;

public class BrowseFileServlet  extends HttpServlet{

	public BrowseFileServlet() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String a0100 = PubFunc.decrypt(request.getParameter("a0100"));
		//【11482】外网简历上传改为保存文件到文件夹，简历预览时，使用文件名下载文件  jingq upd 2015.08.05
		String filename = PubFunc.decrypt(request.getParameter("filename"));
		String dbName = PubFunc.decrypt(request.getParameter("dbName"));
		filename = PubFunc.hireKeyWord_filter_reback(filename);
		String ext = "";
		ServletOutputStream sos = response.getOutputStream();
		String name = "";
		Connection con = null;
		InputStream inputStream = null;
		try {
			con = AdminDb.getConnection();
			try {
				UserView userView =  (UserView) request.getSession().getAttribute(WebConstant.userView);
				ResumeFileBo bo = new ResumeFileBo(con, userView);
				String path = bo.getPath(dbName, a0100);
				File filedir = new File(path);
				File doc = null;
				if(filedir.exists()){
					File[] filelist = filedir.listFiles();
					for (int i = 0; i < filelist.length; i++) {
						File file = filelist[i];
						if(file.isFile()){
							if(filename.equals(file.getName())){
								doc = new File(path+file.getName());
								break;
							}
						}
					}
					if(doc.exists()){
						ext = doc.getName().substring(doc.getName().lastIndexOf(".")+1);
						inputStream = new FileInputStream(doc);
						String mime = ServletUtilities.getMimeType("." + ext);
						// 设置http头部声明为下载模式
						if (DownLawBase.checkCanOpenFile(ext)) {
							response.setContentType(mime);
							name = "fileDown_"+userView.getUserName()+"." + ext;// 加上这句，支持中文文件名，但IE6却多出了一个窗口
							response.setHeader("Content-disposition",
									"attachment;filename=\"" + name + "\"");
						} else {
							response.setContentType("APPLICATION/OCTET-STREAM");
							response.setHeader("Content-Disposition",
									"attachment;   filename=\"extFile\"");
						}
						byte buf[] = new byte[1024];
						int len;
						while ((len = inputStream.read(buf)) != -1) {
							sos.write(buf, 0, len);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(inputStream);
			PubFunc.closeResource(con);
		}

	}

}
