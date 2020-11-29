<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<%
  int i=0;
  int j=0;
%>
<script language="javascript">
  function getKqCalendarVar()
   {
     var request=new Request({method:'post',asynchronous:false,onSuccess:setkqcalendar,functionId:'15388800008'});
    
   }
 function setkqcalendar(outparamters)
   {
      weeks=outparamters.getValue("weeks");  
      feasts=outparamters.getValue("feasts"); 
      turn_dates=outparamters.getValue("turn_dates"); 
      week_dates=outparamters.getValue("week_dates");  
   }
  function kqQuery()
   {
      collectStatForm.action="/general/template/goabroad/collect/searchstat.do?b_select=link";
      collectStatForm.submit();
      window.returnValue = "ok";
      window.close();
   }
   
   function backup(){
   	  collectStatForm.action="/general/template/goabroad/collect/searchstat.do?b_selinit=link";
      collectStatForm.submit(); 
   }
</script>
<html:form action="/general/template/goabroad/collect/searchstat">
  <br>
  <br>
  <table width="550" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<td colspan="4" align="left" class="TableRow"><bean:message key="infor.menu.squery"/></td>         		           	      
          </tr>         
          <tr>
            <td colspan="4" class="framestyle9">
               <br>
               <table border="0" cellspacing="0" width="70%" class="ListTable"  cellpadding="0" align="center" valign="top">
                      <tr>
                            
                	      <td align="center" nowrap class="TableRow"><bean:message key="label.query.logic"/></td>
                              <td align="center" nowrap class="TableRow"><bean:message key="label.query.field"/></td>
                	      <td align="center" nowrap class="TableRow"><bean:message key="label.query.relation"/></td>
                	      <td align="center" nowrap class="TableRow"><bean:message key="label.query.value"/></td>
                      	
                      </tr>          
                     <logic:iterate id="element" name="collectStatForm"  property="factorlist" indexId="index"> 
                       <tr>
                       <td align="center" class="RecordRow" nowrap>
                       <%
                             	if(i!=0)
                             	{
                             %>
                       <html:select name="collectStatForm" property='<%="factorlist["+index+"].log"%>' size="1">
                                  <html:optionsCollection property="logiclist" value="dataValue" label="dataName"/>                                  
                               </html:select>
                               <%
                               }
                               %>
                       </td>
                       <td align="center" class="RecordRow" nowrap> 
                       <bean:write name="element" property="hz"/></td>
                       <td align="center" class="RecordRow" nowrap>
                        <html:select name="collectStatForm" property='<%="factorlist["+index+"].oper"%>' size="1">
                                  <html:optionsCollection property="operlist" value="dataValue" label="dataName"/>                                   
                               </html:select></td>
                       
                       <logic:equal name="element" property="fieldtype" value="D">
                            <td align="left" class="RecordRow" nowrap>                
				<html:text name="collectStatForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="10" styleClass="text4" ondblclick="getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates);" />
                            </td>                           
                          </logic:equal>
                          <!--备注型 -->                              
                          <logic:equal name="element" property="fieldtype" value="M">
                            <td align="left" class="RecordRow" nowrap>                
                               <html:text name="collectStatForm" property='<%="factorlist["+index+"].value"%>' size="30" styleClass="text4" maxlength='<%="factorlist["+index+"].itemlen"%>' />                               
                            </td>                           
                          </logic:equal>
                          <!--字符型 -->                                                    
                          <logic:equal name="element" property="fieldtype" value="A">
                            <td align="left" class="RecordRow" nowrap>
                              <logic:notEqual name="element" property="codeid" value="0">
                                <html:hidden name="collectStatForm" property='<%="factorlist["+index+"].value"%>' styleClass="text4"/>                               
                                <html:text name="collectStatForm" property='<%="factorlist["+index+"].hzvalue"%>' size="30" maxlength="50" styleClass="text4" onchange="fieldcode(this,1)"/><img src="/images/code.gif" onclick="openCondCodeDialog('${element.codeid}','<%="factorlist["+index+"].hzvalue"%>');"/>
                              </logic:notEqual>                              
                              <logic:equal name="element" property="codeid" value="0">
                                <!--考勤日期-->
                                <logic:equal name="element" property="fieldname" value="q03z0">
                                  <html:text name="collectStatForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="${element.itemlen}" styleClass="text4"  onfocus="setday(this);" readonly="true"/>                               
                                </logic:equal>
                                <!--人员库-->
                                <logic:equal name="element" property="fieldname" value="nbase">
                                   <html:select name="collectStatForm" property='<%="factorlist["+index+"].value"%>' size="1" styleClass="text4">
                                       <html:optionsCollection property="dblist" value="dataValue" label="dataName"/>
                                   </html:select>                                
                                </logic:equal>
                                <!--其他-->
                                <logic:notEqual name="element" property="fieldname" value="q03z0">
                                    <logic:notEqual name="element" property="fieldname" value="nbase">
                                      <html:text name="collectStatForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="${element.itemlen}" styleClass="text4"/>                               
                                    </logic:notEqual>
                                </logic:notEqual>                                
                              </logic:equal>                               
                            </td>                           
                          </logic:equal> 
                          <!--数据值-->                            
                          <logic:equal name="element" property="fieldtype" value="N">
                            <td align="left" class="RecordRow" nowrap="nowrap">             
                               <html:text name="collectStatForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="${element.itemlen}" styleClass="text4"/>                               
                            </td>                           
                          </logic:equal> 
                                                
                       </tr>        
                       <%
                       	++i;
                       %>                    
                       </logic:iterate> 
                      <tr><td height="15" colspan="4"></td></tr>                                      
	       </table>
	       </td>	        	
           </tr>   
          <tr class="list3">
            <td align="left" colspan="2">
		<html:checkbox name="collectStatForm" property="like" value="1" >&nbsp;<bean:message key="label.query.like"/></html:checkbox>&nbsp;&nbsp;       
            </td>
          </tr>            
          <tr class="list3">
            <td colspan="4">		
				<input type="button" name="btnreturn" value='<bean:message key="button.query"/>' onclick="kqQuery();" class="mybutton">							
				 <input type="button" name="btnreturn" value='<bean:message key="button.query.pre"/>' onclick="backup();" class="mybutton">						      
				<html:reset styleClass="mybutton">
					<bean:message key="button.clear"/>
				</html:reset> 	
            </td>
          </tr>  
  </table>
</html:form>