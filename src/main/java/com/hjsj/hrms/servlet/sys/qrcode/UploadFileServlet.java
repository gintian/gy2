package com.hjsj.hrms.servlet.sys.qrcode;

import com.hrms.struts.constant.SystemConfig;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Category;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;


public class UploadFileServlet extends HttpServlet{
	private Category cat = Category.getInstance(UploadFileServlet.class); 
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");  
		resp.setCharacterEncoding("utf-8");  
        //1、创建一个DiskFileItemFactory工厂  
        DiskFileItemFactory factory = new DiskFileItemFactory();  
        //2、创建一个文件上传解析器  
        ServletFileUpload upload = new ServletFileUpload(factory);  
        //解决上传文件名的中文乱码  
        upload.setHeaderEncoding("UTF-8");   
        factory.setSizeThreshold(1024 * 1024 * 5);//设置内存的临界值为5M
        upload.setSizeMax(1024 * 1024 * 500);//设置上传的文件总的大小不能超过500M  
        
        InputStream in = null;
        OutputStream out = null;
        try {  
        	/**
        	 * 通过photoSelector组件上传文件 ，需要2个参数 
        	 * fileId 参数: 上传文件名称
        	 * file 参数: 上传文件对象
        	 */
            // 1. 得到 FileItem 的集合 items  
            List<FileItem> items = upload.parseRequest(req);  
            String fileId = "";
            // 2. 遍历 items:  
            for (FileItem item : items) {  
                // 若是一个一般的表单域, 打印信息  
                if (item.isFormField()) {//普通文本  
                    String name = item.getFieldName();
                    if("fileId".equalsIgnoreCase(name))
                    	fileId = item.getString("utf-8");  
                }  
                else {//文件对象  
                	String fileName = item.getName();//文件真实名称  
                	long sizeInBytes = item.getSize();//上传文件大小
                	String filetype = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
        	        if(SystemConfig.getAllowExt().indexOf(","+filetype.toLowerCase()+",") == -1){
        	        	resp.getWriter().write("fail`filetype");
        	        	return;
        	        }
        	        if ((int)sizeInBytes <= 0){// 图像数据为空 
        	        	resp.getWriter().write("fail`filesize");
        	            return;
        	        }
        	        String tempdir = System.getProperty("java.io.tmpdir");//获得临时目录路径
        	        if(!tempdir.endsWith("\\")) {
        	            tempdir = tempdir+"\\";
        	        }
        	        tempdir = tempdir.replace("\\", File.separator).replace("/", File.separator);//解决linux和windows文件路径分隔符不同的问题
        	        String imgPath = tempdir + fileId+"."+filetype;//图片地址
        	        cat.debug("imgAddress--"+imgPath);
        	        
                    in = item.getInputStream();  
                    byte[] buffer = new byte[1024];  
                    int len = 0;  
                    out = new FileOutputStream(imgPath);  
                    while ((len = in.read(buffer)) != -1) {  
                        out.write(buffer, 0, len);  
                    }  
                     
                    resp.getWriter().write("success");
                }  
            }  
        } catch (Exception e) {  
            resp.getWriter().write("fail`file");
        }finally{
        	if(out != null)
        		out.close(); 
        	if(in != null)
        		in.close();
        }
	}
}
