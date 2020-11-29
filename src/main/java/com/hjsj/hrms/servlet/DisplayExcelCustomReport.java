package com.hjsj.hrms.servlet;

import com.hrms.frame.codec.SafeCode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;

/**
 * <p>Title:DisplayOleContent</p>
 * <p>Description:显示多媒体字段内容</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 15, 2005:4:44:48 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class DisplayExcelCustomReport extends HttpServlet {

    
    protected void service(HttpServletRequest request, HttpServletResponse response)
             throws ServletException, IOException {
        HttpSession session = request.getSession();
        response.setContentType("text/html;charset=gb2312"); 
        request.setCharacterEncoding("GBK");
        response.setCharacterEncoding("GBK");
        String filename = request.getParameter("filename");
        //考虑到中文的文件名称
        filename=SafeCode.decode(filename);
        //  Check the file exists
        filename=new String(filename.getBytes("GB2312"),"GBK");
	 	try
		{
 	 	    File file = new File(System.getProperty("java.io.tmpdir"), filename);
	        if (!file.exists()) {
	            throw new ServletException(
	                "File '" + file.getAbsolutePath() + "' does not exist"
	            );
	    }
	    ServletUtilities.sendInlineExcelFile(file,response);
		}catch(Exception e)
		{
	 		e.printStackTrace();
	 	}	

    }
    /**
     * 
     */
    public DisplayExcelCustomReport() {
        super();
    }

}
