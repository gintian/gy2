<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
%>

<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >

<html:form action="/system/gcodeselect">
   <div id="_dropdown_div" style="overflow: auto;border-style:inset ;border-width:2px">
		   
   <SCRIPT LANGUAGE=javascript>
             var m_sXMLFile="/system/get_code_treeinputinfo.jsp?codesetid="+"${codeSelectForm.codesetid}"+"&codeitemid=";//+codevalue + "&isfirstnode=" + flag;	 //
                          
             var root=new xtreeItem("root","代码项目ss","","","代码项目","/images/unit.gif",m_sXMLFile);
             Global.closeAction="savecode();window.close();";
             root.setup(document.getElementById("_dropdown_div"));
	     
   </SCRIPT>    
   </div>
</html:form>

<script language="javascript">
initDropDownBox("custom");
</script> 



