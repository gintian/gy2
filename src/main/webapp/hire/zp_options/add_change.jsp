<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>

<html:form action="/hire/zp_options/add_change">
      <br>
      <br>
      <table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="label.zp_options.testprocess"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td> -->  
       		<td align=center class="TableRow">&nbsp;<bean:message key="label.zp_options.testprocess"/>&nbsp;</td>           	      
          </tr> 
          <tr>
            <td class="framestyle9">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">     
                      <tr class="list3">
                	      <td align="right" nowrap valign="top"><bean:message key="label.zp_resource.name"/>:</td>
                	      <td align="left"  nowrap>
                	      	<html:text name="testProcessForm" property="testProcessvo.string(name)" maxlength="30" size="20" styleClass="text"/>
                          </td>
                      </tr> 

                 </table>     
              </td>
          </tr>                                                            
          <tr class="list3">
            <td align="center" style="height:35px;">
         	<hrms:submit styleClass="mybutton" property="b_save" onclick="document.testProcessForm.target='_self';validate('R','testProcessvo.string(name)','名称');return (document.returnValue && ifqrbc());">
            		<bean:message key="button.save"/>
	 	</hrms:submit>	 	
         	<hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	</hrms:submit>            
            </td>
          </tr>          
      </table>
</html:form>
