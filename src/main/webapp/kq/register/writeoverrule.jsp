<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/kq/register/browse_registerdata">


<table align="center" width="80%" valign='middle'height="300">
  <tr>
     <td valign='middle'>
         <table border="1" width="100%" class="complex_border_color" cellspacing="0" cellpadding="4" style="border-collapse: collapse" bgcolor="#F7FAFF" height="270" align="center">
           <tr>
             <td bgcolor="#3399FF" class="common_background_color" style="font-size:14px;color:#000" height=24 colspan="2" align="center">审批意见信息</td>
           </tr>
           <tr>
             <td align="center" width="25%" height="30">
               姓名:
             </td>
             <td align="left">
                &nbsp;<bean:write  name="browseRegisterForm" property="one_vo.string(a0101)" filter="true"/>
             </td>
           </tr>
            <tr>
             <td align="center" height="30">
               部门:
             </td>
             <td align="left">
             <hrms:codetoname codeid="UM" name="browseRegisterForm" codevalue="one_vo.string(e0122)" codeitem="codeitem" scope="session"/>  	      
          	   &nbsp;<bean:write name="codeitem" property="codename" /> 
               
             </td>
           </tr>
           <tr>
             <td align="center" height="30">
               岗位:
             </td>
             <td align="left">
               <hrms:codetoname codeid="@K" name="browseRegisterForm" codevalue="one_vo.string(e01a1)" codeitem="codeitem" scope="session"/>  	      
          	    &nbsp;<bean:write name="codeitem" property="codename" /> 
             </td>
           </tr>
           <tr>
             <td align="center">
               意见:
             </td>
             <td align="left" valign="top">
               <bean:write name="browseRegisterForm" property="one_vo.string(overrule)" filter="false"/> 
              </td>
           </tr>
        </table>
     </td>     
  </tr>
  <tr>
    <td align="center">
      <input type="button" name="btnreturn" value='<bean:message key="button.close"/>' onclick="window.close();" class="mybutton">						      
    </td>
  </tr>
</table>
</html:form>