<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/kq/kq.js"></script>
<script type="text/javascript">dateFormat='yyyy-mm-dd'</script>
<script language="javascript">
    function validate1()
    {
      var co=document.getElementById("q2903"); 
      var value_co=co.value;
      var isCorrect=true;
      if(trim(value_co)=="")
      {
         alert("年度不能为空！");
         co.focus(); 
         isCorrect=false;
         return false;
      }else
      {
         if(!isDate(value_co,"yyyy"))
         {
           alert("年度格式不正确,请输入正确的时间格式！\nyyyy");
           co.focus();
           isCorrect=false; 
           return false;
         }
      }
      co=document.getElementById("q2905");       
      value_co=co.value;
      if(trim(value_co)=="")
      {
         alert("计划名称不能为空！");
         co.focus(); 
         isCorrect=false;
         return false;
      }
      co=document.getElementById("q2907"); 
      value_co=co.value;
      if(trim(value_co)=="")
      {
         alert("计划内容不能为空！");
         co.focus(); 
         isCorrect=false;
         return false;
      }
      co=document.getElementById("q2909"); 
      value_co=co.value;
      if(trim(value_co)=="")
      {
         alert("创建日期不能为空！");
         co.focus(); 
         isCorrect=false;
         return false;
      }else
      {
         if(!isDate(value_co,"yyyy-MM-dd"))
         {
           alert("创建日期格式不正确,请输入正确的时间格式！\nyyyy-MM-dd");
           co.focus(); 
           isCorrect=false;
           return false;
         }
      }
      co=document.getElementById("q2911"); 
      value_co=co.value;
      if(trim(value_co)=="")
      {
         alert("创建人不能为空！");
         co.focus(); 
         isCorrect=false;
         return false;
      }
      return isCorrect;
    }
    function validate()
    {
      var co=document.getElementById("q2903"); 
      var value_co=co.value;
      var isCorrect=true;
      if(trim(value_co)=="")
      {
         co.focus(); 
         isCorrect=false;
         return false;
      }else
      {
         if(!isDate(value_co,"yyyy"))
         {
           co.focus();
           isCorrect=false; 
           return false;
         }
      }
      co=document.getElementById("q2905");       
      value_co=co.value;
      if(trim(value_co)=="")
      {
         co.focus(); 
         isCorrect=false;
         return false;
      }
      co=document.getElementById("q2907"); 
      value_co=co.value;
      if(trim(value_co)=="")
      {
         co.focus(); 
         isCorrect=false;
         return false;
      }
      co=document.getElementById("q2909"); 
      value_co=co.value;
      if(trim(value_co)=="")
      {
         co.focus(); 
         isCorrect=false;
         return false;
      }else
      {
         if(!isDate(value_co,"yyyy-MM-dd"))
         {
           co.focus(); 
           isCorrect=false;
           return false;
         }
      }
      co=document.getElementById("q2911"); 
      value_co=co.value;
      if(trim(value_co)=="")
      {
         co.focus(); 
         isCorrect=false;
         return false;
      }
      return isCorrect;
    }
</script>
<html:form action="/kq/kqself/plan/annual_plan_institute" onsubmit="return validate()">
   <br><br>
   <html:hidden property="year"/>
   <html:hidden property="plan_id"/>
  <table width="400" border="0" cellpadding="1" cellspacing="0" align="center">
          <tr height="20">
       	    <!--  <td width=10 valign="top" class="tableft"></td>
       	    <td width=130 align=center class="tabcenter"><bean:message key="kq.self.plan"/></td>
       	    <td width=10 valign="top" class="tabright"></td>
       	    <td valign="top" class="tabremain" width="500"></td>  -->  
       	    <td align=center class="TableRow"><bean:message key="kq.self.plan"/></td>          	      
          </tr> 
          <tr>
          <td  class="framestyle9">
            <table border="0" cellpmoding="0" cellspacing="5"  class="DetailTable"  cellpadding="0" >   
                <tr><td height="10"></td></tr>   
                <logic:iterate id="element" name="annualPlanForm"  property="flist" indexId="index"> 
                      <logic:equal name="element" property="visible" value="true">
                           <tr> 
                              <td align="right" class="tdFontcolor" nowrap >                
                                 <bean:write  name="element" property="itemdesc" filter="true"/>:
                              </td>             
                              <!--日期型 -->                            
                              <logic:equal name="element" property="itemtype" value="D">
                                 <td align="left" class="tdFontcolor" nowrap>                
                                     <html:text name="annualPlanForm" property='<%="flist["+index+"].value"%>' styleId='${element.itemid}' size="13" maxlength="10" styleClass="TEXT4" onclick="getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);"/>
                                 </td>                           
                              </logic:equal>
                              <!--备注型 -->                              
                              <logic:equal name="element" property="itemtype" value="M">
                                 <td align="left" class="tdFontcolor" nowrap>                
                                    <html:textarea name="annualPlanForm" property='<%="flist["+index+"].value"%>' styleId='${element.itemid}' cols="35" rows="4" styleClass="text5"/>
                                 </td>                           
                              </logic:equal>
                              <!--字符型 -->                                                    
                              <logic:equal name="element" property="itemtype" value="A">
                                 <td align="left" class="tdFontcolor" nowrap>
                                    <logic:notEqual name="element" property="codesetid" value="0">
                                       <html:hidden name="annualPlanForm" property='<%="flist["+index+"].value"%>' styleClass="text"/>                               
                                       <html:text name="annualPlanForm" property='<%="flist["+index+"].viewvalue"%>' size="31" maxlength="50" styleClass="TEXT4" onchange="fieldcode(this,2);"/>
                                       <img src="/images/code.gif" onclick='openInputCodeDialog("${element.codesetid}","<%="flist["+index+"].viewvalue"%>");'/>
                                    </logic:notEqual> 
                                    <logic:equal name="element" property="codesetid" value="0">
                                    	<logic:equal name="element" property="itemid" value="q2911">
                                        <html:text disabled="true" name="annualPlanForm" property='<%="flist["+index+"].value"%>' size="31" styleId='${element.itemid}' maxlength="${element.itemlength}" styleClass="TEXT4"/> 
                                    	</logic:equal>                               
                                    	<logic:notEqual name="element" property="itemid" value="q2911">
                                        <html:text name="annualPlanForm" property='<%="flist["+index+"].value"%>' size="31" styleId='${element.itemid}' maxlength="${element.itemlength}" styleClass="TEXT4"/> 
                                    	</logic:notEqual>                               
                                    </logic:equal>                               
                                  </td>                           
                                </logic:equal> 
                                <!--数据值-->                            
                                <logic:equal name="element" property="itemtype" value="N">
                                   <td align="left" class="tdFontcolor" nowrap>                
                                       <html:text name="annualPlanForm" property='<%="flist["+index+"].value"%>' size="31" maxlength="${element.itemlength}" styleClass="TEXT4"/>                               
                                   </td>                           
                                </logic:equal>                           
                              </tr>   
                        </logic:equal>  
                   </logic:iterate>
            <tr><td height="10"></td></tr>    
 	      </table>	            	
      </td>
     </tr>
     <tr class="list3">
             <td align="center" style="height:35px;">
         	     <hrms:submit styleClass="mybutton" property="b_save" onclick="document.annualPlanForm.target='_self';validate1('R','','');return (document.returnValue && ifqrbc());">
            		<bean:message key="button.save"/>
	 	           </hrms:submit>
	 	           <input type="button" name="br_approve" value='<bean:message key="button.return"/>' class="mybutton" onclick="history.back();"> 
             </td>
     </tr>                
   </table>
</html:form>





