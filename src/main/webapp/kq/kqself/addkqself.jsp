<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.kq.kqself.KqSelfForm" %>
<%@ page import="java.util.HashMap" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>

<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="javascript">
var isclass=false;
var flag_biaozhi = 0;
   function setIsclass()
   {
      isclass=true;
   }
    function validate()
    {
          var tag=false; 
	      var i=0;          
          if($F('sels')=="#"||$F('sels')=="")
          {
            alert('请选择申请类型!');
            return false;
          }
          var app_obj=document.getElementById("app_date"); 
          var z1_obj=document.getElementById("z1"); 
          var z3_obj=document.getElementById("z3"); 
          var start_date=z1_obj.value;
          var end_date=z3_obj.value;
          if(app_obj.value==""||z1_obj.value==""||z3_obj.value=="")
          {
             alert('<bean:message key="kq.rest.unull"/>');
             tag=false;
             return tag;
          }else 
          {
             if(!isDate(app_obj.value,"yyyy-MM-dd HH:mm"))
             {
                alert("申请日期时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd HH:mm");
                tag=false;
                return tag;
             }else  if(!isDate(z1_obj.value,"yyyy-MM-dd HH:mm"))
             {
                 alert("开始时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd HH:mm");
                 tag=false;
                 return tag;
             }else if(!isDate(z3_obj.value,"yyyy-MM-dd HH:mm"))
             {
                 alert("结束时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd HH:mm");
                 tag=false;
                 return tag;
             }else(start_date.length>0&&end_date.length>0)
             {
               var c="起始时间不能大于或等于终止时间！";           
               if(start_date>=end_date)
               {
                  alert(c);
                  tag=false;
                  return tag;
                 }else{
                   tag=true;
                 }
             }  
             
          }         
          var tab_name="${kqselfForm.table}";          
          tab_name=tab_name.toLowerCase()+"07";          
          var co=document.getElementById(tab_name);  
          if(co!=null)
          {
             var value_co=co.value; 
             if(trim(value_co)=="")
             {
                alert("申请事由不能为空！");
                tag=false;
                return tag;
             }
          }    
          var apptext="";
          var appid="";
          var appObj= document.getElementById("apptype");
          for(i=0;i<appObj.options.length;i++)
          {
             if(appObj.options[i].selected)
             {
    	       apptext=appObj.options[i].text;
    	       appid=appObj.options[i].value;
             }
          }
          if(isclass&&appid!="12")
          {
              var classObj= document.getElementById("classid");
              if(classObj!=null)
              {
                 var  classid=classObj.value;
                 if(classid==""||classid=="#")
                 {
                    //if(!confirm("您没有选择参考班次，可能会对该申请数据分析的不准确！\n是否确认继续？"))
                   // {
                    //   tag=false;
                    //   return tag;
                    //}
                 }
              }
          } 
          
		var hashvo=new ParameterSet();
  		hashvo.setValue("z1",z1_obj.value);
  		hashvo.setValue("z1str","开始日期");
		hashvo.setValue("z3",z3_obj.value);
		hashvo.setValue("z3str","结束日期");
		var request=new Request({asynchronous:false,onSuccess:returnResult,functionId:'1510010055'},hashvo);
         
         if (flag_biaozhi == 0) {
         	return false;
         } 
         
         request=new Request({asynchronous:false,onSuccess:returnResult2,functionId:'1510010056'},hashvo);
         if (flag_biaozhi == 0) {
         	return false;
         }
         
          if(!confirm("您申请的是"+apptext+"，请确认！"))
          {
             tag=false;
             return tag;
          }          
          return tag; 
    }
    
     function returnResult(outparamters) {
		var resultStr = outparamters.getValue("resultStr");
		resultStr = getDecodeStr(resultStr)
   		if (resultStr == "ok") {
   			flag_biaozhi = 1;
   		} else {
   			flag_biaozhi = 0;
   			alert(resultStr);
   		} 
   }
   
   function returnResult2(outparamters) {
		var resultStr = outparamters.getValue("resultStr");
		resultStr = getDecodeStr(resultStr)
   		if (resultStr == "ok") {
   			flag_biaozhi = 1;
   		} else {
   			flag_biaozhi = 0;
   			alert(resultStr);
   		} 
   }
	function changeClassID(obj)
    {
       var class_id=obj.value;
       var hashvo=new ParameterSet();
       hashvo.setValue("class_id",class_id);      
       var request=new Request({method:'post',asynchronous:false,onSuccess:setTiem,functionId:'1510030082'},hashvo);
    }
    function setTiem(outparamters)
    {
        var start_h=outparamters.getValue("start_h");
        var start_m=outparamters.getValue("start_m");
        var end_h=outparamters.getValue("end_h");
        var end_m=outparamters.getValue("end_m");     
        var class_id=outparamters.getValue("class_id");         
        var vo_obj;
        var s_hh="00";
        var s_mm="00";
        var e_hh="00";
        var e_mm="00";
        if(start_h!="")
        {
          s_hh=start_h;
        }
        if(start_m!="")
        {
          s_mm=start_m;
        }
        if(end_h!="")
        {
          e_hh=end_h;
        }
        if(end_m!="")
        {
          e_mm=end_m;
        }          
        var objDate = new Date();
        var monthS=objDate.getMonth()+1;
        var timeS=objDate.getDate();
        timeS="0"+timeS;
        monthS="0"+monthS;
        timeS=timeS.substr(timeS.length-2);
        monthS=monthS.substr(monthS.length-2);
        var str_d=objDate.getYear()+"-"+monthS+"-"+timeS; 
        var z1 =document.getElementById("z1").value
        var z3 =document.getElementById("z3").value	
        if(z1==""||z1.length<10)
        {
           z1=str_d;
        }else
        {
           z1=z1.substring(0,10);
        }  
        if(z3==""||z3.length<10)
        {
           z3=str_d;
        }else
        {
           z3=z3.substring(0,10);
        }
        if(class_id!=""&&class_id!="#")
        {
           document.getElementById("z1").value=z1+" "+s_hh+":"+s_mm;
           document.getElementById("z3").value=z3+" "+e_hh+":"+e_mm;
           //document.getElementById("z1").disabled=true;
           //document.getElementById("z3").disabled=true; 
        }else
        {
           document.getElementById("z1").value="";
           document.getElementById("z3").value="";
           //document.getElementById("z1").disabled=false;
           //document.getElementById("z3").disabled=false;            
        }
                
    }
</script>
<script language="javascript">
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
   	  var tabid=$F('tabid');
   	  if(tabid!="Q15")
   	    return;
   	  var hashvo=new ParameterSet();
   	  hashvo.setValue("date_type","D");
   	  hashvo.setValue("type",$F('sels'));
	  hashvo.setValue("app_date",$F('app_date')); 	   
   	  var request=new Request({method:'post',onSuccess:showDays,functionId:'1510060001'},hashvo);
   }
   function appealData()
   {
       
   }  
   
   function goback()
   {
      kqselfForm.action="/kq/kqself/search_kqself.do?b_query=link";
      kqselfForm.submit();
   }
   function IsDigit() 
    { 
       return ((event.keyCode >= 45) && (event.keyCode <= 57)); 
    } 
    function clueon(id)
    {
       if(id!="")
         alert("申请成功！");
    }
    function printT(id)
    {
       var tab_name="${kqselfForm.table}";       
       var win=window.open("/servlet/OutputKqTemplateDataServlet?tab_name="+tab_name+"&id="+id,"_blank");
    }
</script>
<%
KqSelfForm kqselfForm=(KqSelfForm)session.getAttribute("kqselfForm");
HashMap taskmap=kqselfForm.getTaskMap();
String id=kqselfForm.getId();

//pageContext.setAttribute("parammap", taskmap);
%>

<html:form action="/kq/kqself/addkqself" onsubmit="return validate()">
   <br><br>
   <html:hidden name="kqselfForm" styleId="tabid" property="table"/>

  <table width="400" border="0" cellpadding="1" cellspacing="0" align="center">
          <tr height="20">
       	    <!-- <td width=10 valign="top" class="tableft"></td>
       	    <td width=130 align=center class="tabcenter"><bean:message key="lable.overtime"/></td>
       	    <td width=10 valign="top" class="tabright"></td>
       	    <td valign="top" class="tabremain" width="500"></td> -->  
       	     <td align=center class="TableRow"><bean:message key="lable.overtime"/></td>            	      
          </tr> 
          <tr>
          <td  class="framestyle9" style="border-top-style: solid;border-top-width: 0px;">
            <table border="0" cellpmoding="0" cellspacing="5"  class="DetailTable"  cellpadding="0" >   
                <tr><td height="10"></td></tr> 
                 <logic:equal name="kqselfForm" property="table" value="Q15">
                    <logic:notEqual name="kqselfForm" property="rule_day" value="">
                       <tr><td></td>
                       <td class="tdFontcolor" nowrap>     
                              <bean:message key="kq.Leavetime_rule.enrol"/> ${kqselfForm.rule_day}<bean:message key="kq.Overtime_rule.day"/>
                       </td></tr>       
                     </logic:notEqual> 
                 </logic:equal>  
                <logic:iterate id="element" name="kqselfForm"  property="fieldlist" indexId="index">                 
                      <logic:equal name="element" property="visible" value="true">
                           <tr>                                          
                              <!--日期型 -->                            
                              <logic:equal name="element" property="itemtype" value="D">
                                <td align="right" class="tdFontcolor" nowrap >                
                                 <bean:write  name="element" property="itemdesc" filter="true"/>:
                                </td>  
                              	<logic:match name="element" property="itemid" value="05">
                              	<td align="left" class="tdFontcolor" nowrap>                
                                     <html:text name="kqselfForm" styleId="app_date"  property='<%="fieldlist["+index+"].value"%>' disabled = "true" size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);' styleClass="TEXT4"/>
                                 (时间格式：2007-10-01 08:30)
                                 </td>  
                              	</logic:match>
                              	<logic:match name="element" property="itemid" value="z1">
                                   <td align="left" class="tdFontcolor" nowrap> 
                                     <html:text name="kqselfForm" property='<%="fieldlist["+index+"].value"%>' styleId='z1' size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);' ondblclick="getdate(this,'z1');" styleClass="TEXT4"/>
                                    </td> 
                                 </logic:match>    
                                 <logic:match name="element" property="itemid" value="z3">
                                   <td align="left" class="tdFontcolor" nowrap>                
                                     <html:text name="kqselfForm" property='<%="fieldlist["+index+"].value"%>' styleId='z3' size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);' ondblclick="getdate(this,'z3');" styleClass="TEXT4"/>
                                   </td> 
                                 </logic:match>                      
                              </logic:equal>
                              <!--备注型 -->                              
                              <logic:equal name="element" property="itemtype" value="M">
                                <td align="right" class="tdFontcolor" nowrap >                
                                 <bean:write  name="element" property="itemdesc" filter="true"/>:
                                </td>  
                                 <td align="left" class="tdFontcolor" nowrap>                
                                    <html:textarea name="kqselfForm" property='<%="fieldlist["+index+"].value"%>' styleId='${element.itemid}' cols="35" rows="4" styleClass="text5"/>
                               </td>   
                                                         
                              </logic:equal>
                              <!--字符型 -->                                                    
                              <logic:equal name="element" property="itemtype" value="A">
                                 <td align="right" class="tdFontcolor" nowrap >                
                                   <bean:write  name="element" property="itemdesc" filter="true"/>:
                                 </td>  
                                 <td align="left" class="tdFontcolor" nowrap>
                                    <logic:notEqual name="element" property="codesetid" value="0">
                                       <html:hidden name="kqselfForm" property='<%="fieldlist["+index+"].value"%>'  value="sss" styleClass="text"/>                               
                                       <hrms:optioncollection name="kqselfForm" property="selist" collection="list" />
	                                      <html:select name="kqselfForm" property="sels" size="1" onchange="getDays();" styleId="apptype">
                                         <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                                        </html:select> 
                                        <span id="days"></span>                                       
                                    </logic:notEqual> 
                                    <logic:equal name="element" property="codesetid" value="0">
                                         <html:textarea name="kqselfForm" property='<%="fieldlist["+index+"].value"%>'  styleId='${element.itemid}' cols="35" rows="4" styleClass="text5"/>
                                    </logic:equal>                               
                                  </td>                           
                                </logic:equal> 
                                <!--数据值-->                            
                                <logic:equal name="element" property="itemtype" value="N">
                                 <logic:match name="element" property="itemid" value="04">                                       
                                   <logic:equal name="kqselfForm" property="table" value="Q11">   
                                     <td align="right" class="tdFontcolor" nowrap >                
                                       <bean:write  name="element" property="itemdesc" filter="true"/>:
                                     </td>
                                     <td align="left" class="tdFontcolor" nowrap >    
                                     <script language="javascript">
                                           setIsclass();
                                      </script>
 
                                        <hrms:optioncollection name="kqselfForm" property="class_list" collection="list" />
	                                      <html:select name="kqselfForm" property='<%="fieldlist["+index+"].value"%>' onchange="changeClassID(this);" styleId="classid" size="1">
                                          <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                                        </html:select>  
                                      </td>                          
                                     </logic:equal>                                    
                                     <logic:equal name="kqselfForm" property="table" value="Q15">    
                                        <td align="right" class="tdFontcolor" nowrap >                
                                         <bean:write  name="element" property="itemdesc" filter="true"/>:
                                        </td>
                                        <td align="left" class="tdFontcolor" nowrap >   
                                        <script language="javascript">
                                           setIsclass();
                                        </script>
                                        <hrms:optioncollection name="kqselfForm" property="class_list" collection="list" />
	                                      <html:select name="kqselfForm" property='<%="fieldlist["+index+"].value"%>' onchange="changeClassID(this);"  styleId="classid" size="1">
                                          <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                                        </html:select>   
                                        </td>                
                                     </logic:equal>
                                      
                                   </logic:match>                       
                                   <logic:notMatch name="element" property="itemid" value="04"> 
                                     <td align="right" class="tdFontcolor" nowrap >                
                                     <bean:write  name="element" property="itemdesc" filter="true"/>:
                                     </td>  
                                     <td align="left" class="tdFontcolor" nowrap>       
                                                
                                         
                                         <logic:equal name="element" property="itemdesc" value="休息扣除数">         
                                               <html:text name="kqselfForm" property='<%="fieldlist["+index+"].value"%>' size="20" maxlength="${element.itemlength}" onkeypress="event.returnValue=IsDigit();" styleClass="TEXT4"/>                                分钟 <font color="red">(加班中间休息时长)</font>                            
                                         </logic:equal>  
                                         <logic:notEqual name="element" property="itemdesc" value="休息扣除数">         
                                           <html:text name="kqselfForm" property='<%="fieldlist["+index+"].value"%>' size="20" maxlength="${element.itemlength}" onkeypress="event.returnValue=IsDigit();" styleClass="TEXT4"/>                                                          
                                         </logic:notEqual> 
                                     </td>
                                   </logic:notMatch>
                                </logic:equal>                           
                              </tr>   
                        </logic:equal>  
                   </logic:iterate>
           <tr><td height="10"></td></tr>    
            <tr class="list3">
             <td align="center" colspan="4">
             
             </td>
             </tr>    
            <tr><td height="10"></td></tr>         
 	    </table>	            	
           </td>
           </tr>
 
                      
      </table>
      
      <table align="center">
      	<tr>
      		<td style="height:35px;">
      			<logic:equal name="kqselfForm" property="table" value="Q15">
         	       <!--<hrms:submit styleClass="mybutton" property="b_save">
            		<bean:message key="button.save"/>
	 	           </hrms:submit>-->
	 	         <hrms:priv func_id="0B221">   
	 	           <hrms:submit styleClass="mybutton" property="b_report">
            		<bean:message key="button.report"/>
	 	           </hrms:submit>
	 	         </hrms:priv>  
	 	        <hrms:priv func_id="0B222">    	
         	       <hrms:submit styleClass="mybutton" property="b_appeal">
            		<bean:message key="button.appeal"/>
	 	           </hrms:submit>	
	 	        </hrms:priv>     	 
	 	     </logic:equal> 
	 	     <logic:equal name="kqselfForm" property="table" value="Q11">
         	       <!--<hrms:submit styleClass="mybutton" property="b_save">
            		<bean:message key="button.save"/>
	 	           </hrms:submit>-->
	 	         <hrms:priv func_id="0B211">   
	 	           <hrms:submit styleClass="mybutton" property="b_report">
            		<bean:message key="button.report"/>
	 	           </hrms:submit>
	 	         </hrms:priv>  
	 	        <hrms:priv func_id="0B212">    	
         	       <hrms:submit styleClass="mybutton" property="b_appeal">
            		<bean:message key="button.appeal"/>
	 	           </hrms:submit>	
	 	        </hrms:priv>     	 
	 	     </logic:equal> 
	 	     <logic:equal name="kqselfForm" property="table" value="Q13">
         	       <!--<hrms:submit styleClass="mybutton" property="b_save">
            		<bean:message key="button.save"/>
	 	           </hrms:submit>-->
	 	         <hrms:priv func_id="0B231">   
	 	           <hrms:submit styleClass="mybutton" property="b_report">
            		<bean:message key="button.report"/>
	 	           </hrms:submit>
	 	         </hrms:priv>  
	 	        <hrms:priv func_id="0B232">    	
         	       <hrms:submit styleClass="mybutton" property="b_appeal">
            		<bean:message key="button.appeal"/>
	 	           </hrms:submit>	
	 	        </hrms:priv>     	 
	 	     </logic:equal> 
	 	     <logic:notEqual name="kqselfForm" property="id" value="">    
	 	       <logic:equal name="kqselfForm" property="isTemplate" value="1">    
	 	        <input type="button" name="btnreturn" value='打印' onclick="printT('<%=id%>');" class="mybutton">	
	 	       </logic:equal>				      
	 	     </logic:notEqual>
	 	    <html:reset styleClass="mybutton" property="reset"><bean:message key="button.clear"/></html:reset>	 
	 	    
         	 <%--<hrms:fixflowbutton name="加班" url="${kqselfForm.taskurl}" parammap="<%=taskmap%>" formname="kqselfForm" js_flag="1"/>--%>
         	 <input type="button" name="btnreturn" value='<bean:message key="kq.emp.button.return"/>' onclick="goback();" class="mybutton">						      
      		</td>
      	</tr>
      </table>
</html:form>
<script type="text/javascript">
<!--
getDays();
//-->
clueon('<%=id%>');
</script>


