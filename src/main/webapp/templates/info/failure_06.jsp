<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ page import='com.hrms.struts.utility.JSMessage' %>
<%@ page import='com.hrms.struts.constant.SystemConfig' %>

<%

	String errMsg=(String)session.getAttribute("errMsg");
	Object goto_page=session.getAttribute("goto");
    String portalurl=SystemConfig.getPropertyValue("portalurl"); 
	if( errMsg != null&&errMsg.length()>0 )	
	{
		out.println("<script language=\"javascript\">");
		out.println("alert('"+errMsg+"');");
		/**考滤从单点登录servlet跳转过来的页面*/
		if(portalurl!=null&&portalurl.length()>0)
		{
		    out.println(" window.location=\""+portalurl+"\";");		
		}else if(goto_page!=null)
		{
		    String str_page=(String)goto_page;
		    str_page=str_page.replaceAll(".do",".jsp");
			out.println(" window.location=\""+str_page+"\";");			
		}
		else
		{
		    out.println(" window.location=\"/index.jsp\";");
			//out.println("history.back();");
		}
		out.println("</script>");
	}else
	{
	   out.println("<script language=\"javascript\">");
	   out.println(" window.location=\"/index.jsp\";");
	   out.println("</script>");
	}
%>