<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	   css_url="/css/css1.css";
	}
%>
<HTML>
<HEAD>
<TITLE></TITLE>

<link href="/css/xtree.css" rel="stylesheet" type="text/css" > 
<script type="text/javascript">
	function ret(){
		var rdos = document.getElementsByTagName("input");
		for(var i=0; i<rdos.length; i++){
			if(rdos[i].type=="radio" && rdos[i].checked){
				returnValue = rdos[i].value;
			}
		}
	}
</script>
</HEAD>
<hrms:themes />
<body style="text-align: center;"> 
<%
	String outname = request.getParameter("outname");
	String[] name=outname.split(",");
 %>
 <span style="width: 100%; margin-top:10px; font-weight:bold; height: 19px; background-color: #F4F7F7; text-align: left;">&nbsp;&nbsp;&nbsp;报批给：</span>
<div style="width: 100%; height: 60%; border-top: 1px solid #7F9DB9;border-bottom: 1px solid #7F9DB9; text-align: left; padding: 8px 20px 5px 20px;overflow-y:auto;">
 <% for(int i=0; i<name.length; i++){ if(i==0){%>
    <input type="radio" name="name" checked="checked" value="<%=(name[i].split(":"))[0] %>"/>&nbsp;&nbsp;<%=(name[i].split(":"))[1] %>
 <%}else{ %>
 	<br/><input style="margin-top: 5px;" type="radio" name="name" value="<%=(name[i].split(":"))[0] %>"/>&nbsp;&nbsp;<%=(name[i].split(":"))[1] %>
 <%} }%>
</div><br/>
	<input type="button" class="mybutton" value='<bean:message key="button.ok"/>' onclick="ret();window.close();"/>&nbsp;
	<input type="button" class="mybutton" value='<bean:message key="button.cancel"/>' onclick="javascript:window.close()"/>

<BODY>
</HTML>


