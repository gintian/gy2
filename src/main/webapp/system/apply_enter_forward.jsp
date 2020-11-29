
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
    try{
		String apply_path=(String)session.getAttribute("ehr_apply_path");
		/**清空未登录前输入的url*/	
		session.removeAttribute("ehr_apply_path"); 
	    request.getRequestDispatcher(apply_path).forward(request,response);
	    /**解决 response.getWrite()和response.getOUtputStream冲突问题*/
	    out.clear();
	    out = pageContext.pushBody();
     }catch(IllegalStateException e){
	    e.printStackTrace();
	   }
%>
