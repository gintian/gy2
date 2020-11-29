package com.hjsj.hrms.servlet.codetree;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;


public class OutputCode extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public OutputCode() {
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
		String aFilePath = ""; // 要下载的文件路径
		String aFileName = null; // 要下载的文件名
		FileInputStream in = null; // 输入流
		ServletOutputStream out = null; // 输出流
		String FileName=(String)request.getParameter("path");
		FileName = PubFunc.decrypt(SafeCode.decode(FileName));
		FileName = PubFunc.hireKeyWord_filter_reback(FileName);
		String[] temp=FileName.split("/");
		aFileName=temp[temp.length-1];
		try {
//			aFilePath = "D:\\";
//			aFileName = "AB.cod";

			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-disposition", "attachment; filename="
					+ aFileName);

			in = new FileInputStream(FileName); // 读入文件
			out = response.getOutputStream();
			out.flush();
			int aRead = 0;
			while ((aRead = in.read()) != -1 && in != null) {
				out.write(aRead);
			}
			out.flush();
		} catch (Throwable e) {
			e.printStackTrace();

		} finally {
			try {
				in.close();
				out.close();
			} catch (Throwable e) {

			}
		}
	}

	
	public void init() throws ServletException {
		// Put your code here
	}

}
