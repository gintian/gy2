<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/kq/kq.js"></script>
<script language="javascript">
    	var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>
<script language="javascript">
   var outObject;
   var weeks="";
   var feasts ="";
   var turn_dates="";
   var week_dates="";
   
   function change(){
	   var start_value = document.getElementById("start_date").value;
	   start_value = start_value.replace(/-/g,".");
	   document.getElementById("start_date").value = start_value;
       var end_value=document.getElementById("end_date").value;
       end_value = end_value.replace(/-/g,".");
	   document.getElementById("end_date").value = end_value;
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
   function selectTrans()
   {
       var thevo=new Object();
       var start_obj=document.getElementById("start_date");
       var end_obj=document.getElementById("end_date");
       var start_date=start_obj.value;
       var end_date=end_obj.value;
       if(start_obj.value=="")
       {
          alert('起始日期不能为空！');
          return false;
       }else if(end_obj.value=="")
       {
          alert('结束日期不能为空！');
          return false;
       }else
       {
             if(!isDate(start_obj.value,"yyyy-MM-dd") && !isDate(start_obj.value,"yyyy.MM.dd"))
             {
                alert("起始日期时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd");
                return false;
             }else  if(!isDate(end_obj.value,"yyyy-MM-dd") && !isDate(end_obj.value,"yyyy.MM.dd"))
             {
                 alert("结束日期格式不正确,请输入正确的时间格式！\nyyyy-MM-dd");
                 return false;
             }else(start_date.length>0&&end_date.length>0)
             {
               var c="起始时间不能大于或等于终止时间！";           
               if(start_date>end_date)
               {
                  alert(c);
                  return false;
               }
             } 
       } 
       thevo.start_date=start_date;
       thevo.end_date=end_date;
       var a_code="${kqShiftForm.a_code}";
       var nbase_id="${kqShiftForm.nbase}";
       if(a_code.indexOf("EP")==-1)
       {
          var nbase_obj=document.getElementById("nbase");
          for(var i=0;i<nbase_obj.options.length;i++)
          {
             if(nbase_obj.options[i].selected)
             {
    	        nbase_id=nbase_obj.options[i].value;
    	        break;
             }
          }
       }       
       thevo.nbase=nbase_id;       
       thevo.a_code="${kqShiftForm.a_code}";
       thevo.flag="true";
       window.returnValue=thevo;
   }
</script>
<html:form action="/kq/team/array/excel_shift_data">
<div class="fixedDiv3">
<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!--  <td width="10" valign="top" class="tableft"></td>
       		
          <td width="130" align=center class="tabcenter">排班模板</td>
       		<td width="10" valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="200"></td> --> 
       		<td align=center class="TableRow">排班模板</td>            	      
          </tr> 
          <tr>
            <td class="framestyle9" >
               <table width="266" border="0" align="center"  cellpadding="0" cellspacing="5"  class="DetailTable" cellpmoding="0" >
    
        <tr> 
       
          <td width="72" align="left" nowrap class="tdFontcolor">当前对象:</td>
          <td width="200" align="left" nowrap class="tdFontcolor"> 
            <bean:write name="kqShiftForm" property="code_mess" />
          </td>
        </tr>
       
        <tr> 
        
          <td width="72" align="left" nowrap class="tdFontcolor"><bean:message key="kq.shift.start_date"/>:</td>
          <td width="200" align="left" nowrap class="tdFontcolor"> 
          
           <html:text name="kqShiftForm" property='start_date'  size="20" maxlength="20" styleId='start_date' onchange='change()'  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' styleClass="TEXT4"/>
            &nbsp; </td>
        </tr>
        <tr> 
		 
          <td  align="left" nowrap class="tdFontcolor" > <bean:message key="kq.shift.end_date"/>: </td>
          <td align="left" class="tdFontcolor" nowrap>
           <html:text name="kqShiftForm" property='end_date'  size="20" maxlength="20" styleId='end_date' onchange='change()' onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' styleClass="TEXT4"/>
           </td>
          <!--数据值-->
          <html:hidden name="kqShiftForm" property="a_code" styleClass="text"/>
        </tr>
            <logic:notMatch name="kqShiftForm" property="a_code" value="EP">
        <tr> 
		 
          <td  align="left" nowrap class="tdFontcolor" > </td>
          <td align="left" class="tdFontcolor" nowrap>           
         
            <html:select name="kqShiftForm" property="nbase" styleId="nbase" size="0">
             <html:optionsCollection property="nbase_list" value="dataValue" label="dataName"/>
            </html:select> 
          </td>
          <!--数据值-->
        </tr>
        </logic:notMatch>
      </table>	            	
            </td>
          </tr>
  <tr align="center" class="list3"> 
    <td height="35" > 
      <input type="button" name="b_shift" value="<bean:message key="button.ok"/>" class="mybutton" onclick="selectTrans();window.close();">
               <input type="reset" name="bc_clear" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="window.close();">
    </td>
          </tr>  
  </table>
  </div>
</html:form>
<script language="javascript">
hide_nbase_select('nbase');
</script>