<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/meizzDate_saveop.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<%@ page import="com.hjsj.hrms.actionform.kq.register.SelectKqFieldForm" %>
<%
  int i=0;
  int j=0;
  SelectKqFieldForm selectKqFieldForm=(SelectKqFieldForm)session.getAttribute("selectKqFieldForm");
  String strus=selectKqFieldForm.getSelectsturs();  
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
      //selectKqFieldForm.action="/kq/register/select/sum/selectfiled.do?b_query=link&action=kqquery.do&target=mil_body";
      //selectKqFieldForm.target="il_body";
      //selectKqFieldForm.submit();    
      selectKqFieldForm.action="/kq/register/select/sum/selectfiled.do?b_query=link";
      selectKqFieldForm.submit();     
   }
   function reValue(strus)
   {
      if(strus=="1")
      {
         window.returnValue="${selectKqFieldForm.selectResult}";
         window.close();
      }
   }
   reValue("<%=strus%>");
   function getdate(tt)
   {
     var valueS=tt.value;
     if(valueS!=null||valueS.length<=7)
       valueS=valueS.substr(0,7);
     tt.value=valueS;
   }
   function kqblack()
   {
      selectKqFieldForm.action="/kq/register/select/sum/selectfiled.do?b_init=link";
      selectKqFieldForm.submit();   
   }
</script>
<base id="mybase" target="_self">
<html:form action="/kq/register/select/sum/selectfiled">
  <br> 
  <table width="550" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!--  <td width=1 valign="top" class="tableft1"></td>
       		<td width=130 align=center class="tabcenter">条件设置</td>   
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="700"></td> --> 
       		<td  align=center class="TableRow">条件设置</td>       		           	      
          </tr> 
          <tr>
            <td  class="framestyle9"><br>
               <table border="0"  cellspacing="0" width="70%" class="ListTable1"  cellpadding="2" align="center">
                      <tr>
                            
                	      <td align="center" nowrap class="TableRow"><bean:message key="label.query.logic"/></td>
                              <td align="center" nowrap class="TableRow"><bean:message key="label.query.field"/></td>
                	      <td align="center" nowrap class="TableRow"><bean:message key="label.query.relation"/></td>
                	      <td align="center" nowrap class="TableRow"><bean:message key="label.query.value"/></td>
                      	      <html:hidden name="selectKqFieldForm" property="select_flag" styleClass="text"/>
                      	      <html:hidden name="selectKqFieldForm" property="duration" styleClass="text"/>
                      </tr>                       
                         <logic:iterate id="element" name="selectKqFieldForm"  property="factorlist" indexId="index"> 
                      <tr>       
                         <td align="center" class="RecordRow" nowrap>
                           <%
                             	if(i!=0)
                             	{
                             %>
                               <html:select name="selectKqFieldForm" property='<%="factorlist["+index+"].log"%>' size="1">
                                  <html:optionsCollection property="logiclist" value="dataValue" label="dataName"/>                                  
                               </html:select>
                             <%
                               }
                             %>
                         </td>
			  <td align="center" class="RecordRow" nowrap>
			     <bean:write name="element" property="hz" />
                         </td>
                         <td align="center" class="RecordRow" nowrap>
                           <html:select name="selectKqFieldForm" property='<%="factorlist["+index+"].oper"%>' size="1">
                                  <html:optionsCollection property="operlist" value="dataValue" label="dataName"/>                                   
                               </html:select>
                         </td>
                         <logic:equal name="element" property="fieldtype" value="D">
                            <td align="left" class="RecordRow" nowrap>                
				<html:text name="selectKqFieldForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="10" styleClass="text4" ondblclick="showDateSelectBox(this);" onblur="Element.hide('date_panel');"/>
                            </td>                           
                          </logic:equal>
                          <!--备注型 -->                              
                          <logic:equal name="element" property="fieldtype" value="M">
                            <td align="left" class="RecordRow" nowrap>                
                               <html:text name="selectKqFieldForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength='<%="factorlist["+index+"].itemlen"%>' styleClass="text4"/>                               
                            </td>                           
                          </logic:equal>
                          <!--字符型 -->                                                    
                          <logic:equal name="element" property="fieldtype" value="A">
                            <td align="left" class="RecordRow" nowrap>
                              <logic:notEqual name="element" property="codeid" value="0">
                                <html:hidden name="selectKqFieldForm" property='<%="factorlist["+index+"].value"%>' styleClass="text4"/>                               
                                <html:text name="selectKqFieldForm" property='<%="factorlist["+index+"].hzvalue"%>' size="30" maxlength="50" styleClass="text4" onchange="fieldcode(this,1)"/><img src="/images/code.gif" align="middle" onclick='openCondCodeDialog("${element.codeid}","<%="factorlist["+index+"].hzvalue"%>");'/>
                              </logic:notEqual>                              
                              <logic:equal name="element" property="codeid" value="0">
                                <!--考勤日期-->
                                <logic:equal name="element" property="fieldname" value="q03z0">
                                  <html:text name="selectKqFieldForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="${element.itemlen}" styleClass="text4"  onfocus="setday(this);" onchange="getdate(this);" readonly="true"/>                               
                                </logic:equal>
                                <!--人员库-->
                                <logic:equal name="element" property="fieldname" value="nbase">
                                   <html:select name="selectKqFieldForm" property='<%="factorlist["+index+"].value"%>' size="1" styleClass="text4">
                                       <html:optionsCollection property="dblist" value="dataValue" label="dataName"/>
                                   </html:select>                                
                                </logic:equal>
                                <!--其他-->
                                <logic:notEqual name="element" property="fieldname" value="q03z0">
                                    <logic:notEqual name="element" property="fieldname" value="nbase">
                                      <html:text name="selectKqFieldForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="${element.itemlen}" styleClass="text4"/>                               
                                    </logic:notEqual>
                                </logic:notEqual>                                
                              </logic:equal>                               
                            </td>                           
                          </logic:equal> 
                          <!--数据值-->                            
                          <logic:equal name="element" property="fieldtype" value="N">
                            <td align="left" class="RecordRow" nowrap>                
                               <html:text name="selectKqFieldForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="${element.itemlen}" styleClass="text4"/>                               
                            </td>                           
                          </logic:equal>                       
                       </tr>        
                       <%
                       	++i;
                       %>                    
                       </logic:iterate>                     
                      
                      </tr>
                      <tr><td height="15" colspan="4"></td></tr>                                      
	       </table>	        	
            
          <tr class="list3">
            <td align="left" colspan="2">
		<html:checkbox name="selectKqFieldForm" property="like" value="1" >&nbsp;<bean:message key="label.query.like"/></html:checkbox>&nbsp;&nbsp;       
            </td>
          </tr>            
          <tr class="list3">
            <td height="35">
          
				<input type="button" name="btnreturn" value='<bean:message key="button.query.pre"/>' onclick="kqblack();" class="mybutton">
				<input type="button" name="btnreturn" value='<bean:message key="button.query"/>' onclick="kqQuery();" class="mybutton">
							
				 <!--<input type="button" name="btnreturn" value='<bean:message key="button.query.pre"/>' onclick="history.back();" class="mybutton">-->						      
				<input type="button" name="btnreturn" value='<bean:message key="button.close"/>' onclick="window.close();" class="mybutton">
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