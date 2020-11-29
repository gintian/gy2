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
<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="javascript">
   function sumitMackout()
   {
      if (confirm('确认签写年假吗?如果填写提交则不能再修改该计划!'))
      {
         kqPlanInfoForm.action="/kq/kqself/plan/my_plan_info.do?b_makesave=link";
         kqPlanInfoForm.submit();
      }
   } 
</script>
<html:form action="/kq/kqself/plan/my_plan_info">
   <br><br>
     <table width="400" border="0" cellpadding="1" cellspacing="0" align="center">
          <tr height="20">
       	    <!-- <td width=10 valign="top" class="tableft"></td>
       	    <td width=130 align=center class="tabcenter">签写年假</td>
       	    <td width=10 valign="top" class="tabright"></td>
       	    <td valign="top" class="tabremain" width="500"></td>  -->
	<td align=center class="TableRow">签写年假</td>            	      
          </tr> 
          <tr>
          <td  class="framestyle9">
            <table border="0" cellpmoding="0" cellspacing="5"  class="DetailTable"  cellpadding="0" >   
                <tr><td height="25" colspan="4">
                确认后自动生成年假申请单,等待管理员审批,审批通过后假期生效</td></tr>   
                <logic:iterate id="element" name="kqPlanInfoForm"  property="onelist" indexId="index"> 
                      <logic:equal name="element" property="visible" value="true">
                           <tr> 
                              <td align="right" class="tdFontcolor" nowrap >                
                                 <bean:write  name="element" property="itemdesc" filter="true"/>:
                              </td>             
                              <!--日期型 -->                            
                              <logic:equal name="element" property="itemtype" value="D">
                                 <td align="left" class="tdFontcolor" nowrap> 
                                      <html:text name="kqPlanInfoForm" property='<%="onelist["+index+"].value"%>' disabled = "true" size="20" maxlength="20"  styleClass="TEXT4"/>
                                  </td>                           
                              </logic:equal>
                              <!--备注型 -->                              
                              <logic:equal name="element" property="itemtype" value="M">
                                 <td align="left" class="tdFontcolor" nowrap>                
                                    <html:textarea name="kqPlanInfoForm" property='<%="onelist["+index+"].value"%>' disabled = "true" cols="35" rows="4" styleClass="text5"/>
                                 </td>                           
                              </logic:equal>
                              <!--字符型 -->                                                    
                              <logic:equal name="element" property="itemtype" value="A">
                                 <td align="left" class="tdFontcolor" nowrap>
                                    <logic:notEqual name="element" property="codesetid" value="0">
                                       <html:hidden name="kqPlanInfoForm" property='<%="onelist["+index+"].value"%>' styleClass="text"/>                               
                                       <html:text name="kqPlanInfoForm" property='<%="onelist["+index+"].viewvalue"%>' disabled = "true" size="20" maxlength="50" styleClass="TEXT4" onchange="fieldcode(this,2);"/>
                                     </logic:notEqual> 
                                    <logic:equal name="element" property="codesetid" value="0">
                                        <html:text name="kqPlanInfoForm" property='<%="onelist["+index+"].value"%>' disabled = "true" size="20" maxlength="${element.itemlength}" styleClass="TEXT4"/> 
                                    </logic:equal>                               
                                  </td>                           
                                </logic:equal> 
                                <!--数据值-->                            
                                <logic:equal name="element" property="itemtype" value="N">
                                   <td align="left" class="tdFontcolor" nowrap>                
                                       <html:text name="kqPlanInfoForm" property='<%="onelist["+index+"].value"%>' disabled = "true" size="31" maxlength="${element.itemlength}" styleClass="TEXT4"/>                               
                                   </td>                           
                                </logic:equal>                           
                              </tr>   
                        </logic:equal>  
                   </logic:iterate>
            <tr><td height="10"></td></tr>    
            <tr class="list3">
             <td align="center" style="height:35px;">
         	
         	<input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' onclick="sumitMackout();" class="mybutton">						
	     	 
              <input type="button" name="btnreturn" value='<bean:message key="kq.emp.button.return"/>' onclick="history.back();" class="mybutton">						      
             
             </td>
             </tr>    
            <tr><td height="10"></td></tr>         
 	      </table>	            	
      </td>
     </tr>                
   </table>
</html:form>





