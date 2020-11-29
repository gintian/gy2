<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/selfservice/propose/addconsulant">
      <table width="500" border="0" cellpadding="0" cellspacing="0" class="ftable" align="center" style="margin-top: 6px;">
          <tr height="20">
       		<td align="left" colspan="2" class="TableRow"><bean:message key="lable.consult.repair"/>&nbsp;</td>             	      
          </tr> 
          <tr class="list3">
    	      <td align="right" nowrap valign="top"><bean:message key="column.submit.consult"/></td>
    	      <td align="left"  nowrap>
    	      	<%--文本域可以拉伸   添加禁止拉伸样式 resize bug 34943 wangb 20180226--%>
    	      	<html:textarea name="consulantForm" property="consulantvo.string(ccontent)" cols="80" style="font-size:14px;resize:none;" rows="20"/>
              </td>
          </tr> 
          <tr class="list3">
            <td align="center" colspan="2" style="height:35px;">
         	    <hrms:submit styleClass="mybutton" property="b_save" onclick="document.consulantForm.target='_self';validate('R','consulantvo.string(ccontent)','咨询内容');return (document.returnValue && ifqrbc());">
            		<bean:message key="button.save"/>
	 	        </hrms:submit>
		        <html:reset styleClass="mybutton" property="reset"><bean:message key="button.clear"/></html:reset>	 	
         	    <hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	        </hrms:submit>            
            </td>
          </tr>          
      </table>
</html:form>
