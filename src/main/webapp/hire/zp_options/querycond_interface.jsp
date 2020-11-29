<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
  function validate()
  {
    var tag=true;    
     <logic:iterate id="element" name="posFilterSetForm"  property="factorlist" indexId="index">   
        <logic:equal name="element" property="fieldtype" value="D">  
          var valueInputs=document.getElementsByName("<%="factorlist["+index+"].value"%>");
          var dobj=valueInputs[0];
          tag= checkDate(dobj) && tag;      
	  if(tag==false)
	  {
	    dobj.focus();
	    return false;
	  }
        </logic:equal> 
       
      </logic:iterate>    
     return tag;   
  }
  
</script>

<%
  int i=0;
%>
<html:form action="/hire/zp_options/querycond_interface" onsubmit="return validate()">
  <br>
  <br>
  <br>   
  <table width="560" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!-- <td width=1 valign="top" class="tableft1"></td>
       		    <td width=130 align=center class="tabcenter">&nbsp;<bean:message key="label.query.cquery"/>&nbsp;</td>                                           		    
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="700"></td>  -->  
       		<td align=center class="TableRow">&nbsp;<bean:message key="label.query.cquery"/>&nbsp;</td>                         	      
          </tr> 
          <tr>
            <td class="framestyle9">
               <table border="0"  cellspacing="0" width="70%" class="ListTable"  cellpadding="2" align="center">
                    <br>           
                      <tr><td><td colspan="4">&nbsp;</td></tr>
                      <tr><td colspan="4"> 
                      <table border="0"  cellspacing="0" width="70%" class="ListTable1"  cellpadding="4" align="center">
                      <tr>
                	      <td align="center" nowrap class="TableRow"><bean:message key="label.query.number"/></td>                                          	      
                	      <td align="center" nowrap class="TableRow"><bean:message key="label.query.field"/></td>
                	      <td align="center" nowrap class="TableRow"><bean:message key="label.query.relation"/></td>
                	      <td align="center" nowrap class="TableRow"><bean:message key="label.query.value"/></td>
                      	
                      </tr> 
                                
                      <logic:iterate id="element" name="posFilterSetForm"  property="factorlist" indexId="index"> 
                      <tr>           
                           <td align="center" class="RecordRow" nowrap >
				<%=i+1%>　	
                          　</td>                        
                          <td align="center" class="RecordRow" nowrap >
                             <bean:write name="element" property="hz" />&nbsp;
                          </td>  
                          <td align="center" class="RecordRow" nowrap >
                 	       <hrms:optioncollection name="posFilterSetForm" property="operlist" collection="list"/>
                               <html:select name="posFilterSetForm" property='<%="factorlist["+index+"].oper"%>' size="1">
                                  <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                               </html:select>
                          </td>                                                  
                          <!--日期型 -->                            
                          <logic:equal name="element" property="fieldtype" value="D">
                            <td align="left" class="RecordRow" nowrap>                
				<html:text name="posFilterSetForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="10" styleClass="text4"/>
                            </td>                           
                          </logic:equal>
                          <!--备注型 -->                              
                          <logic:equal name="element" property="fieldtype" value="M">
                            <td align="left" class="RecordRow" nowrap>                
                               <html:text name="posFilterSetForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength='<%="factorlist["+index+"].itemlen"%>' styleClass="text4"/>                               
                            </td>                           
                          </logic:equal>
                          <!--字符型 -->                                                    
                          <logic:equal name="element" property="fieldtype" value="A">
                            <td align="left" class="RecordRow" nowrap>
                              <logic:notEqual name="element" property="codeid" value="0">
                                <html:hidden name="posFilterSetForm" property='<%="factorlist["+index+"].value"%>' styleClass="text4"/>                               
                                <html:text name="posFilterSetForm" property='<%="factorlist["+index+"].hzvalue"%>' size="30" maxlength="50" styleClass="text4" onchange="fieldcode(this,1)"/>
                                <img src="/images/code.gif" onclick='openCondCodeDialog("${element.codeid}","<%="factorlist["+index+"].hzvalue"%>");'/>
                              </logic:notEqual> 
                              <logic:equal name="element" property="codeid" value="0">
                                <html:text name="posFilterSetForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="${element.itemlen}" styleClass="text4"/>                               
                              </logic:equal>                               
                            </td>                           
                          </logic:equal> 
                          <!--数据值-->                            
                          <logic:equal name="element" property="fieldtype" value="N">
                            <td align="left" class="RecordRow" nowrap>                
                               <html:text name="posFilterSetForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="${element.itemlen}" styleClass="text4"/>                               
                            </td>                           
                          </logic:equal>                           
                       </tr>        
                       <%
                       	++i;
                       %>                    
                       </logic:iterate>
                       <tr>
                	   <td align="left" nowrap class="RecordRow" colspan="4">
                	     <span><bean:message key="label.query.expression"/></span><br>
                	     <html:textarea name="posFilterSetForm" property="expression" rows="10" cols="60"/>
			   </td>
                       </tr>                             
                     
                      </table>
                      </td>
                      </tr>
                      <tr><td height="15" colspan="4"></td></tr>                                      
	       </table>	            	
            </td>
          </tr>           
          <tr class="list3">
            <td align="center" style="height:35px;">           
                 <hrms:submit styleClass="mybutton"  property="b_save">
                    <bean:message key="button.save"/>
	         </hrms:submit>               	                	                     	         
               <hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.query.pre"/>
	       </hrms:submit>
	       	       
               <html:reset styleClass="mybutton">
                    <bean:message key="button.clear"/>
	       </html:reset> 	
            </td>
          </tr>  
  </table>
 
</html:form>
