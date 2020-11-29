<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ page import='com.hrms.struts.utility.JSMessage' %>
<%@ page import='com.hrms.struts.constant.SystemConfig' %>

<%

	JSMessage test = new JSMessage(pageContext);
	Object goto_page=session.getAttribute("goto");
    String portalurl=SystemConfig.getPropertyValue("portalurl"); 
	if( test.getMessage() != null )	
	{
		out.println("<script language=\"javascript\">");
		out.println("alert('"+test.getMessage()+"');");
		/**考滤从单点登录servlet跳转过来的页面*/
		if(goto_page!=null)
		{
		    String str_page=(String)goto_page;
		    str_page=str_page.replaceAll(".do",".jsp");
		    
		    if(portalurl!=null&&portalurl.length()>0)
		    {
		        out.println(" window.location=\""+portalurl+"\";");		
		    }else if(str_page.equalsIgnoreCase(portalurl))
			{
				out.println(" window.open(window.location=\""+str_page+"\",\"_top\",\"toolbar=no,location=no,directories=0,fullscreen=0,status=no,menubar=no,scrollbars=no,resizable=no\");");
				out.println(" window.opener=null;");
				out.println(" self.close();");			
			}
			else
			{
				out.println(" window.location=\""+str_page+"\";");
			}
		}
		else
		{
		    out.println(" window.location=\"/index.jsp\";");
			//out.println("history.back();");
		}
		out.println("</script>");
	}
%>