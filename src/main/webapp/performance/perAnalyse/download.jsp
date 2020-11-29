	<%@ page contentType="text/html; charset=UTF-8"%>
	<%@ page import="java.util.*,
                 com.hjsj.hrms.actionform.performance.PerAnalyseForm" %>
	<%   
		  PerAnalyseForm perAnalyseForm=(PerAnalyseForm)session.getAttribute("perAnalyseForm");
		  String remark=perAnalyseForm.getRemark();

			if(remark!=null)
	     	{
	      remark = remark.replaceAll("<br>" , "\r\n");
	      remark = remark.replaceAll("\n" , "\r\n");
		  remark = remark.replaceAll("&nbsp;" , " ");
		  
	      String   filename   =   "remark.txt";   
	      response.setContentType("APPLICATION/OCTET-STREAM");   
	      response.setHeader("Content-Disposition",   
	      "attachment;   filename=\""   +   filename   +   "\"");
	      out.write(remark);
	      out.close(); 
}
	 %> 
	 
	