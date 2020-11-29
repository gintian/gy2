package com.hjsj.hrms.servlet;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class DisplayCustomerWordFile extends HttpServlet {
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		//String filename = request.getParameter("filename");
		String filepath = request.getParameter("filepath");
		filepath = PubFunc.decrypt(SafeCode.decode(filepath));
		filepath = PubFunc.keyWord_reback(filepath);

		if (filepath == null) {
			throw new ServletException("Parameter 'filepath' must be supplied");
		}
		// 考虑到中文的文件名称
		filepath = SafeCode.decode(filepath);
		// Check the file exists
//		filepath = new String(filepath.getBytes("GB2312"), "GBK");
		// 禁止使用相对路径
		//String[] filepath = filepath.split("\\./");
		//filename = filepath[filepath.length - 1];

		File file = new File(filepath);
		
		if (!file.exists()) {
			// return;
			throw new ServletException("File '" + file.getAbsolutePath()
					+ "' does not exist");
		}

		ServletOutputStream servletoutputstream = null;
		try {
			servletoutputstream = response.getOutputStream();
			String mimeType = ServletUtilities.getMimeType(file.getName());
			servletoutputstream.write("EDA_STREAMBOUNDARY".getBytes());
			sendTempFile(file, response, mimeType, "", servletoutputstream);
			servletoutputstream.write("EDA_STREAMBOUNDARY".getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (servletoutputstream != null)
					servletoutputstream.flush();
				if (servletoutputstream != null)
					servletoutputstream.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	private void sendTempFile(File file, HttpServletResponse response,
			String mimeType, String displayfilename,
			ServletOutputStream servletoutputstream) throws IOException {
		BufferedInputStream bis = null;
		FileInputStream fis = null;
		try {
			if (file.exists()) {
			    fis = new FileInputStream(file);
				bis = new BufferedInputStream(fis);

				// Set HTTP headers
				if (mimeType != null) {
					response.setContentType(mimeType);

					// response.setHeader("Content-Type", mimeType);
				}
				// response.setHeader("Content-Length",
				// String.valueOf(file.length()));
				// SimpleDateFormat sdf = new
				// SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
				// sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
				// response.setHeader("Last-Modified", sdf.format(new
				// Date(file.lastModified())));
				/*
				 * yuxiaochun zengjia 显示下在文件名称。
				 */
				String name = "";
				if (displayfilename != null && !"".equals(displayfilename))
					name = new String(displayfilename.getBytes("gb2312"),
							"ISO8859_1");
				else
					name = new String(file.getName().getBytes("gb2312"),
							"ISO8859_1");
				response.setHeader("Content-Disposition",
						"attachment;filename=" + name);

				int len;
				byte buf[] = new byte[1024];
				while ((len = bis.read(buf)) != -1) {
					servletoutputstream.write(buf, 0, len);
				}
				/*
				 * BufferedOutputStream bos = new
				 * BufferedOutputStream(response.getOutputStream()); byte[]
				 * input = new byte[1024]; int len; while ((len =
				 * bis.read(input)) != -1) { bos.write(input,0,len); }
				 */
				/*
				 * boolean eof = false; while (!eof) { int length =
				 * bis.read(input); if (length == -1) { eof = true; } else {
				 * bos.write(input, 0, length); } }
				 */
				// bos.flush();
				// bis.close();
				// bos.close();
			} else {
				throw new FileNotFoundException(file.getAbsolutePath());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}finally{
		    PubFunc.closeResource(bis);
		    PubFunc.closeResource(fis);
		}

		return;
	}

	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		doPost(arg0, arg1);
	}
}
