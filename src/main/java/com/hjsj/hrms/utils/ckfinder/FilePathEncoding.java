package com.hjsj.hrms.utils.ckfinder;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;

/**
 * 
 * @author guodd
 * createdate 2016-09-30
 * 对ckfinder选择的图片，直接使用路径会有中文名图片显示不了问题，
 * 使用公共servlet加载图片解决此问题。
 */
public class FilePathEncoding extends HttpServlet{

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String filePath = req.getParameter("filePath");
		filePath = SafeCode.decode(filePath);
		filePath = PubFunc.keyWord_reback(filePath);
		filePath = URLDecoder.decode(filePath, "GBK");
		//将路径加密，并使用公共servlet加载图片
		filePath = "/servlet/DisplayOleContent?fromflag=ckfinder&filePath="+PubFunc.encrypt(filePath);
		resp.getWriter().write(filePath);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doGet(req, resp);
	}

}
