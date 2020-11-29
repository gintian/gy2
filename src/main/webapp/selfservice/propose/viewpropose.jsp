<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/selfservice/propose/viewpropose">
      <table width="500" border="0" cellpadding="0" cellspacing="0" align="center" style="margin-top:6px;">
          <tr height="20">
       		<!--td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="580"></td>    -->
       		<td align="left" colspan="4" class="TableRow"><bean:message key="label.suggest.box"/>&nbsp;</td>              	                  	      
          </tr> 
          <tr>
            <td colspan="4" class="framestyle3" width="500">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">     
                      <tr class="list3" height="200">
                	      <td align="right" nowrap valign="top"><bean:message key="column.submit.propose"/>:</td>
                	      <td align="left"  nowrap valign="top"  width="500" style="word-break:break-all">
                	        <%--当显示文字多了，不显示滚动条     bug 34941 wangb 20180226 --%>
                	        <div style="width:100%;height:220px;overflow:auto;"><bean:write  name="proposeForm" property="proposevo.string(scontent)" filter="false"/>&nbsp;</div>
                              </td>
                      </tr> 

                 </table>     
              </td>
          </tr>
          <tr>
            <td colspan="4" class="framestyle" width="500" style="border-top:none;">
               <table border="0" cellspacing="0"  class="DetailTable"  cellpadding="0">     
                      <tr class="list3" height="200">
                	      <td align="right" nowrap valign="top"><bean:message key="column.reply.content"/>:</td>
                	      <td align="left"  nowrap valign="top" width="500" style="word-break:break-all">
                	      	<%--当显示文字多了，不显示滚动条     bug 34941 wangb 20180226 --%>
                	        <div style="width:100%;height:220px;overflow:auto;"><bean:write  name="proposeForm" property="proposevo.string(rcontent)" filter="false"/>&nbsp;</div>
                          </td>
                      </tr> 

               </table>     
              </td>
          </tr>                                                 
          <tr class="list3">
            <td align="center" colspan="2" height="35px;">
         	<hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	</hrms:submit>            
            </td>
          </tr>          
      </table>
</html:form>
