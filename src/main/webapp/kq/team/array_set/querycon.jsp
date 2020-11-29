<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<script type="text/javascript" src="/kq/kq.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="javascript" src="/ajax/common.js"></script>
<%
	String rightStr = request.getParameter("rightStr");
%>
<script language="javascript">
   var date_desc;
  
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
function back(){
	arrayGroupSelectForm.action="/kq/team/array_group/selectfiled.do?b_init=link&rightStr="+$URL.encode("<%=rightStr%>");
	arrayGroupSelectForm.submit();
}
</script>
<%
  int i=0;
%>
<script language="javascript">
    
     
 
</script>
<html:form action="/kq/team/array_group/selectfiled">
  <br>
  <br>
  <br>  
  <table width="700" border="0" cellpadding="0" cellspacing="0" align="center">
    <tr height="20">
      <!--  <td width=1 valign="top" class="tableft1"></td>
      <td width=130 align=center class="tabcenter">&nbsp;<bean:message key="button.query"/>&nbsp;</td>
      <td width=10 valign="top" class="tabright"></td>
      <td valign="top" class="tabremain" width="700"></td>  -->
      <td  align=center class="TableRow">&nbsp;<bean:message key="button.query"/>&nbsp;</td>            	      
    </tr>
    <tr>
       <td  class="framestyle9" >
          <table border="0"  cellspacing="0" width="70%" class="ListTable"  cellpadding="2" align="center">
             <tr><td height="10" colspan="4"></td></tr> 
              <tr><td colspan="4"> 
                 <table border="0"  cellspacing="0" width="70%" class="ListTable1"  cellpadding="2" align="center"  style="border-right: 0">
                     <tr>
                	  <td align="left"  colspan="4">
                	     <hrms:optioncollection name="arrayGroupSelectForm" property="dblist" collection="list" />
                       	 <html:select name="arrayGroupSelectForm" property="dbpre" size="1" >
                      	 <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                  		 </html:select>   
                	  </td>
                     </tr>
                     <tr><td height="5" colspan="4"></td></tr>
                    <tr>
                       <td align="center" nowrap class="TableRow"><bean:message key="label.query.logic"/></td>
                       <td align="center" nowrap class="TableRow"><bean:message key="label.query.field"/></td>
                       <td align="center" nowrap class="TableRow"><bean:message key="label.query.relation"/></td>
                       <td align="center" nowrap class="TableRow"><bean:message key="label.query.value"/></td>
                    </tr> 
                    <logic:iterate id="element" name="arrayGroupSelectForm"  property="factorlist" indexId="index"> 
                      <tr>       
                         <td align="center" class="RecordRow" nowrap >
                            <%
                              if(i!=0)
                              { 
                            %>
                 	    <hrms:optioncollection name="arrayGroupSelectForm" property="logiclist" collection="list"/>
                                <html:select name="arrayGroupSelectForm" property='<%="factorlist["+index+"].log"%>' size="1">
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
                            <hrms:optioncollection name="arrayGroupSelectForm" property="operlist" collection="list"/>
                              <html:select name="arrayGroupSelectForm" property='<%="factorlist["+index+"].oper"%>' size="1">
                                  <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                            </html:select>
                         </td>                                                  
                          <!--日期型 -->                            
                          <logic:equal name="element" property="fieldtype" value="D">
                            <td align="left" class="RecordRow" nowrap>                
			        <html:text name="arrayGroupSelectForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="10" styleClass="text4" ondblclick="showDateSelectBox(this);" onblur="Element.hide('date_panel');"/>
                            </td>                           
                          </logic:equal>
                          <!--备注型 -->                              
                          <logic:equal name="element" property="fieldtype" value="M">
                            <td align="left" class="RecordRow" nowrap>                
                               <html:text name="arrayGroupSelectForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength='<%="factorlist["+index+"].itemlen"%>' styleClass="text4"/>                               
                            </td>                           
                          </logic:equal>
                          <!--字符型 -->                                                    
                          <logic:equal name="element" property="fieldtype" value="A">
                            <td align="left" class="RecordRow" nowrap>
                              <logic:notEqual name="element" property="codeid" value="0">
                                <html:hidden name="arrayGroupSelectForm" property='<%="factorlist["+index+"].value"%>' styleClass="text4"/>                               
                                <html:text name="arrayGroupSelectForm" property='<%="factorlist["+index+"].hzvalue"%>' size="30" maxlength="50" styleClass="text4" onchange="fieldcode(this,1)"/>
                                <logic:notEqual value="UN" name="element" property="codeid">
                                	<logic:notEqual value="UM" name="element" property="codeid">
                                		<logic:notEqual value="@K" name="element" property="codeid">
			                                <img src="/images/code.gif" align="middle" onclick='openCondCodeDialog("${element.codeid}","<%="factorlist["+index+"].hzvalue"%>");'/>
                                		</logic:notEqual>
                                	</logic:notEqual>
                                </logic:notEqual>
                                <logic:equal value="UN" name="element" property="codeid">
                                	<img src="/images/code.gif" align="middle" onclick='openInputCodeDialogOrgInputPos("${element.codeid}","<%="factorlist["+index+"].hzvalue"%>","${arrayGroupSelectForm.orgparentcode}",1);'/>
                                </logic:equal>
                                <logic:equal value="UM" name="element" property="codeid">
                                	<img src="/images/code.gif"  align="middle" onclick='openInputCodeDialogOrgInputPos("${element.codeid}","<%="factorlist["+index+"].hzvalue"%>","${arrayGroupSelectForm.orgparentcode}",1);'/>
                                </logic:equal>
                                <logic:equal value="@K" name="element" property="codeid">
                                	<img src="/images/code.gif"  align="middle" onclick='openInputCodeDialogOrgInputPos("${element.codeid}","<%="factorlist["+index+"].hzvalue"%>","${arrayGroupSelectForm.orgparentcode}",1);'/>
                                </logic:equal>
                              </logic:notEqual> 
                              <logic:equal name="element" property="codeid" value="0">
                                <html:text name="arrayGroupSelectForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="${element.itemlen}" styleClass="text4"/>                               
                              </logic:equal>                               
                            </td>                           
                          </logic:equal> 
                          <!--数据值-->                            
                          <logic:equal name="element" property="fieldtype" value="N">
                            <td align="left" class="RecordRow" nowrap>                
                               <html:text name="arrayGroupSelectForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="${element.itemlen}" styleClass="text4"/>                               
                            </td>                           
                          </logic:equal>                           
                       </tr>        
                       <%
                       	++i;
                       %>                    
                       </logic:iterate>
                       <tr>
                	  <td align="center" nowrap class="RecordRow" colspan="4">
                	     <html:checkbox name="arrayGroupSelectForm" property="like" value="1" >&nbsp;<bean:message key="label.query.like"/></html:checkbox>
                	 
                	  </td>
                        </tr> 
                   </table>
               </td>
               </tr>
               <tr><td height="15" colspan="4"></td></tr>                                      
	     </table>	            	
           </td>
         </tr>
         <tr class="list3" style="padding-top:5px;">
            <td >
       	       <hrms:submit styleClass="mybutton"  property="b_query" onclick="document.arrayGroupSelectForm.target='_self';validate('RS','dbpre','人员库');return document.returnValue;">
                  <bean:message key="button.query"/>
	           </hrms:submit>
	           
              <html:button styleClass="mybutton" property="b_pre" onclick="back();">
            	  <bean:message key="button.query.pre"/>
	      </html:button> 	     
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
   hide_nbase_select('dbpre');
</script>