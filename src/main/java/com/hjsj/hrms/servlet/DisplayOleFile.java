package com.hjsj.hrms.servlet;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class DisplayOleFile extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
       String filename = request.getParameter("filename");
//       String fromflag=request.getParameter("fromflag"); 2017-4-27 删除临时文件会导致导出文件异常。停用此功能 zhanghua
//       
//       int isDel=0;  //fromflag=register 下载后直接删掉临时文件程序 20160707 
//       if (fromflag!=null&&fromflag.trim().equalsIgnoreCase("register"))
//       	isDel=1;
        
        if (filename == null) {
            throw new ServletException("Parameter 'filename' must be supplied");
        }     
        //考虑到中文的文件名称
        filename=PubFunc.decrypt(SafeCode.decode(filename));
        String oldFileName = filename;
        /**针对一些GBK中没有的中文**/
        /**【13610】 中路港：自助用户导出通讯录报500错误 自助账号用 户名含“劼”字，该自助用户登陆系统查看通讯录并导出的时候就报500或404错误无法导出excel**/
        /**原因是在 filename=new String(filename.getBytes("GB2312"),"GBK");转码时，会把“劼”转为“?”，引起报错**/
        /**因此new了一个fileN，来处理这种情况，并在41行，判断文件是否存在时，由之前判断“!file.exists()”改为“!file.exists()&&!fileN.exists()”**/
        /**51行-56行增加对存在fileN的情况的导出***/
        /**孙明 mod 2015-11-6 ！！！！如发现会引起其他bug，请告知，谢谢！！！**/
        filename=new String(filename.getBytes("GB2312"),"Unicode");
        File fileN = new File(System.getProperty("java.io.tmpdir"), filename);
        /*****/
        
        //用原始的文件名
        filename = oldFileName;
        //禁止使用相对路径
        String[] filepath = filename.split("\\./"); 
        filename = filepath[filepath.length-1];
        
        File file = new File(System.getProperty("java.io.tmpdir"), filename);

        //如果原始文件名和转码后的文件名都没有找到文件，提示错误信息。
        if (!file.exists()&&!fileN.exists()) {
        	throw new ServletException("File '" + file.getAbsolutePath() + "' does not exist");
        }
        if(file.exists()){
        	if("doc".equalsIgnoreCase(file.getName().substring(file.getName().length()-3)))
        		ServletUtilities.sendInlineOleFile(file,response);
        	else
        		ServletUtilities.sendTempOleFile(file,response);	
        }else if(fileN.exists()){
        	if("doc".equalsIgnoreCase(fileN.getName().substring(fileN.getName().length()-3)))
        		ServletUtilities.sendInlineOleFile(fileN,response);
        	else
        		ServletUtilities.sendTempOleFile(fileN,response);	
        }
   }
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
    throws ServletException, IOException {
      doPost(arg0, arg1);
  }
}
