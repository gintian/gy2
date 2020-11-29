<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="JavaScript" src="/js/validate.js"></script>
<html:form action="/selfservice/addressbook/editaddressbook">
  <table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!-- <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="selfservice.addressbook.editaddressbook"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>   --> 
       		<td  align=center class="TableRow">&nbsp;<bean:message key="selfservice.addressbook.editaddressbook"/>&nbsp;</td>           	      
          </tr>     
          <tr>
            <td class="framestyle9">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">     
                      <logic:iterate  id="element"    name="addressBookConstantForm"  property="fielditemlist" indexId="index"> 
                      <tr>           
                          <td align="right" class="tdFontcolor" nowrap >                
                              <bean:write  name="element" property="itemdesc"/>&nbsp;    
                          </td>
                          <td align="left" class="tdFontcolor" nowrap>                
                              <logic:equal name="element" property="codesetid" value="0">
                                  <logic:equal name="element" property="priv_status" value="1">
                                      &nbsp;<html:text name="addressBookConstantForm" property='<%="fielditemlist["+index+"].value"%>' onclick="closefrm()" readonly="true" styleClass="text"/> 
                                  </logic:equal>
                                  <logic:equal name="element" property="priv_status" value="2">
                                         <logic:equal name="element" property="itemtype" value="D">
                                             &nbsp;<html:text  name="addressBookConstantForm" property='<%="fielditemlist["+index+"].value"%>' styleClass="text" onclick="closefrm()"/>  
                                         </logic:equal>
                                         <logic:equal name="element" property="itemtype" value="M">
                                              <html:hidden name="addressBookConstantForm" property='<%="fielditemlist["+index+"].value"%>'/>
                                              &nbsp;<div id="Layer${element.rowindex}" style="display:none; position:absolute; left:297px; top:146px; width:215px; height:100px; z-index:53; background-color: #FFFFFF; layer-background-color: #FFFFFF; border: 1px	none #000000;">	
	                                      <html:textarea name="addressBookConstantForm" property='<%="fielditemlist["+index+"].viewvalue"%>'  rows="8"  cols="30" styleClass="text"/>
	                                      <img src="/images/ok.jpg" width="57" height="16" border="0" align="right" onclick="paste(Layer${element.rowindex})">	
	                                      <img src="/images/edit.gif" width="57" height="16" border="0" align="right" onClick="closecancle(Layer${element.rowindex})"> 
	                                       </div>      
                                               <img src="/images/ok.jpg" border="0" onclick="openlayer(Layer${element.rowindex})" style="cursor:hand" onclick="closefrm()"> 
                                         </logic:equal>
                                         <logic:equal name="element" property="itemtype" value="A">
                                              &nbsp;<html:text  name="addressBookConstantForm" property='<%="fielditemlist["+index+"].value"%>' styleClass="text" onclick="closefrm()"/>  
                                         </logic:equal> 
                                         <logic:equal name="element" property="itemtype" value="N">
                                            &nbsp;<html:text  name="addressBookConstantForm" property='<%="fielditemlist["+index+"].value"%>' styleClass="text" onclick="closefrm()"/>  
                                         </logic:equal>                            
                                 </logic:equal>            
                             </logic:equal>
                             <logic:notEqual name="element" property="codesetid" value="0">
                                 <logic:equal name="element" property="priv_status" value="1">
                                        &nbsp;<html:text name="addressBookConstantForm" property='<%="fielditemlist["+index+"].viewvalue"%>' readonly="true" styleClass="text"/>         
                                 </logic:equal>
                                 <logic:equal name="element" property="priv_status" value="2">
                                         <html:hidden name="addressBookConstantForm" property='<%="fielditemlist["+index+"].value"%>'/>  
                                             &nbsp;<html:text name="addressBookConstantForm" property='<%="fielditemlist["+index+"].viewvalue"%>' readonly="true" styleClass="text" onclick="openCodeDialog('${element.codesetid}',this);"/> 
                                 </logic:equal>
                            </logic:notEqual>
                          </td>                                               
                      </tr> 
                     </logic:iterate>                                           
	       </table>	            	
            </td>
          </tr>
          <tr class="list3">
             <td align="center" nowrap style="height:35px;">

                   <hrms:submit styleClass="mybutton"  property="b_save">
                       <bean:message key="button.ok"/>
	           </hrms:submit>    
              </td>
          </tr>           
  </table> 
</html:form>
