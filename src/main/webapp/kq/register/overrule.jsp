<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/kq/register/daily_registerdata">
<table align="center" width="80%" valign='middle' height="300"  border="0" cellspacing="0" cellpadding="0" class="ListTable">
  <tr>
     <td valign='middle' align="center">
     <logic:equal name="dailyRegisterForm" property="one_vo.string(overrule)" value="">	 
                    暂无意见！
     </logic:equal>
     <logic:notEqual name="dailyRegisterForm" property="one_vo.string(overrule)" value="">	 
          <table border="0" width="100%" cellspacing="0" cellpadding="0" height="270" align="center" class="ListTable">
           <tr>
             <td class="TableRow" style="font-size:14px;" height=24 colspan="2" align="center">备注说明</td>
           </tr>
           <tr>
             <td align="center" width="25%" height="30" class="RecordRow" >
               姓名:
             </td>
             <td align="left" class="RecordRow" >
                &nbsp;<bean:write  name="dailyRegisterForm" property="one_vo.string(a0101)" filter="true"/>
             </td>
           </tr>
            <tr>
             <td align="center" height="30" class="RecordRow" >
               部门:
             </td>
             <td align="left" class="RecordRow" >
             <hrms:codetoname codeid="UM" name="dailyRegisterForm" codevalue="one_vo.string(e0122)" codeitem="codeitem" scope="session"/>  	      
          	   &nbsp;<bean:write name="codeitem" property="codename" /> 
               
             </td>
           </tr>
           <tr>
             <td align="center" height="30" class="RecordRow" >
               岗位:
             </td>
             <td align="left" class="RecordRow" >
               <hrms:codetoname codeid="@K" name="dailyRegisterForm" codevalue="one_vo.string(e01a1)" codeitem="codeitem" scope="session"/>  	      
          	    &nbsp;<bean:write name="codeitem" property="codename" /> 
             </td>
           </tr>
           <tr>
             <td align="center" class="RecordRow" >
               说明:
             </td>
             <td align="left" class="RecordRow" >
               <bean:write  name="dailyRegisterForm" property="one_vo.string(overrule)" filter="false"/>
             </td>
           </tr>
        </table>
     </logic:notEqual>
        
     </td>     
  </tr>
  <tr>
    <td align="center">
      <input type="button" name="btnreturn" value='<bean:message key="button.close"/>' onclick="window.close();" class="mybutton">						      
    </td>
  </tr>
</table>
</html:form>