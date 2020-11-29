<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>

<html:form action="/hire/zp_filter/query">
  <br>
  <br>
  <br>    
  <table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!--  <td width="10" valign="top" class="tableft"></td>
       		<td width="130" align=center class="tabcenter"><bean:message key="label.query.inforquery"/></td>
       		<td width="10" valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>--> 
       		<td align=center class="TableRow"><bean:message key="label.query.inforquery"/></td>             	      
          </tr> 
          <tr>
            <td class="framestyle9">

               <br>
               <table border="0" cellpmoding="0" cellspacing="5"  class="DetailTable"  cellpadding="0" align="center" >     
                      <logic:iterate id="element" name="zpFilterForm"  property="fieldlist" indexId="index"> 
                      <tr>           
                          <td align="right" class="tdFontcolor" nowrap >                
                            <bean:write  name="element" property="itemdesc" filter="true"/>:
                          </td>
                          <!--日期型 -->                            
                          <logic:equal name="element" property="itemtype" value="D">
                            <td align="left" class="tdFontcolor" nowrap>                
                               <html:text name="zpFilterForm" property='<%="fieldlist["+index+"].value"%>' size="13" maxlength="10" styleClass="TEXT4"/>
                               <bean:message key="label.query.to"/><html:text name="zpFilterForm" property='<%="fieldlist["+index+"].viewvalue"%>' size="13" maxlength="10" styleClass="TEXT4"/>
			       <!-- 没有什么用，仅给用户与视觉效果-->
			       <INPUT type="radio" name="radio" checked=true><bean:message key="label.query.day"/><INPUT type="radio" name="radio"><bean:message key="label.query.age"/>			                         	                                            
                            </td>                           
                          </logic:equal>
                          <!--备注型 -->                              
                          <logic:equal name="element" property="itemtype" value="M">
                            <td align="left" class="tdFontcolor" nowrap>                
                               <html:text name="zpFilterForm" property='<%="fieldlist["+index+"].value"%>' size="31" maxlength='<%="fieldlist["+index+"].itemlength"%>' styleClass="TEXT4"/>                               
                            </td>                           
                          </logic:equal>
                          <!--字符型 -->                                                    
                          <logic:equal name="element" property="itemtype" value="A">
                            <td align="left" class="tdFontcolor" nowrap>
                              <logic:notEqual name="element" property="codesetid" value="0">
                                <html:hidden name="zpFilterForm" property='<%="fieldlist["+index+"].value"%>' styleClass="text"/>                               
                                <html:text name="zpFilterForm" property='<%="fieldlist["+index+"].viewvalue"%>' size="31" maxlength="50" styleClass="TEXT4" onchange="fieldcode(this,2);"/>
                                <img src="/images/code.gif" onclick='openInputCodeDialog("${element.codesetid}","<%="fieldlist["+index+"].viewvalue"%>");'/>
                              </logic:notEqual> 
                              <logic:equal name="element" property="codesetid" value="0">
                                <html:text name="zpFilterForm" property='<%="fieldlist["+index+"].value"%>' size="31" maxlength="${element.itemlength}" styleClass="TEXT4"/>                               
                              </logic:equal>                               
                            </td>                           
                          </logic:equal> 
                          <!--数据值-->                            
                          <logic:equal name="element" property="itemtype" value="N">
                            <td align="left" class="tdFontcolor" nowrap>                
                               <html:text name="zpFilterForm" property='<%="fieldlist["+index+"].value"%>' size="31" maxlength="${element.itemlength}" styleClass="TEXT4"/>                               
                            </td>                           
                          </logic:equal>                           
                       </tr>                            
                       </logic:iterate>
                       <tr><td height="10"></td></tr>                    
	       </table>	            	
            </td>
          </tr>
          
      
          <tr class="list3">
            <td align="center" style="height:35px;">
	       <html:checkbox name="zpFilterForm" property="like" value="1"><bean:message key="label.query.like"/></html:checkbox>            
               <hrms:submit styleClass="mybutton"  property="b_mquery">
                    <bean:message key="button.query"/>
	       </hrms:submit>
               <html:reset styleClass="mybutton" property="bc_clear" >
                    <bean:message key="button.clear"/>
	       </html:reset> 
	       &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
               <hrms:submit styleClass="mybutton"  property="b_filter" onclick="return ifrysx()">
                    <bean:message key="label.zp_filter.name"/>
	       </hrms:submit>  
	
            </td>
          </tr>  
  </table>
 
</html:form>
