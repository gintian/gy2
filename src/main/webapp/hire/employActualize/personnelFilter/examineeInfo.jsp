<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script type="text/javascript" src="/js/wz_tooltip.js"></script>  
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css"; 
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
	String id=(String)request.getParameter("id");
	if(id.indexOf("/")!=-1)
		id=id.split("/")[0];
	String dbName=(String)request.getParameter("dbName");
	
%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<hrms:themes></hrms:themes>
<html:form action="/hire/employActualize/personnelFilter/personnelFilterTree"> 
<input type='button' name='back' value='<bean:message key="kq.search_feast.back"/>'  class="mybutton"  onclick='javascript:history.go(-1)' />
    <hrms:infobrowse nid="<%=id%>" infokind="1" pre="<%=dbName%>" isinfoself="2"/> 
</html:form>
