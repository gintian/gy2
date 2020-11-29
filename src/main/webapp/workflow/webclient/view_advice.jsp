<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>


<html:form action="/workflow/webclient/view_advice">
      <table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;[<bean:write name="nodeForm" property="advice_vo.string(actor_id)" filter="true"/>]审批意见&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>-->   
       		<td align=center class="TableRow">&nbsp;[<bean:write name="nodeForm" property="advice_vo.string(actor_id)" filter="true"/>]审批意见&nbsp;</td>          	      
          </tr>      
          <tr>
            <td class="framestyle9">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">    
                   <tr class="list3">
             	      <td align="right" nowrap>处理意见:</td>
             	      <td align="left"  nowrap>
          			<hrms:codetoname codeid="YY" name="nodeForm" codevalue="advice_vo.string(advice_type)" codeitem="codeitem"/>&nbsp;  	      
          			<bean:write name="codeitem" property="codename" />&nbsp;				
          
                       </td>
                   </tr>                    
                   <tr class="list3">
             	      <td align="right" wrap>批示:</td>
             	      <td align="left"  wrap>
          			<bean:write name="nodeForm" property="advice_vo.string(advice_value)" filter="true"/>&nbsp;
                       </td>
                   </tr>

               </table>
            </td>
          </tr>
          <tr class="list3">
            <td align="center" style="height:35px;">
 			<input type="button" name="br_return" value="返回" class="mybutton" onclick="history.back();">
            </td>
          </tr>                
      </table>
 
</html:form>
