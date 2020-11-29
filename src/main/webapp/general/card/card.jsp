<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hjsj.hrms.actionform.ykcard.CardTagParamForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.businessobject.ykcard.DataEncapsulation"%>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
</head>
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
%>
<script type='text/javascript' src='../../../ext/ext6/ext-all.js'></script>
<script type='text/javascript' src='../../../ext/ext6/locale-zh_CN.js' ></script>
<script type='text/javascript' src='../../../ext/rpc_command.js'></script>
<link rel='stylesheet' href='../../../ext/ext6/resources/ext-theme.css' type='text/css' />
<script language="javascript">
    	var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	function getOs()  
	{   
	   if(navigator.userAgent.indexOf("MSIE")>0) {  
	        return "MSIE";  
	   }  
	   if(isFirefox=navigator.userAgent.indexOf("Firefox")>0){  
	        return "Firefox";  
	   }  
	   if(isSafari=navigator.userAgent.indexOf("Chrome")>0) {  
	        return "Chrome";  
	   }
	   if(isSafari=navigator.userAgent.indexOf("Safari")>0) {  
	        return "Safari";  
	   }   
	   if(isCamino=navigator.userAgent.indexOf("Camino")>0){  
	        return "Camino";  
	   }  
	   if(isMozilla=navigator.userAgent.indexOf("Gecko/")>0){  
	        return "Gecko";  
	   }  
	    
	}  
	
</script>
<%
	String browser = DataEncapsulation.analyseBrowser(request.getHeader("user-agent"));
 %>
<html:form action="/general/card/searchcard">
  <!--<table width="90%">
    <tr width="90%">
       <td width="90%" align="center">
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type='button' value='生成PDF'  class='mybutton' onclick='excecutePDF()'> 
       </td>
     </tr>
  </table>   -->
<div id="card">
<input type="hidden" id="firstFlag" name="firstFlag" value="${cardTagParamForm.firstFlag}"/>
  <table>
     <tr>
       <td align="center">      
         <hrms:ykcard name="cardTagParamForm" property="cardparam" istype="3" nid="${cardTagParamForm.a0100}" tabid="${cardTagParamForm.tabid}" cardtype="${cardTagParamForm.cardtype}" disting_pt="javascript:screen.width" userpriv="noinfo" havepriv="1" queryflag="0" infokind="${cardTagParamForm.inforkind}" plan_id="${cardTagParamForm.plan_id}" browser="<%=browser %>"/>
       </td>
    </tr>
  </table>
</div> 
 </html:form>
