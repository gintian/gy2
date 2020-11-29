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
    function validate()
    {
	 var tag=false; 
	  var i=0;
          var start_date="";
          var end_date="";  
          var co=document.getElementById("q2903"); 
       var value_co=co.value;
       var isCorrect=true;
       if(value_co=="")
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
       co=document.getElementById("q3105");       
       value_co=co.value;
       if(trim(value_co)=="")
       {
         alert("申请日期不能为空！");
         co.focus(); 
         isCorrect=false;
         return false;
       }else
       {
         if(!isDate(value_co,"yyyy-MM-dd"))
         {
           alert("申请日期格式不正确,请输入正确的时间格式！\nyyyy-MM-dd");
           co.focus();
           isCorrect=false; 
           return false;
         }
       } 
       co=document.getElementById("q31z1");       
       value_co=co.value;
       if(trim(value_co)=="")
       {
         alert("起始时间不能为空！");
         co.focus(); 
         isCorrect=false;
         return false;
       }else
       {
         if(!isDate(value_co,"yyyy-MM-dd"))
         {
           alert("起始时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd");
           co.focus();
           isCorrect=false; 
           return false;
         }else
         {
           start_date=value_co;
         }
       }  
       co=document.getElementById("q31z3");       
       value_co=co.value;
       if(trim(value_co)=="")
       {
         alert("终止时间不能为空！");
         co.focus(); 
         isCorrect=false;
         return false;
       }else
       {
         if(!isDate(value_co,"yyyy-MM-dd"))
         {
           alert("终止时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd");
           co.focus();
           isCorrect=false; 
           return false;
         }else
         {
           end_date=value_co;
         }
       }  
       co=document.getElementById("q3107");       
       value_co=co.value;
       if(trim(value_co)=="")
       {
         alert("说明不能为空！");
         co.focus(); 
         isCorrect=false;
         return false;
       } 
       if(start_date.length>0&&end_date.length>0)
       {
             var c="起始时间不能大于终止时间！";           
             if(start_date>end_date)
             {
               alert(c);
               isCorrect=false;
             }else{
                if ( confirm('确认此操作吗？') )
                {
                  isCorrect=true;
                }else
                {
                  isCorrect=false;
                }
             }
        }  
        return isCorrect;	 
    }
   var outObject;
   var weeks="";
   var feasts ="";
   var turn_dates="";
   var week_dates="";
   function getdate(tt,flag)
   {
     outObject=tt;     
     var hashvo=new ParameterSet();     
     hashvo.setValue("date",tt.value);  
     hashvo.setValue("flag",flag);     		
     var request=new Request({method:'post',onSuccess:showSelect,functionId:'1510010020'},hashvo);
   }
   function showSelect(outparamters)
   { 
     var tes=outparamters.getValue("re_date");     
     outObject.value=tes;
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
    function showDays(outparamters)
   {
     var days=outparamters.getValue("days");
     AjaxBind.bind($('days'),days);  
   }
   
   function getDays()
   {
   	  var hashvo=new ParameterSet();
   	  hashvo.setValue("type","06");
	  hashvo.setValue("app_date",$F('q3105')); 	   
	  hashvo.setValue("plan_id",$F('plan_id')); 	 
   	  var request=new Request({method:'post',onSuccess:showDays,functionId:'15502110017'},hashvo);
   }
</script>
<html:form action="/kq/kqself/apply/my_one_annual_apply" onsubmit="return validate()">
   <br>
  <table width="400" border="0" cellpadding="1" cellspacing="0" align="center">
          <tr height="20">
       	    <!--  <td width=10 valign="top" class="tableft"></td>
       	    <td width=130 align=center class="tabcenter"><bean:message key="kq.self.annual"/></td>
       	    <td width=10 valign="top" class="tabright"></td>
       	    <td valign="top" class="tabremain" width="500"></td>-->  
       	    <td align=center class="TableRow"><bean:message key="kq.self.annual"/></td>						
          </tr> 
          <tr>
          <td class="framestyle9">
            <table border="0" cellpmoding="0" cellspacing="5"  class="DetailTable"  cellpadding="0" >   
                <tr><td height="10"></td></tr> 
                  <tr><td height="25" align="right" class="tdFontcolor" nowrap >
                  计划:
                  </td>
                  <td class="tdFontcolor" nowrap>                      
	                   <!--<hrms:optioncollection name="annualApplyForm" property="plist" collection="list" />    
	                  <html:select name="annualApplyForm" property="plan_id" size="1" onchange="getDays();">
	                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
	                  </html:select> -->
	                  <bean:write name="annualApplyForm" property="plan_name"/>
	                  <html:hidden property="plan_id" name="annualApplyForm"/>
                   	  <span id="days"></span>
                  </td></tr> 
                <logic:iterate id="element" name="annualApplyForm"  property="flist" indexId="index"> 
                      <logic:equal name="element" property="visible" value="true">
                           <tr> 
                              <td align="right" class="tdFontcolor" nowrap >                
                                 <bean:write  name="element" property="itemdesc" filter="true"/>:
                              </td>             
                              <!--日期型 -->                            
                              <logic:equal name="element" property="itemtype" value="D">                              	
                                 <td align="left" class="tdFontcolor" nowrap>   
                                   <logic:match name="element" property="itemid" value="05">           
                                      <html:text name="annualApplyForm" property='<%="flist["+index+"].value"%>' styleId='${element.itemid}' size="20" maxlength="20" disabled="true" styleClass="TEXT4"/>
                                   </logic:match>
                              	   <logic:match name="element" property="itemid" value="z1">
                              	       <html:text name="annualApplyForm" property='<%="flist["+index+"].value"%>' styleId='${element.itemid}' size="20" maxlength="20" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false,false);' styleClass="TEXT4"/>
                              	   </logic:match>
                              	   <logic:match name="element" property="itemid" value="z3">
                              	        <html:text name="annualApplyForm" property='<%="flist["+index+"].value"%>' styleId='${element.itemid}'  size="20" maxlength="20" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false,false);' styleClass="TEXT4"/>
                              	   </logic:match>
                                 </td> 
                              </logic:equal>
                              <!--备注型 -->                              
                              <logic:equal name="element" property="itemtype" value="M">
                                 <td align="left" class="tdFontcolor" nowrap>                
                                    <html:textarea name="annualApplyForm" property='<%="flist["+index+"].value"%>' styleId='${element.itemid}'  cols="35" rows="4" styleClass="text5"/>
                                 </td>                           
                              </logic:equal>
                              <!--字符型 -->                                                    
                              <logic:equal name="element" property="itemtype" value="A">
                                 <td align="left" class="tdFontcolor" nowrap>
                                    <logic:notEqual name="element" property="codesetid" value="0">
                                       <html:hidden name="annualApplyForm" property='<%="flist["+index+"].value"%>' styleClass="text"/>                               
                                       <html:text name="annualApplyForm" property='<%="flist["+index+"].viewvalue"%>'  styleId='${element.itemid}'  size="31" maxlength="50" styleClass="TEXT4" onchange="fieldcode(this,2);"/>
                                       <img src="/images/code.gif" onclick='openInputCodeDialog("${element.codesetid}","<%="flist["+index+"].viewvalue"%>");'/>
                                    </logic:notEqual> 
                                    <logic:equal name="element" property="codesetid" value="0">
                                       <logic:equal name="element" property="itemid" value="q2903">
                                        <html:text name="annualApplyForm" property='<%="flist["+index+"].value"%>' disabled = "true" styleId='${element.itemid}'  size="31" maxlength="${element.itemlength}" styleClass="TEXT4"/> 
                                       </logic:equal>
                                       <logic:notEqual name="element" property="itemid" value="q2903">
                                        <html:text name="annualApplyForm" property='<%="flist["+index+"].value"%>'  styleId='${element.itemid}'  size="31" maxlength="${element.itemlength}" styleClass="TEXT4"/> 
                                       </logic:notEqual>
                                    </logic:equal>                               
                                  </td>                           
                                </logic:equal> 
                                <!--数据值-->                            
                                <logic:equal name="element" property="itemtype" value="N">
                                   <td align="left" class="tdFontcolor" nowrap>                
                                       <html:text name="annualApplyForm" property='<%="flist["+index+"].value"%>'  styleId='${element.itemid}'  size="31" maxlength="${element.itemlength}" styleClass="TEXT4"/>                               
                                   </td>                           
                                </logic:equal>                           
                              </tr>   
                        </logic:equal>  
                   </logic:iterate>
            <tr><td height="10"></td></tr>    
            <tr class="list3">
             <td align="center" colspan="4">
         	   		<!--szk直接报批 
         	   		<hrms:submit styleClass="mybutton" property="b_save">
            	   	<bean:message key="button.save"/>
	 	           </hrms:submit>
	 	            -->  
	 	           <hrms:submit styleClass="mybutton" property="b_Approval">
            	   	<bean:message key="button.appeal"/>
	 	           </hrms:submit>
	            	<html:reset styleClass="mybutton" property="reset"><bean:message key="button.clear"/></html:reset>	 
                     <input type="button" name="btnreturn" value='<bean:message key="kq.emp.button.return"/>' onclick="history.back();" class="mybutton">						      
             </td>
             </tr>    
            <tr><td height="10"></td></tr>         
 	      </table>	            	
      </td>
     </tr>                
   </table>
</html:form>
<script type="text/javascript">

getDays();

</script>




