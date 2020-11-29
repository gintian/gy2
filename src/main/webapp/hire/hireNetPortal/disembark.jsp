<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm"%>
<html>
<head>
<LINK 
href="/css/hireNetStyle.css" type=text/css rel=stylesheet>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/hire/hireNetPortal/employNetPortal.js"></script>
</head>
<%
EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
			String dbName=employPortalForm.getDbName();
 %>
<BODY leftMargin=0 topMargin=0 onKeyDown="return pf_ChangeFocusmark();"  ><BR><BR><BR><BR><BR><BR><BR><BR>
<html:form action="/hire/hireNetPortal/search_zp_position"> 
 
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
                <td width="10%">&nbsp;&nbsp;</td>
                 <TD class='user_input_back' width="22%" align="right" height=26><font class='FontStyle'><bean:message key="hire.email.address"/>：</font></TD>
			     <TD class='user_input_back' width="50%" align="left"><INPUT class=s_input id=loginName  name=loginName size="20"></TD>
                  <td width="18%">&nbsp;&nbsp;</td>
                  </TR>
              <TR>
              <td width="10%">&nbsp;&nbsp;</td>
                <TD class='user_input_back' width="22%" align="right" height=26><font class='FontStyle'><bean:message key="label.mail.password"/>：</font></TD>
			 <TD class='user_input_back' width="50%" align="left"><INPUT class=s_input id=password type=password name=password size="20"></TD>
			 <td width="18%">&nbsp;&nbsp;</td>
            </TR>
            <tr><td colspan="4">&nbsp;</td></tr>
             <TR>
						              <td width="10%">&nbsp;</td>
						                <TD  width="22%" height=26>&nbsp;</TD>
						                <TD width="50%" align=right><img style="cursor:hand;" src="/images/zp_logon.gif" title="登录"  onclick='login();'/></TD>
						                <td width="18%">&nbsp;</td>
						                  </tr>
						                    <TR> 
						              <td colspan='4' align="center" >
						              <IMG border= '0' height=1  src="/images/l_8_T.gif" width=140 >
						              </td>
						              </TR>
                         
                          <tr>
						                  <td width="10%">&nbsp;</td>
						                  <td colspan='2' align="left">
						                  <logic:equal value="0" name="employPortalForm" property="isDefinitinn">
						                   <a href='javascript:T_BUTTON();'><font class='zp_zc_fontstyle'>注册</font></a>
						                  </logic:equal>
						                    <logic:equal value="1" name="employPortalForm" property="isDefinitinn">
						                   <a href='javascript:TR_BUTTON();'><font class='zp_zc_fontstyle'>注册</font></a>
						                  </logic:equal>|
						                <a href='javascript:getPasswordZPnew("<%=dbName%>","username","userpassword");'><font class="zp_zc_fontstyle">找回密码</font></a>
						                  </TD>
						                <td width="18%">&nbsp;</td>
						                  </TR>
						                       
						                  <TR> 
						              <td colspan='4' align="left" >
						              &nbsp;
						              </td>
						              </TR>  
             </TBODY></TABLE>
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