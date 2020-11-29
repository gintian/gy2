<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="javascript">
     function validate()
     {
          var i=0;
          var start_date="";
          var end_date="";
	      var tag=false; 	   
           <logic:iterate  id="element"    name="appForm"  property="viewlist" indexId="index"> 
              <logic:equal name="element" property="itemtype" value="D">  
                <logic:notMatch name="element" property="itemid" value="z7">
                   var valueInputs=document.getElementsByName("<%="viewlist["+index+"].value"%>");
                   var dobj=valueInputs[0];                                     
                   if((dobj.value).length==0)
                   {
                    alert('<bean:message key="kq.rest.unull"/>');
                    tag=false;
                    return tag;
                   }else          
                  {
                     if(i==0)
                     {
                       if(!isDate(dobj.value,"yyyy-MM-dd HH:mm"))
                       {
                         alert("申请日期时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd HH:mm");
                         tag=false;
                         return tag;
                       }
                       }else
                       {
                         if(!isDate(dobj.value,"yyyy-MM-dd HH:mm"))
                          {
                            alert("时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd HH:mm");
                            tag=false;
                            return tag;
                          }else
                          {
                             if(i==1)
                             {
                                start_date=dobj.value;
                             }else if(i==2)
                             {
                                end_date=dobj.value;
                             }
                          }
                   
                       }
                  }
                  i++;
                </logic:notMatch>               
             </logic:equal>                
           </logic:iterate>  
          if(start_date.length>0&&end_date.length>0)
          {
             var c="起始时间不能大于或等于终止时间！";           
             if(start_date>=end_date)
             {
               alert(c);
               tag=false;
             }else{
               tag=true;
             }
          }
          var tab_name="${appForm.table}";
          tab_name=tab_name.toLowerCase()+"07";                   
          var co=document.getElementById(tab_name);        
          var value_co=co.value;  
          if(value_co=="")
          {
                alert("申请事由不能为空！");
                tag=false;
          } 
          var tab_name1="${appForm.table}";
          tab_name1=tab_name1.toLowerCase()+"03";
          var co1=document.getElementById(tab_name1);
          var value_co1=co1.value; 
          if(value_co1=="")
          {
          		obj = document.getElementById("appType");
          		if (obj != null && obj.value != "") {
                	alert(obj.value + "不能为空！");
                } else {
                	alert("请假类型不能为空！");
                }
                tag=false;
          }
          return tag;          
      }
      function backs()
      {
	  appForm.action="/kq/app_check_in/manuselect0.do?query=link";
    	  appForm.target="il_body";
          appForm.submit();
       }
       function backq()
       {
	  appForm.action="/kq/app_check_in/querycon.do?b_simplequery=link";
    	  appForm.target="il_body";
          appForm.submit();
       }
        function saveq(sub_flag)
       {
          if(validate())
          {
             appForm.action="/kq/app_check_in/add_app.do?b_save=link&sub_flag="+sub_flag;
    	     appForm.target="il_body";
             appForm.submit();
          }
	  
       }
  	function changeClassID(obj)
    {
       var class_id=obj.value;
       var hashvo=new ParameterSet();
       var z3 =document.getElementById("z3").value;
       hashvo.setValue("class_id",class_id);  
       hashvo.setValue("z3",z3);      
       var request=new Request({method:'post',asynchronous:false,onSuccess:setTiem,functionId:'1510030082'},hashvo);
    }
    function setTiem(outparamters)
    {
        var start_h=outparamters.getValue("start_h");
        var start_m=outparamters.getValue("start_m");
        var end_h=outparamters.getValue("end_h");
        var end_m=outparamters.getValue("end_m");     
        var class_id=outparamters.getValue("class_id");  
        var isspan=outparamters.getValue("isspan");      
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
        var timeE=timeS
        if(isspan=="true")
           timeE=timeE+1;
        timeS="0"+timeS;
        monthS="0"+monthS;
        timeS=timeS.substr(timeS.length-2);
        monthS=monthS.substr(monthS.length-2);
        var str_d=objDate.getYear()+"-"+monthS+"-"+timeS;
        var z1 =document.getElementById("z1").value;
        var z3 =document.getElementById("z3").value;	
        if(z1==""||z1.length<10)
        {
           z1=str_d;
        }else
        {
           z1=z1.substring(0,10);
        }  
        if(z3==""||z3.length<10)
        {
          z3=objDate.getYear()+"-"+monthS+"-"+timeE;
        }else
        {
           z3=z3.substring(0,10);
           if(isspan=="true")
           {
              if(z1==z3)
              {
                 z3=outparamters.getValue("z3"); 
              }
           }
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
     var request=new Request({method:'post',asynchronous:false,onSuccess:setkqcalendar,functionId:'15388800008'});
   }
   function setkqcalendar(outparamters)
   {
      weeks=outparamters.getValue("weeks");  
      feasts=outparamters.getValue("feasts"); 
      turn_dates=outparamters.getValue("turn_dates"); 
      week_dates=outparamters.getValue("week_dates");  
   }
   function IsDigit() 
    { 
       return ((event.keyCode >= 45) && (event.keyCode <= 57)); 
    } 
</script>
<html:form action="/kq/app_check_in/add_app" onsubmit="return validate()" >
   <br><br>
  <table width="400" border="0" cellpadding="1" cellspacing="0" align="center">
   <tr height="20">
    <!--  <td width=10 valign="top" class="tableft"></td>
    <td width=130 align=center class="tabcenter"><bean:message key="lable.overtime"/></td>
    <td width=10 valign="top" class="tabright"></td>
    <td valign="top" class="tabremain" width="500"></td> --> 
    <td colspan="4"  align="center" class="TableRow"><bean:message key="lable.overtime"/></td>            	      
   </tr> 
     <tr>
      <td colspan="4" class="framestyle9">
       <table border="0" cellpmoding="0" cellspacing="5"  class="DetailTable"  cellpadding="0" >   
                <tr><td height="10"></td></tr>   
                <logic:iterate id="element" name="appForm"  property="viewlist" indexId="index"> 
                      <logic:equal name="element" property="visible" value="true">
                           <tr>                                          
                              <!--日期型 -->                            
                              <logic:equal name="element" property="itemtype" value="D">
                                <td align="right" class="tdFontcolor" nowrap >                
                                 <bean:write  name="element" property="itemdesc" filter="true"/>:
                                </td>  
                                <logic:match name="element" property="itemid" value="05">
                              	    <td align="left" class="tdFontcolor" nowrap>                               	                
                                     <html:text name="appForm" property='<%="viewlist["+index+"].value"%>'  size="20" maxlength="20" disabled = "true" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);' styleClass="TEXT4"/>
                                     (时间格式：2007-10-01 08:30)
                                    </td>  
                              	</logic:match>
                              	<logic:match name="element" property="itemid" value="z1">
                                 <td align="left" class="tdFontcolor" nowrap>                
                                     <html:text name="appForm" property='<%="viewlist["+index+"].value"%>' styleId='z1'  size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);' styleClass="TEXT4"/>
                                 </td> 
                                 </logic:match>    
                                 <logic:match name="element" property="itemid" value="z3">
                                 <td align="left" class="tdFontcolor" nowrap>                
                                     <html:text name="appForm" property='<%="viewlist["+index+"].value"%>' styleId='z3'  size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);' styleClass="TEXT4"/>
                                 </td> 
                                 </logic:match>  
                                 <logic:match name="element" property="itemid" value="z7">
                              	    <td align="left" class="tdFontcolor" nowrap>                               	                
                                     <html:text name="appForm" property='<%="viewlist["+index+"].value"%>'  size="20" maxlength="20" disabled = "true"  styleClass="TEXT4"/>
                                    </td>  
                              	</logic:match>                    
                              </logic:equal>
                              <!--备注型 -->                              
                              <logic:equal name="element" property="itemtype" value="M">
                                 <td align="right" class="tdFontcolor" nowrap >                
                                    <bean:write  name="element" property="itemdesc" filter="true"/>:
                                 </td>  
                                 <td align="left" class="tdFontcolor" nowrap>                
                                    <html:textarea name="appForm" property='<%="viewlist["+index+"].value"%>' styleId='${element.itemid}' cols="35" rows="4" styleClass="text5"/>
                                 </td>                           
                              </logic:equal>
                              <!--字符型 -->                                                    
                              <logic:equal name="element" property="itemtype" value="A">
                                 <td align="right" class="tdFontcolor" nowrap >                
                                 <bean:write  name="element" property="itemdesc" filter="true"/>:
                                 </td>  
                                 <td align="left" class="tdFontcolor" nowrap>
                                    <logic:notEqual name="element" property="codesetid" value="0">
                                        <logic:equal name="element" property="readonly" value="false">
                             	 <logic:notEqual name="element" property="itemid" value="q1515">
                             	 <logic:notEqual name="element" property="itemid" value="q1115">
                             	 <logic:notEqual name="element" property="itemid" value="q1315">
                             	 <logic:notEqual name="element" property="itemid" value="q1511">
                             	 <logic:notEqual name="element" property="itemid" value="q1111">
                             	 <logic:notEqual name="element" property="itemid" value="q1311">   
                                <html:hidden name="appForm" property='<%="viewlist["+index+"].value"%>' styleClass="text"/> 
                                <input id="appType" type="hidden" name="appType" value="${element.itemdesc}"/>                              
                                <hrms:optioncollection name="appForm" property="salist" collection="list" />
	                               <html:select name="appForm" property="mess" size="1"  styleId='${element.itemid}'>
                                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                                </html:select>
                                </logic:notEqual>
                                </logic:notEqual>
                                </logic:notEqual>
                                </logic:notEqual>
                                </logic:notEqual> 
                                </logic:notEqual> 
                              </logic:equal>
                              <logic:equal name="element" property="itemid" value="q1515">
                              <html:hidden name="appForm" property='<%="viewlist["+index+"].value"%>' styleClass="text"/>
                              	 <hrms:optioncollection name="appForm" property="salistko" collection="list" />
	                               <html:select name="appForm" property="mess1" size="1" disabled = "false"  styleId='${element.itemid}'>
                                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                                </html:select> 
                              </logic:equal>
                              <logic:equal name="element" property="itemid" value="q1115">
                              <html:hidden name="appForm" property='<%="viewlist["+index+"].value"%>' styleClass="text"/>
                              	 <hrms:optioncollection name="appForm" property="salistko" collection="list" />
	                               <html:select name="appForm" property="mess1" size="1" disabled = "false"  styleId='${element.itemid}'>
                                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                                </html:select> 
                              </logic:equal>
                              <logic:equal name="element" property="itemid" value="q1315">
                              <html:hidden name="appForm" property='<%="viewlist["+index+"].value"%>' styleClass="text"/>
                              	 <hrms:optioncollection name="appForm" property="salistko" collection="list" />
	                               <html:select name="appForm" property="mess1" size="1" disabled = "false"  styleId='${element.itemid}'>
                                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                                </html:select> 
                              </logic:equal>
                               <logic:equal name="element" property="itemid" value="q1511">
                              <html:hidden name="appForm" property='<%="viewlist["+index+"].value"%>' styleClass="text"/>
                              	 <hrms:optioncollection name="appForm" property="salist11" collection="list" />
	                               <html:select name="appForm" property="mess2" size="1" disabled = "false"  styleId='${element.itemid}'>
                                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                                </html:select> 
                              </logic:equal>
                              <logic:equal name="element" property="itemid" value="q1111">
                              <html:hidden name="appForm" property='<%="viewlist["+index+"].value"%>' styleClass="text"/>
                              	 <hrms:optioncollection name="appForm" property="salist11" collection="list" />
	                               <html:select name="appForm" property="mess2" size="1" disabled = "false"  styleId='${element.itemid}'>
                                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                                </html:select> 
                              </logic:equal>
                              <logic:equal name="element" property="itemid" value="q1311">
                              <html:hidden name="appForm" property='<%="viewlist["+index+"].value"%>' styleClass="text"/>
                              	 <hrms:optioncollection name="appForm" property="salist11" collection="list" />
	                               <html:select name="appForm" property="mess2" size="1" disabled = "false"  styleId='${element.itemid}'>
                                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                                </html:select> 
                              </logic:equal> 
                                    </logic:notEqual> 
                                    <logic:equal name="element" property="codesetid" value="0">
                                       <logic:equal name="element" property="readonly" value="false">   
                                           <html:text name="appForm" property='<%="viewlist["+index+"].value"%>' styleId='${element.itemid}' size="31" maxlength="${element.itemlength}" styleClass="TEXT4"/> 
                                       </logic:equal>
                                        <logic:equal name="element" property="readonly" value="true">   
                                           <html:text name="appForm" property='<%="viewlist["+index+"].value"%>' disabled = "true" styleId='${element.itemid}' size="31" maxlength="${element.itemlength}" styleClass="TEXT4"/> 
                                       </logic:equal>
                                    </logic:equal>                               
                                  </td>                           
                                </logic:equal> 
                                <!--数据值-->                            
                                <logic:equal name="element" property="itemtype" value="N">
                                <logic:equal name="element" property="itemid" value="q1104">
                                   <td align="right" class="tdFontcolor" nowrap >                
                                       <bean:write  name="element" property="itemdesc" filter="true"/>:
                                   </td> 
                                   <td align="left" class="tdFontcolor" nowrap>                
                                      <hrms:optioncollection name="appForm" property="class_list" collection="list" />
	                                  <html:select name="appForm" property='<%="viewlist["+index+"].value"%>' onchange="changeClassID(this);" styleId="classid" size="1">
                                        <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                                      </html:select>
                                   </td>   
                                </logic:equal>
                                <logic:notEqual name="element" property="itemid" value="q1104">
                                <logic:notEqual name="element" property="itemid" value="q1304">
                                <logic:notEqual name="element" property="itemid" value="q1504">
                                   <td align="right" class="tdFontcolor" nowrap >                
                                    <bean:write  name="element" property="itemdesc" filter="true"/>:                                    
                                   </td>  
                                   <td align="left" class="tdFontcolor" nowrap>   
                                   <logic:equal name="element" property="itemdesc" value="休息扣除数">         
                                       <html:text name="appForm" property='<%="viewlist["+index+"].value"%>' size="20" maxlength="${element.itemlength}" onkeypress="event.returnValue=IsDigit();" styleClass="TEXT4"/>  分钟 <font color="red">(加班中间休息时长)</font>                            
                                   </logic:equal>  
                                   <logic:notEqual name="element" property="itemdesc" value="休息扣除数">         
                                       <html:text name="appForm" property='<%="viewlist["+index+"].value"%>' size="20" maxlength="${element.itemlength}" onkeypress="event.returnValue=IsDigit();" styleClass="TEXT4"/>                               
                                   </logic:notEqual>   
                                   </td>     
                                  </logic:notEqual>
                                  </logic:notEqual>
                                  </logic:notEqual>                      
                                </logic:equal>                           
                              </tr>   
                        </logic:equal>  
                   </logic:iterate>
           <tr><td height="10"></td></tr>    
 	    </table>	            	
           </td>
           </tr>
 		   <tr class="list3">
            <td align="center" colspan="4" style="height:35px;">  
               	<logic:equal name="appForm" property="table" value="Q11">
               	    <hrms:priv func_id="0C341a,27010a"> 
               	       <input type="button" class="mybutton"  name="dd" value='<bean:message key="button.report"/>' onclick="saveq('08');">   
               	    </hrms:priv>
               	     <hrms:priv func_id="0C341b,27010b"> 
               	       <input type="button" class="mybutton"  name="dd" value='<bean:message key="button.appeal"/>' onclick="saveq('02');">                	    
               	    </hrms:priv>
               	    <hrms:priv func_id="270102,0C3412"> 
               	     <input type="button" class="mybutton"  name="dd" value='<bean:message key="button.approve"/>' onclick="saveq('03');">                	    
               	    </hrms:priv>
               	</logic:equal> 
               	<logic:equal name="appForm" property="table" value="Q13">
               	     <hrms:priv func_id="0C343a,27012a"> 
               	     <input type="button" class="mybutton"  name="dd" value='<bean:message key="button.report"/>' onclick="saveq('08');"> 
               	    </hrms:priv>
               	     <hrms:priv func_id="0C343b,27012b"> 
               	     <input type="button" class="mybutton"  name="dd" value='<bean:message key="button.appeal"/>' onclick="saveq('02');">  
               	    </hrms:priv>
               	    <hrms:priv func_id="270122,0C3432"> 
               	     <input type="button" class="mybutton"  name="dd" value='<bean:message key="button.approve"/>' onclick="saveq('03');">                	    
               	    </hrms:priv>
               	</logic:equal> 
               	<logic:equal name="appForm" property="table" value="Q15">
               	     <hrms:priv func_id="0C342a,27011a"> 
               	     <input type="button" class="mybutton"  name="dd" value='<bean:message key="button.report"/>' onclick="saveq('08');"> 
               	    </hrms:priv>
               	     <hrms:priv func_id="0C342b,27011b"> 
               	     <input type="button" class="mybutton"  name="dd" value='<bean:message key="button.appeal"/>' onclick="saveq('02');">  
               	    </hrms:priv>
               	    <hrms:priv func_id="270112,0C3422"> 
               	     <input type="button" class="mybutton"  name="dd" value='<bean:message key="button.approve"/>' onclick="saveq('03');">                	    
               	    </hrms:priv>
               	</logic:equal>       	     
	 	         
	 	         <html:reset styleClass="mybutton" property="reset"><bean:message key="button.clear"/></html:reset>	 
             <logic:equal name="appForm" property="selectflag" value="0"> 
                <!--<hrms:submit styleClass="mybutton" property="br_return" onclick="validate('1')">
            	    <bean:message key="button.return"/>
	     	    </hrms:submit>
	     	    <input type="button" class="mybutton"  name="dd" value="<bean:message key="button.save"/>" onclick="saveq();">   
	     	    -->
	 	        <input type="button" class="mybutton"  name="dd" value="<bean:message key="button.return"/>" onclick="backs();">  
	 	     </logic:equal> 
	  	     <logic:equal name="appForm" property="selectflag" value="1">
	   	       <input type="button" class="mybutton"  name="dd" value="<bean:message key="button.return"/>" onclick="backq();">   
	        </logic:equal>    
             </td>
             </tr> 
      </table>
</html:form>



