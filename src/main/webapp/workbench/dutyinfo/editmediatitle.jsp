<%@ page contentType="text/html; charset=UTF-8" language="java"%>
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
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<html:form action="/workbench/dutyinfo/searchmediainfolist"> 
<br/><br/>
<table width="600" border="0" cellpadding="0" cellspacing="0" align="center">
   <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="conlumn.mediainfo.titleedit"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>  --> 
       		<td width=130 align="left" colspan="2" class="TableRow">&nbsp;<bean:message key="conlumn.mediainfo.titleedit"/>&nbsp;</td>           	      
  </tr>  
   <tr>
                <td align="right">
                  <bean:message key="conlumn.mediainfo.info_title"/>&nbsp;
                </td>
               <td align="left">
                  <html:text   name="dutyInfoForm" property="filetitle"  styleClass="textborder" maxlength="20" size="25"/>
               </td>
             </tr> 
             <tr   >
                <td colspan="2" align="center" style="height:35px;">
                <br>
                   <hrms:submit styleClass="mybutton"  property="b_update" onclick="document.dutyInfoForm.target='_self';validate('R','filetitle','多媒体标题');return document.returnValue;">
                     <bean:message key="button.save"/>
	           </hrms:submit>
	           <hrms:submit styleClass="mybutton"  property="br_editreturn">
                     <bean:message key="button.return"/>
	           </hrms:submit>
                </td>
            </tr>
         
  </table>
</html:form>
