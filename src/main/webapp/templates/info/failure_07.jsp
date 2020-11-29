<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ page import='com.hrms.struts.utility.JSMessage' %>


<%

	String errMsg=(String)session.getAttribute("errMsg");
	
	if( errMsg != null&&errMsg.length()>0 )	
	{
		out.println("<script language=\"javascript\">");
		out.println("alert('"+errMsg+"');");		
		out.println("</script>");
	}else
	{
	   out.println("<script language=\"javascript\">");
	   out.println(" window.location=\"/index.jsp\";");
	   out.println("</script>");
	}
%>