<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm"%>
<html>
<head>
<LINK 
href="/css/employNetStyle.css" type=text/css rel=stylesheet>
<LINK href="/css/main.css" type=text/css rel=stylesheet>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/hire/employNetPortal/employNetPortal.js"></script>
</head>
<%
EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
			String dbName=employPortalForm.getDbName();
 %>
<BODY leftMargin=0 topMargin=0 onKeyDown="return pf_ChangeFocusmark();"  ><BR><BR><BR><BR><BR><BR><BR><BR>
<html:form action="/hire/employNetPortal/search_zp_position"> 
 
<TABLE cellSpacing=0 cellPadding=0 width=95 align=center border=0>
  <TBODY>
  <TR>
    <TD><IMG height=48 src="/images/app_1.gif" width=389></TD></TR>
  <TR>
    <TD background=/images/app_bg.gif>
      <TABLE cellSpacing=0 cellPadding=0 width="100%" border=0>
        <TBODY>
        <TR>
          <TD width="39%"><IMG height=175 hspace=6 
            src="/images/app_l.gif" width=140></TD>
          <TD vAlign=top width="61%"><IMG height=19 hspace=5 
            src="/images/app_2.gif" width=142 vspace=6> 
            <TABLE cellSpacing=0 cellPadding=0 width="100%" border=0>
              <TBODY>
              <TR>
                <TD class=gary12 align=right width="32%" height=26><bean:message key="hire.email.address"/>：</TD>
                <TD align=left width="68%"><INPUT class=input01 id=loginName 
                  name=loginName maxlength=45 ></TD></TR>
              <TR>
                <TD class=gary12 align=right height=26><bean:message key="label.mail.password"/>：</TD>
                <TD align=left><INPUT class=input01 id=password type=password 
                  name=password maxlength=8  ></TD></TR>
                  <TD align="center" colspan="2">
                            <input type="checkbox" name="remenber" value="1" id="remenberme"/><font class='FontStyle'>记住我&nbsp;&nbsp;</font><a href='javascript:getPasswordZP("<%=dbName%>","username","userpassword");'>忘记密码？</a></TD>
                            </TR>
                           
              <TR>
                <TD align=middle colSpan=2 height=35>
                  <A href="javascript:login()">
                  <IMG  src="/images/dl.gif"   border=0>
                  </A>
                   <logic:equal value="0" name="employPortalForm" property="isDefinitinn">
						                  <A href="/hire/employNetPortal/search_zp_position.do?b_register=register" >
						                  </logic:equal>
						                    <logic:equal value="1" name="employPortalForm" property="isDefinitinn">
						                  <A href="/hire/employNetPortal/search_zp_position.do?br_license=license" >
						                  </logic:equal>
                  <IMG  src="/images/zc.gif"   border=0>
                  </A>
                  
                  </TD></TR></TBODY></TABLE>
            <TABLE cellSpacing=0 cellPadding=0 width="95%" border=0>
              <TBODY>
              <TR>
                <TD>
                  <DIV id=dv></DIV></TD></TR>
              <TR>
                <TD class=l12>
               <bean:message key="hire.welcome.into"/>${employPortalForm.masterName}<bean:message key="hire.net.info"/>  
                
                </TD></TR></TBODY></TABLE></TD></TR></TBODY></TABLE></TD></TR>
  <TR>
    <TD><IMG height=10 src="/images/app_d.gif" 
width=389></TD></TR></TBODY></TABLE>


</html:form>
</BODY>
</html>