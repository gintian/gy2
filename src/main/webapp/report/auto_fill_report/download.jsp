<%@ page import="com.hjsj.hrms.utils.PubFunc" %>
	<%   
	      String source = PubFunc.keyWord_reback(request.getParameter("message"));  //update by wangchaoqun on 2014-9-15
	      source = source.replaceAll("<br>" , "\r\n");
		  source = source.replaceAll("&nbsp;" , " ");
		  source = source.replaceAll("&#160;" , " ");
		  
	      String   filename   =   "chkerr.txt";   
	      response.setCharacterEncoding("utf-8");   //add by wangchaoqun on 2014-9-15
	      response.setContentType("APPLICATION/OCTET-STREAM");   
	      response.setHeader("Content-Disposition",   
	      "attachment;   filename=\""   +   filename   +   "\"");   
	 
	      out.write(source);   

	 %> 
	 
	