package com.hjsj.hrms.servlet;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class DisplayImageServlet extends HttpServlet {
	public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		this.doPost(request, response);
	}
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		InputStream is = null;
		OutputStream os = null;
		try {
			String filePath = request.getParameter("filepath");
			filePath = PubFunc.keyWord_reback(filePath);
			filePath = PubFunc.decryption(SafeCode.decode(filePath));
			if (filePath == null) {
				throw new ServletException("Parameter 'filepath' must be supplied");
			}
			//后缀名
			String ext = filePath.substring(filePath.lastIndexOf(".")+1);
			//不是图片
			if("jpg;jpeg;png;bmp".indexOf(ext) == -1)
				throw new  ServletException("不是图片!");
				
			File image = new File(filePath);
			if(!image.exists())
				throw new  ServletException("未找到指定的图片,请确认图片路径是否正确!");
			
			is = new FileInputStream(image);
			//得到文件大小
			int size = is.available();
			byte[] datas = new byte[size];
			//读数据
			is.read(datas);
			response.setContentType("image/"+ext+";charset=UTF-8");
			os = response.getOutputStream();
			os.write(datas);
			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(is);
			PubFunc.closeResource(os);
		}
	}
}
