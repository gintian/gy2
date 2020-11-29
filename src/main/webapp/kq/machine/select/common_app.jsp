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
   var date_desc;
   /*只有一个库时,对库进行隐藏*/
   
   
   function setSelectValue()
   {
     if(date_desc)
     {
       date_desc.value=$F('date_box');
       Element.hide('date_panel');   
     }
   }
   
   function showDateSelectBox(srcobj)
   {
       //if(event.button==2)
       //{
          date_desc=srcobj;
          Element.show('date_panel');   
          var pos=getAbsPosition(srcobj);
	  with($('date_panel'))
	  {
	        style.position="absolute";
    		style.posLeft=pos[0]-1;
		style.posTop=pos[1]-1+srcobj.offsetHeight;
		style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
          }                 
       //}
   }
   function getKqCalendarVar(){
     var request=new Request({method:'post',asynchronous:false,onSuccess:setkqcalendar,functionId:'15388800008'});
    
   }
   function setkqcalendar(outparamters)
   {
      weeks=outparamters.getValue("weeks");  
      feasts=outparamters.getValue("feasts"); 
      turn_dates=outparamters.getValue("turn_dates"); 
      week_dates=outparamters.getValue("week_dates");  
   }
function appQuery() {
	var selectObj = $("Slike").checked;
	if(selectObj)
		$("Hlike").value="1";
	repairKqCardFrom.action="/kq/machine/select/selectfiled.do?b_query=link";
    // repairKqCardFrom.target="mil_body";
	repairKqCardFrom.submit();  
	returnValue="ok";
	window.close();
}

function backfield(){
	repairKqCardFrom.action="/kq/machine/select/selectfiled.do?b_init=link";
    // repairKqCardFrom.target="mil_body";
	repairKqCardFrom.submit();  
}
   
</script>
<base id="mybase" target="_self">
<html:form action="/kq/machine/select/selectfiled"><br/>
  <table width="550" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!--  <td width=1 valign="top" class="tableft1"></td>
       		<td width=130 align=center class="tabcenter">申请查询</td>   
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="550"></td> --> 
       		<td align="left" class="TableRow">&nbsp;&nbsp;简单查询</td>      		           	      
          </tr> 
          <tr>
            <td colspan="1" class="framestyle9" valign="top">
            <table border="0"  cellspacing="0" width="70%" class="ListTable"  cellpadding="0" align="center">
            			<tr><td style="height:10px;"></td></tr>
                      <tr>
                	      <td align="center" nowrap class="TableRow"><bean:message key="label.query.logic"/></td>
                              <td align="center" nowrap class="TableRow"><bean:message key="label.query.field"/></td>
                	      <td align="center" nowrap class="TableRow"><bean:message key="label.query.relation"/></td>
                	      <td align="center" nowrap class="TableRow"><bean:message key="label.query.value"/></td>
                      </tr>                       
            <logic:iterate id="element" name="repairKqCardFrom"  property="factorlist" indexId="index"> 
                      <tr>       
			  <td align="center" class="RecordRow" nowrap >
                             <%
                             	if(i!=0)
                             	{
                             %>
                               <html:select name="repairKqCardFrom" property='<%="factorlist["+index+"].log"%>' size="1">
                                  <html:optionsCollection property="logiclist" value="dataValue" label="dataName"/>                                  
                               </html:select>
                             <%
                               }else{
                               %>
                               &nbsp;
                             <%  }
                             %>
                          </td>
                          <td align="center" class="RecordRow" nowrap >
                             <bean:write name="element" property="hz" />&nbsp;
                          </td>  
                          <td align="center" class="RecordRow" nowrap >
                               <html:select name="repairKqCardFrom" property='<%="factorlist["+index+"].oper"%>' size="1">
                                  <html:optionsCollection property="operlist" value="dataValue" label="dataName"/>                                   
                               </html:select>
                          </td>                                                  
                          <!--日期型 -->                            
                          <logic:equal name="element" property="fieldtype" value="D">
                            <td align="left" class="RecordRow" nowrap>                
				<html:text name="repairKqCardFrom" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="10" styleClass="text4" ondblclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates);' onblur="Element.hide('date_panel');"/>
                            </td>                           
                          </logic:equal>
                          <!--备注型 -->                              
                          <logic:equal name="element" property="fieldtype" value="M">
                            <td align="left" class="RecordRow" nowrap>                
                               <html:text name="repairKqCardFrom" property='<%="factorlist["+index+"].value"%>' size="30" maxlength='<%="factorlist["+index+"].itemlen"%>' styleClass="text4"/>                               
                            </td>                           
                          </logic:equal>
                          <!--字符型 -->                                                    
                          <logic:equal name="element" property="fieldtype" value="A">
                            <td align="left" class="RecordRow" nowrap>
                              <logic:notEqual name="element" property="codeid" value="0">
                                <html:hidden name="repairKqCardFrom" property='<%="factorlist["+index+"].value"%>' />                               
                                <html:text name="repairKqCardFrom" property='<%="factorlist["+index+"].hzvalue"%>' size="30" maxlength="50" styleClass="text4" onchange="fieldcode(this,1)"/> &nbsp; <img src="/images/code.gif" align="middle" onclick='openInputCodeDialogOrgInputPos("${element.codeid}","<%="factorlist["+index+"].hzvalue"%>","${repairKqCardFrom.orgparentcode}","1");'/>
                              </logic:notEqual>                              
                              <logic:equal name="element" property="codeid" value="0">
                                <!--考勤日期-->
                                <logic:equal name="element" property="fieldname" value="q03z0">
                                  <html:text name="repairKqCardFrom" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="${element.itemlen}" styleClass="text4"  onfocus="setday(this);"/>                               
                                </logic:equal>
                                <!--人员库-->
                                <logic:equal name="element" property="fieldname" value="nbase">
                                   <html:select name="repairKqCardFrom" property='<%="factorlist["+index+"].value"%>' size="1" styleClass="text4">
                                       <html:optionsCollection property="dblist" value="dataValue" label="dataName"/>
                                   </html:select>                                
                                </logic:equal>
                                <!--其他-->
                                <logic:notEqual name="element" property="fieldname" value="q03z0">
                                    <logic:notEqual name="element" property="fieldname" value="nbase">
                                      <html:text name="repairKqCardFrom" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="${element.itemlen}" styleClass="text4"/>                               
                                    </logic:notEqual>
                                </logic:notEqual>                                
                              </logic:equal>                               
                            </td>                           
                          </logic:equal> 
                          <!--数据值-->                            
                          <logic:equal name="element" property="fieldtype" value="N">
                            <td align="left" class="RecordRow" nowrap>                
                               <html:text name="repairKqCardFrom" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="${element.itemlen}" styleClass="text4"/>                               
                            </td>                           
                          </logic:equal>                           
                       </tr>        
                       <%
                       	++i;
                       %>                    
                       </logic:iterate>                     
                       <tr>
                	  <td align="center" nowrap class="RecordRow" colspan="4">
                	     <input type="hidden"  name="like" id="Hlike" value="0">
                	     <input type="checkbox" id="Slike">&nbsp;<bean:message key="label.query.like"/>
			  </td>
                        </tr> 
                      <tr><td height="15" ></td></tr>                                      
	       </table>	        	
            <!-- 
          <tr class="list3">
            <td align="center" colspan="1">
		&nbsp           
            </td>
          </tr>       -->      
          <tr class="list3">
            <td colspan="1" height="35px;" align="center">
				<input type="button" name="btnreturn" value='<bean:message key="button.query"/>' onclick="appQuery()" class="mybutton">
							
				 <input type="button" name="btnreturn" value='<bean:message key="button.query.pre"/>' onclick="backfield();" class="mybutton">						      
				<html:reset styleClass="mybutton">
					<bean:message key="button.clear"/>
				</html:reset> 	
            </td>
          </tr>  
  </table>
                     
</html:form>
<script language="javascript">
   Element.hide('date_panel');     
</script>