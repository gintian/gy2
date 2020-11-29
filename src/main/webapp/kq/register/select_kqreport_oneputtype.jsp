<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html:form action="/kq/register/select_kqreportdata">
<table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
    <tr height="20">
       <!--  <td width=10 valign="top" class="tableft"></td>
          <td width=130 align=center class="tabcenter">&nbsp;修改<bean:message key="kq.report.type"/>&nbsp;</td>
          <td width=10 valign="top" class="tabright"></td>
          <td valign="top" class="tabremain" width="500"></td> --> 
          <td  align=center class="TableRow">&nbsp;修改<bean:message key="kq.report.type"/>&nbsp;</td>            	      
   </tr> 
   <tr>
     <td  class="framestyle9">
       <br>
       <br>
         <table width="90%" border="0" cellspacing="1"  align="center" cellpadding="1">
           <tr> 
            <td align="center" nowrap>名称&nbsp;<html:text name="printKqInfoForm" property="report_name" size="50" styleClass="text"/>&nbsp; </td>
           </tr>
           <tr>           
            <td align="center">
            <br>
              <hrms:submit styleClass="mybutton" property="b_updateput">
                  <bean:message key="button.ok"/>
	       </hrms:submit>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            
               <input type="button" name="btnreturn" value='<bean:message key="kq.emp.button.return"/>' onclick="history.back();" class="mybutton">
            </td>         
          </tr>
        </table>
     </td>    
    </tr>    
  </table> 
</html:form>