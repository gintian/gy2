<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes/>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<html:form action="/kq/kqself/plan/annual_plan_institute" >
   <br><br>   
  <table width="400" border="0" cellpadding="1" cellspacing="0" align="center">
          <tr height="20">
       	    <!--  <td width=10 valign="top" class="tableft"></td>
       	    <td width=130 align=center class="tabcenter"><bean:message key="kq.self.plan"/></td>
       	    <td width=10 valign="top" class="tabright"></td>
       	    <td valign="top" class="tabremain" width="500"></td>  --> 
       	    <td  align=center class="TableRow"><bean:message key="kq.self.plan"/></td>           	      
          </tr> 
          <tr>
          <td  class="framestyle9">
            <table border="0" cellpmoding="0" cellspacing="5"  class="DetailTable"  cellpadding="0" >   
                <tr><td height="10"></td></tr>   
                <logic:iterate id="element" name="annualPlanForm"  property="onelist" indexId="index"> 
                      <logic:equal name="element" property="visible" value="true">
                           <tr> 
                              <td align="right" class="tdFontcolor" nowrap >                
                                 <bean:write  name="element" property="itemdesc" filter="true"/>:
                              </td>         
                                                      
                               <td align="left" class="tdFontcolor" nowrap>  
                                 <logic:notEqual name="element" property="codesetid" value="0">  
                                    <bean:write  name="annualPlanForm" property='<%="onelist["+index+"].viewvalue"%>' filter="false"/>
                                  </logic:notEqual>
                                  <logic:equal name="element" property="codesetid" value="0">              
                                     <bean:write  name="annualPlanForm" property='<%="onelist["+index+"].value"%>' filter="false"/>
                                  </logic:equal>
                                </td>                           
                              
                                                       
                              </tr>   
                        </logic:equal>  
                   </logic:iterate>
            <tr><td height="10"></td></tr>    
 	      </table>	            	
      </td>
     </tr>
     <tr class="list3">
             <td align="center" style="height:35px;">    
             <input type="button" name="btnreturn" value='<bean:message key="kq.emp.button.return"/>' onclick="history.back();" class="mybutton">						      
	            	
             </td>
             </tr>                
   </table>
</html:form>





