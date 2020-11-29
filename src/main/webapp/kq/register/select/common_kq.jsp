<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<%@ page import="com.hjsj.hrms.actionform.kq.register.DailyRegisterForm" %>
<%
  int i=0;
  int j=0;
   DailyRegisterForm dailyRegisterForm=(DailyRegisterForm)session.getAttribute("dailyRegisterForm");
   String strus=dailyRegisterForm.getSelectsturs();   
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
   function kqQuery()
   {
      dailyRegisterForm.action="/kq/register/select/selectfiled.do?b_query=link";      
      dailyRegisterForm.submit();          
   }
   function reValue(strus)
   {
      if(strus=="1")
      {
         window.returnValue="${dailyRegisterForm.selectResult}";
         window.close();
      }
   }
   reValue("<%=strus%>");
</script>
<base id="mybase" target="_self">
<html:form action="/kq/register/select/selectfiled">
  <br>
  <table width="550" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft1"></td>
       		<td width=130 align=center class="tabcenter">条件设置</td>   
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>--> 
       		<td colspan="4" align=center class="TableRow">条件设置</td>        		           	      
          </tr>         
          <tr>
            <td  class="framestyle9">
               <br>
               <table border="0"  cellspacing="0" width="70%" class="ListTable"  cellpadding="2" align="center" valign="top">
                      <tr>
                            
                	      <td align="center" nowrap class="TableRow"><bean:message key="label.query.logic"/></td>
                              <td align="center" nowrap class="TableRow"><bean:message key="label.query.field"/></td>
                	      <td align="center" nowrap class="TableRow"><bean:message key="label.query.relation"/></td>
                	      <td align="center" nowrap class="TableRow"><bean:message key="label.query.value"/></td>
                      	
                      </tr>          
                     <logic:iterate id="element" name="dailyRegisterForm"  property="factorlist" indexId="index"> 
                       <tr>
                       <td align="center" class="RecordRow" nowrap>
                       <%
                             	if(i!=0)
                             	{
                             %>
                       <html:select name="dailyRegisterForm" property='<%="factorlist["+index+"].log"%>' size="1">
                                  <html:optionsCollection property="logiclist" value="dataValue" label="dataName"/>                                  
                               </html:select>
                               <%
                               }
                               %>
                       </td>
                       <td align="center" class="RecordRow" nowrap> 
                       <bean:write name="element" property="hz"/></td>
                       <td align="center" class="RecordRow" nowrap>
                        <html:select name="dailyRegisterForm" property='<%="factorlist["+index+"].oper"%>' size="1">
                                  <html:optionsCollection property="operlist" value="dataValue" label="dataName"/>                                   
                               </html:select></td>
                       
                       <logic:equal name="element" property="fieldtype" value="D">
                            <td align="left" class="RecordRow" nowrap>                
				<html:text name="dailyRegisterForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="10" styleClass="text4" ondblclick="showDateSelectBox(this);" onblur="Element.hide('date_panel');"/>
                            </td>                           
                          </logic:equal>
                          <!--备注型 -->                              
                          <logic:equal name="element" property="fieldtype" value="M">
                            <td align="left" class="RecordRow" nowrap>                
                               <html:text name="dailyRegisterForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength='<%="factorlist["+index+"].itemlen"%>' styleClass="text4"/>                               
                            </td>                           
                          </logic:equal>
                          <!--字符型 -->                                                    
                          <logic:equal name="element" property="fieldtype" value="A">
                            <td align="left" class="RecordRow" nowrap>
                              <logic:notEqual name="element" property="codeid" value="0">
                                <html:hidden name="dailyRegisterForm" property='<%="factorlist["+index+"].value"%>' styleClass="text4"/>                               
                                <html:text name="dailyRegisterForm" property='<%="factorlist["+index+"].hzvalue"%>' size="30" maxlength="50" styleClass="text4" onchange="fieldcode(this,1)"/><img src="/images/code.gif" align="middle" onclick='openCondCodeDialog("${element.codeid}","<%="factorlist["+index+"].hzvalue"%>");'/>
                              </logic:notEqual>                              
                              <logic:equal name="element" property="codeid" value="0">
                                <!--考勤日期-->
                                <logic:equal name="element" property="fieldname" value="q03z0">
                                  <html:text name="dailyRegisterForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="${element.itemlen}" styleClass="text4"  onfocus="setday(this);" readonly="true"/>                               
                                </logic:equal>
                                <!--人员库-->
                                <logic:equal name="element" property="fieldname" value="nbase">
                                   <html:select name="dailyRegisterForm" property='<%="factorlist["+index+"].value"%>' size="1" styleClass="text4">
                                       <html:optionsCollection property="dblist" value="dataValue" label="dataName"/>
                                   </html:select>                                
                                </logic:equal>
                                <!--其他-->
                                <logic:notEqual name="element" property="fieldname" value="q03z0">
                                    <logic:notEqual name="element" property="fieldname" value="nbase">
                                      <html:text name="dailyRegisterForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="${element.itemlen}" styleClass="text4"/>                               
                                    </logic:notEqual>
                                </logic:notEqual>                                
                              </logic:equal>                               
                            </td>                           
                          </logic:equal> 
                          <!--数据值-->                            
                          <logic:equal name="element" property="fieldtype" value="N">
                            <td align="left" class="RecordRow" nowrap>                
                               <html:text name="dailyRegisterForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="${element.itemlen}" styleClass="text4"/>                               
                            </td>                           
                          </logic:equal> 
                                                
                       </tr>        
                       <%
                       	++i;
                       %>                    
                       </logic:iterate> 
                      <tr><td class="RecordRow" colspan="4" align="center">
                      	<html:checkbox name="dailyRegisterForm" property="like" value="1" >&nbsp;<bean:message key="label.query.like"/></html:checkbox>&nbsp;&nbsp;       
                      </td></tr> 
                      <tr>
                      	<td height="15px;">
                      	</td>
                      </tr>                                     
	       </table>
	       </td>	        	
           </tr>   
          <tr class="list3">
            <td align="center" colspan="4">
		
            </td>
          </tr>           
          <tr class="list3">
            <td  align="center" style="height:35px;">
				<input type="button" name="btnreturn" value='<bean:message key="button.query"/>' onclick="kqQuery();" class="mybutton">
				 <input type="submit" name="b_init" value='<bean:message key="button.query.pre"/>' onclick="history.back();" class="mybutton">						      
				<html:reset styleClass="mybutton">
					<bean:message key="button.clear"/>
				</html:reset> 	
            </td>
          </tr>  
  </table>
          <div id="date_panel">
   			<select name="date_box" multiple="multiple" size="10"  style="width:200" onchange="setSelectValue();">    
			    <option value="$YRS[10]">年限</option>
			    <option value="当年">当年</option>
			    <option value="当月">当月</option>
			    <option value="当天">当天</option>				    
			    <option value="今天">今天</option>
			    <option value="截止日期">截止日期</option>
                            <option value="1992.4.12">1992.4.12</option>	
                            <option value="1992.4">1992.4</option>	
                            <option value="1992">1992</option>			    
			    <option value="????.??.12">????.??.12</option>
			    <option value="????.4.12">????.4.12</option>
			    <option value="????.4">????.4</option>			    			    		    
                        </select>
          </div>      
</html:form>
<script language="javascript">
   Element.hide('date_panel');  
      
</script>