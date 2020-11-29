<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%
  int i=0;
%>

<style>

.textInterface 
{
	BACKGROUND-COLOR:transparent;
	font-size: 12px;
	height:22;
	border: 1pt solid #94B6E6;
}
</style>
<script language="JavaScript"src="../../../js/showModalDialog.js"></script>
<html:form action="/selfservice/performance/hquery_interface">
  <br>
  <br>
  <br>   
  <table width="700" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!--<td width=1 valign="top" class="tableft1"></td>
       		
       		<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="gz.bankdisk.querycondition"/>&nbsp;</td>
                                                          		    
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="700"></td>  -->    
       		<td align=center class="TableRow">&nbsp;<bean:message key="gz.bankdisk.querycondition"/>&nbsp;</td>          	      
          </tr> 
          <tr>
            <td class="framestyle9">
               <table border="0"  cellspacing="0" width="70%" class="ListTable"  cellpadding="2" align="center">
               
                     <tr>
                      <td><td colspan="4">&nbsp;</td></tr>
                      <tr><td colspan="4"> 
                      <table border="0"  cellspacing="0" width="70%" class="ListTable1"  cellpadding="2" align="center">
                      <tr>
                      
                	      <td align="center" nowrap class="TableRow"><bean:message key="label.query.logic"/></td>          	      
                	      <td align="center" nowrap class="TableRow"><bean:message key="label.query.field"/></td>
                	      <td align="center" nowrap class="TableRow"><bean:message key="label.query.relation"/></td>
                	      <td align="center" nowrap class="TableRow"><bean:message key="label.query.value"/></td>
                      	
                      </tr> 
                                
                      <logic:iterate id="element" name="conditionQueryForm"  property="factorlist" indexId="index"> 
                      <tr>       
                            
                           <td align="center" class="RecordRow" nowrap >
                             <%
                             	if(i!=0)
                             	{
                             %>
                 	       <hrms:optioncollection name="conditionQueryForm" property="logiclist" collection="list"/>
                               <html:select name="conditionQueryForm" property='<%="factorlist["+index+"].log"%>' size="1">
                                  <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                               </html:select>
                             <%
                               }
                             %>
                          </td>
                                                  
                          <td align="center" class="RecordRow" nowrap >
                             <bean:write name="element" property="hz" />&nbsp;
                          </td>  
                          <td align="center" class="RecordRow" nowrap >
                 	       <hrms:optioncollection name="conditionQueryForm" property="operlist" collection="list"/>
                               <html:select name="conditionQueryForm" property='<%="factorlist["+index+"].oper"%>' size="1">
                                  <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                               </html:select>
                          </td>                                                  
                          <!--日期型 -->                            
                          <logic:equal name="element" property="fieldtype" value="D">
                            <td align="left" class="RecordRow" nowrap>                
								<html:text name="conditionQueryForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="10" styleClass="textInterface common_border_color"/>
                            </td>                           
                          </logic:equal>
                          <!--备注型 -->                              
                          <logic:equal name="element" property="fieldtype" value="M">
                            <td align="left" class="RecordRow" nowrap>                
                               <html:text name="conditionQueryForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength='<%="factorlist["+index+"].itemlen"%>' styleClass="textInterface common_border_color"/>                               
                            </td>                           
                          </logic:equal>
                          <!--字符型 -->                                                    
                          <logic:equal name="element" property="fieldtype" value="A">
                            <td align="left" class="RecordRow" nowrap>
                              <logic:notEqual name="element" property="codeid" value="0">
                                <html:hidden name="conditionQueryForm" property='<%="factorlist["+index+"].value"%>' styleClass="textInterface common_border_color"/>                               
                                <html:text name="conditionQueryForm" property='<%="factorlist["+index+"].hzvalue"%>' size="30" maxlength="50" styleClass="textInterface common_border_color" onchange="fieldcode(this,1)"/>
                                <img src="/images/code.gif" onclick='openCondCodeDialog("${element.codeid}","<%="factorlist["+index+"].hzvalue"%>");'/>
                              </logic:notEqual> 
                              <logic:equal name="element" property="codeid" value="0">
                                <html:text name="conditionQueryForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="${element.itemlen}" styleClass="textInterface common_border_color"/>                               
                              </logic:equal>                               
                            </td>                           
                          </logic:equal> 
                          <!--数据值-->                            
                          <logic:equal name="element" property="fieldtype" value="N">
                            <td align=left class="RecordRow" nowrap>                
                              <html:text name="conditionQueryForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="${element.itemlen}" styleClass="textInterface common_border_color"/>                               
                            </td>                           
                          </logic:equal>                           
                       </tr>        
                       <%
                       	++i;
                       %>                    
                       </logic:iterate>
                      
                      <!-- 查询定义才出现此选项 -->
                      <logic:notEqual name="conditionQueryForm" property="query_type" value="3">                           
                        <tr>
                	   <td align="center" nowrap class="RecordRow" colspan="4">
                	   <html:checkbox name="conditionQueryForm" property="like" value="1" >&nbsp;<bean:message key="label.query.like"/></html:checkbox>&nbsp;&nbsp;&nbsp;&nbsp;<html:checkbox name="conditionQueryForm" property="history" value="1" >&nbsp;<bean:message key="label.query.history"/></html:checkbox>
                		<logic:equal name="conditionQueryForm" property="flag"  value="2">
                			<logic:equal name="conditionQueryForm" property="objectType"  value="2">
                				  <html:checkbox name="conditionQueryForm" property="accordByDepartment" value="1" >&nbsp;<bean:message key="jx.plan.accordByDepartment"/></html:checkbox>
                			</logic:equal>
                		</logic:equal>
			   </td>
                        </tr> 
                      </logic:notEqual> 
                      
                      </table>
                      </td>
                      </tr>
                      <tr><td height="15" colspan="4"></td></tr>                                      
	       </table>	            	
            </td>
          </tr>
                 
          <tr class="list3">
            <td align="center" style="height:35px;">
       	                 
                 <hrms:submit styleClass="mybutton"  property="b_query" >
                    <bean:message key="button.query"/>
	         </hrms:submit>             	                     	         
               <hrms:submit styleClass="mybutton" property="br_return2">
            		<bean:message key="button.query.pre"/>
	       </hrms:submit>
	       	       
               <html:reset styleClass="mybutton">
                    <bean:message key="button.clear"/>
	       </html:reset> 	
            </td>
          </tr>  
  </table>
 
</html:form>
<script>
	//alert('${conditionQueryForm.objectType}');
	//	alert('${conditionQueryForm.flag}');
		//	alert('${conditionQueryForm.query_type}');
</script>