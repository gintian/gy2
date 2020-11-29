<%@   page   contentType="text/html;charset=UTF-8"   language="java"   %>
  <%@   page   import="java.io.*,java.util.*,
  						com.hjsj.hrms.servlet.ServletUtilities,
  						com.hjsj.hrms.utils.PubFunc"%>   
  <% 
  
  response.reset();   
  String filename=request.getParameter("filename");
  filename = PubFunc.decrypt(filename);   //add by wangchaoqun on 2014-9-28
  String mime = ServletUtilities.getMimeType(filename.substring(filename.indexOf(".")));
  response.setContentType(mime);   
  InputStream   ips   =   new   FileInputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+filename);//<---你的xls文件   
  OutputStream   ops   =   response.getOutputStream();   
  int   data   =   -1;   
  while((data   =   ips.read())   !=   -1)   {   
    
  ops.write(data);   
  }   
    
  ops.flush();%>   
