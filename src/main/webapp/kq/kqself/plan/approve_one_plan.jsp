<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript">
   function approve()
   {
	  var approveDate = document.getElementById('q2913').value;
	  var approveResult = document.getElementById('q29z0').value;
	  var approveMemo = document.getElementById('q29z7').value;
	  
	  var arrayObj = new Array();
	  arrayObj[0] = approveDate;
	  arrayObj[1] = approveResult;
	  arrayObj[2] = approveMemo;
	  
	  window.returnValue = arrayObj;
	  window.close();	    
   }
   
   function getKqCalendarVar()
   {
     var request=new Request({method:'post',onSuccess:setKqCalendarVar,functionId:'15388800008'});
   }
   function setKqCalendarVar(outparamters)
   {
       weeks=outparamters.getValue("weeks");  
       feasts=outparamters.getValue("feasts");  
       turn_dates=outparamters.getValue("turn_dates");  
       week_dates=outparamters.getValue("week_dates");  
   }
</script> 
<html:form action="/kq/kqself/plan/searchoneplan" >
    <div class="fixedDiv3">
  <table width="100%" border="0" cellpadding="1" cellspacing="0" align="center">
          <tr height="20">
			<td  align=center class="TableRow"><bean:message key="kq.self.plan"/></td>            	      
          </tr> 
          <tr>
           <td class="framestyle9">
            <table border="0" cellpmoding="0" cellspacing="5"  class="DetailTable"  cellpadding="0" >   
                <tr><td height="10"></td></tr>                
                <logic:iterate id="element" name="kqPlanInfoForm"  property="approvelist" indexId="index"> 
                      <tr> 
                              <td align="right" class="tdFontcolor" nowrap >                
                                 <bean:write  name="element" property="itemdesc" filter="true"/>
                              </td>             
                              <!--日期型 -->                            
                              <logic:equal name="element" property="itemtype" value="D">
                                 <td align="left" class="tdFontcolor" nowrap>                
                                     <html:text name="kqPlanInfoForm" 
                                     styleId="q2913"
                                     property='<%="approvelist["+index+"].value"%>' 
                                     size="20" maxlength="20" styleClass="TEXT4" 
                                     onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false,false);'/>
                                 </td>                           
                              </logic:equal>
                              <!--备注型 -->                                                       
                              <logic:equal name="element" property="itemtype" value="M">
                                 <td align="left" class="tdFontcolor" nowrap>                
                                    <html:textarea styleId="q29z7"
                                    name="kqPlanInfoForm" 
                                    property='<%="approvelist["+index+"].value"%>' 
                                    cols="35" rows="4" styleClass="text5"/>
                                 </td>                           
                              </logic:equal>
                              <!--字符型 -->  
                                                                                          
                              <logic:equal name="element" property="itemtype" value="A">
                                 <td align="left" class="tdFontcolor" nowrap>
                                    <logic:notEqual name="element" property="codesetid" value="0">                                     
                                      <logic:equal name="element" property="itemid" value="q29z0">
                                         <hrms:optioncollection  name="kqPlanInfoForm" property="q29z0list" collection="list" />
	                                     <html:select styleId="q29z0" name="kqPlanInfoForm" property='<%="approvelist["+index+"].value"%>' size="1">
                                              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                                         </html:select>  
                                                                    
                                      </logic:equal>
                                      <logic:notEqual name="element" property="itemid" value="q29z0">
                                          <html:hidden name="kqPlanInfoForm" property='<%="approvelist["+index+"].value"%>' styleClass="text"/>                               
                                          <html:text name="kqPlanInfoForm" property='<%="approvelist["+index+"].viewvalue"%>' size="31" maxlength="50" styleClass="TEXT4" onchange="fieldcode(this,2);"/>
                                          <img src="/images/code.gif" onclick='openInputCodeDialog("${element.codesetid}","<%="approvelist["+index+"].viewvalue"%>");'/>
                                      </logic:notEqual> 
                                    </logic:notEqual> 
                                    <logic:equal name="element" property="codesetid" value="0">
                                        <html:text name="kqPlanInfoForm" property='<%="approvelist["+index+"].value"%>' size="31" maxlength="${element.itemlength}" styleClass="TEXT4"/> 
                                    </logic:equal>                               
                                  </td>                           
                                </logic:equal> 
                                <!--数据值-->                            
                                <logic:equal name="element" property="itemtype" value="N">
                                   <td align="left" class="tdFontcolor" nowrap>                
                                       <html:text name="kqPlanInfoForm" property='<%="approvelist["+index+"].value"%>' size="31" maxlength="${element.itemlength}" styleClass="TEXT4"/>                               
                                   </td>                           
                                </logic:equal>                           
                              </tr>                           
                   </logic:iterate>
            <tr><td height="10"></td></tr>    
 	      </table>	   
 	               	
      </td>
     </tr>                
   </table>
   </div>
   <div align="center" style="margin-top: 5px;">
     <input type="button" name="br_approve" 
     value='<bean:message key="button.approve"/>' 
     class="mybutton" 
     onclick="approve();"> 
   </div>

</html:form>





