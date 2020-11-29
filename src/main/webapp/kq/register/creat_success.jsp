<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/kq/register/daily_registerdata">
<table align="center" width="80%" valign='middle'height="300">
  <tr>
     <td valign='middle'>
         <table border="1" width="67%" cellspacing="0" cellpadding="4" style="border-collapse: collapse" bgcolor="#F7FAFF" height="87" align="center">
           <tr>
             <td bgcolor="#3399FF" style="font-size:12px;color:#ffffff" height=24><bean:message key="kq.register.creat1.success"/>...</td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center><bean:message key="kq.register.creat2.success"/>...
            </td>
          </tr>
          <tr>
             <td style="font-size:12px;line-height:200%" align=center>
             <hrms:submit styleClass="mybutton" property="b_search">
                  <bean:message key="kq.register.kqduration.ok"/>
	     </hrms:submit>
            </td>
          </tr>
        </table>
     </td>
  </tr>
</table>
</html:form>