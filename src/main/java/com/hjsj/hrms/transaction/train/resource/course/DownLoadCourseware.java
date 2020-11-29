package com.hjsj.hrms.transaction.train.resource.course;

import com.hjsj.hrms.servlet.DownLawBase;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.util.regex.Matcher;

public class DownLoadCourseware extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public DownLoadCourseware() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("GBK");
		response.setCharacterEncoding("GBK");
		
		FileInputStream fis = null;
		OutputStream os = null;
		PrintWriter out = null;
		Connection conn = null;

		try {
			String url = SafeCode.decode(request.getParameter("url"));
			String fileName = SafeCode.decode(request.getParameter("fileName"));
			url = PubFunc.decrypt(SafeCode.decode(url));
			String isFrom = request.getParameter("isFrom");
			url = url.replaceAll(Matcher.quoteReplacement("\\"), "/");
//			conn = AdminDb.getConnection();
//            TrainCourseBo tbo = new TrainCourseBo(conn);
//            String rootPath = tbo.getAttacmentRootDir("1");
//            if (StringUtils.isNotEmpty(rootPath) && url.startsWith("/videostreams")) {
//                url = url.substring(url.lastIndexOf("/videostreams/") + 14);
//                url = rootPath + url;
//            }
            
			int index  = url.lastIndexOf("/");
			String name = url.substring(index + 1, url.length());
			if(StringUtils.isNotEmpty(fileName)) {
				String[] fileNames = fileName.split("\\./"); 
				fileName = fileNames[fileNames.length-1];
				name = fileName + name.substring(name.lastIndexOf("."));
			}
			
			if(name==null||name.length()<1){
				response.setContentType("text/html");
				response.setCharacterEncoding("GBK");
				out = response.getWriter();
				out.println("<script>alert('该文件不存在!');history.back();</script>");
				return;
			}
			String[] strs = name.split("\\.");
			String ext = strs[1];
			String mime = ServletUtilities.getMimeType("." + ext);
			
			// 设置http头部声明为下载模式
			if("mobile".equals(isFrom)){
				name = new String(name.getBytes("utf-8"), "ISO_8859_1");
			}else{
				name = new String(name.getBytes("gbk"), "ISO_8859_1");
			}
			url = url.replace("'", "/");
			File file = new File(url);
			if(file.exists()){
				fis = new FileInputStream(file);
				if (DownLawBase.checkCanOpenFile(ext)) {
					response.setContentType(mime);
				} else {
					response.setContentType("APPLICATION/OCTET-STREAM");
				}
				
				response.setHeader("Content-Disposition",
				        "attachment;filename=\"" + name + "\"");
				
				os = response.getOutputStream();
				byte[] b = new byte[1024];
				int len = -1;
				while ((len = fis.read(b)) != -1) {
					os.write(b, 0, len);
				}
			}else{
				response.setContentType("text/html");
				response.setCharacterEncoding("GBK");
				out = response.getWriter();
				out.println("<script>alert('该文件不存在!');history.back();</script>");
			}
		} catch(java.io.IOException ex){
			//ClientAbortException是IOException ，抛出此异常和客户端浏览器有关但不影响功能，只是抛出来到控制台不好看，故针对此异常不做堆栈输出
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		} finally {
		    PubFunc.closeIoResource(conn);
			PubFunc.closeIoResource(fis);
			PubFunc.closeIoResource(os);
			PubFunc.closeIoResource(out);
		}

	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doPost(request, response);
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
