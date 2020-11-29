<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>
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
	 var tag=false;   
          <logic:iterate  id="element"    name="annualApplyForm"  property="flist" indexId="index"> 
            <logic:equal name="element" property="itemtype" value="D">   
               var valueInputs=document.getElementsByName("<%="flist["+index+"].value"%>");
               var dobj=valueInputs[0];
               tag= checkDate(dobj);      
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
<html:form action="/kq/kqself/apply/my_annual_apply" onsubmit="return validate()">
   <br><br>
  <table width="400" border="0" cellpadding="1" cellspacing="0" align="center">
          <tr height="20">
       	    <!--  <td width=10 valign="top" class="tableft"></td>
       	    <td width=130 align=center class="tabcenter"><bean:message key="lable.overtime"/></td>
       	    <td width=10 valign="top" class="tabright"></td>
       	    <td valign="top" class="tabremain" width="500"></td>--> 
       	    <td align=center class="TableRow"><bean:message key="lable.overtime"/></td>             	      
          </tr> 
          <tr>
          <td class="framestyle9">
            <table border="0" cellpmoding="0" cellspacing="5"  class="DetailTable"  cellpadding="0" >   
                <tr><td height="10"></td></tr>   
                <logic:iterate id="element" name="annualApplyForm"  property="flist" indexId="index"> 
                      <logic:equal name="element" property="visible" value="true">
                           <tr> 
                              <td align="right" class="tdFontcolor" nowrap >                
                                 <bean:write  name="element" property="itemdesc" filter="true"/>:
                              </td>             
                              <!--日期型 -->                            
                              <logic:equal name="element" property="itemtype" value="D">
                                 <td align="left" class="tdFontcolor" nowrap>                
                                     <html:text name="annualApplyForm" property='<%="flist["+index+"].value"%>' size="13" maxlength="10" styleClass="TEXT4"/>
                                 </td>                           
                              </logic:equal>
                              <!--备注型 -->                              
                              <logic:equal name="element" property="itemtype" value="M">
                                 <td align="left" class="tdFontcolor" nowrap>                
                                    <html:textarea name="annualApplyForm" property='<%="flist["+index+"].value"%>' cols="35" rows="4" styleClass="text5"/>
                                 </td>                           
                              </logic:equal>
                              <!--字符型 -->                                                    
                              <logic:equal name="element" property="itemtype" value="A">
                                 <td align="left" class="tdFontcolor" nowrap>
                                    <logic:notEqual name="element" property="codesetid" value="0">
                                       <html:hidden name="annualApplyForm" property='<%="flist["+index+"].value"%>' styleClass="text"/>                               
                                       <html:text name="annualApplyForm" property='<%="flist["+index+"].viewvalue"%>' size="31" maxlength="50" styleClass="TEXT4" onchange="fieldcode(this,2);"/>
                                       <img src="/images/code.gif" onclick='openInputCodeDialog("${element.codesetid}","<%="flist["+index+"].viewvalue"%>");'/>
                                    </logic:notEqual> 
                                    <logic:equal name="element" property="codesetid" value="0">
                                        <html:text name="annualApplyForm" property='<%="flist["+index+"].value"%>' size="31" maxlength="${element.itemlength}" styleClass="TEXT4"/> 
                                    </logic:equal>                               
                                  </td>                           
                                </logic:equal> 
                                <!--数据值-->                            
                                <logic:equal name="element" property="itemtype" value="N">
                                   <td align="left" class="tdFontcolor" nowrap>                
                                       <html:text name="annualApplyForm" property='<%="flist["+index+"].value"%>' size="31" maxlength="${element.itemlength}" styleClass="TEXT4"/>                               
                                   </td>                           
                                </logic:equal>                           
                              </tr>   
                       </logic:equal>  
                   </logic:iterate>
                  <tr><td height="10"></td></tr>    
                  <tr class="list3">
                    <td align="center" colspan="4">
             	    <hrms:submit styleClass="mybutton" property="b_save" onclick="document.annualApplyForm.target='_self';validate('R','','');return (document.returnValue && ifqrbc());">
            		    <bean:message key="button.save"/>
	         	       </hrms:submit>
	          	  <html:reset styleClass="mybutton" property="reset"><bean:message key="button.clear"/></html:reset>	 
               </td>
              </tr>    
            <tr><td height="10"></td></tr>         
 	       </table>	            	
       </td>
     </tr>       
  </table>
</html:form>



