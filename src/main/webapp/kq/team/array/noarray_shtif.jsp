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
<%
	String a_code = request.getParameter("a_code");
	String nbase = request.getParameter("nbase");
%>
<script language="javascript">
   var flag_biaozhi  = 0;
   function base_shift()
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
             start_date=start_date.replace('.', '-');
             start_date=start_date.replace('.', '-');
             end_date=end_date.replace('.', '-');
             end_date=end_date.replace('.', '-');            
             if(!isDate(start_date,"yyyy-MM-dd"))
             {
                alert("起始日期时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd");
                return false;
             }else  if(!isDate(end_date,"yyyy-MM-dd"))
             {
                 alert("结束日期格式不正确,请输入正确的时间格式！\nyyyy-MM-dd");
                 return false;
             }else(start_date.length>0&&end_date.length>0)
             {
               var c="起始时间不能大于终止时间！";           
               if(start_date>end_date)
               {
                  alert(c);
                  return false;
               }
             } 
       } 
       thevo.start_date=start_date;
       thevo.end_date=end_date;
       var a_code = $F('a_code');
       var nbase = $F('nbase');
	   var hashvo=new ParameterSet();
  	   hashvo.setValue("z1",start_date);
  	   hashvo.setValue("z1str","开始日期");
       hashvo.setValue("z3",end_date);
  	   hashvo.setValue("z3str","结束日期");
  	   hashvo.setValue("a_code",a_code);
  	   hashvo.setValue("nbase",nbase);
   	   var request=new Request({asynchronous:false,onSuccess:returnResult,functionId:'1510010055'},hashvo);
       
       if (flag_biaozhi == 0) {
       	return ;
       }
       var class_obj=document.getElementById("selected_class");
       var class_id="";
       for(var i=0;i<class_obj.options.length;i++)
       {
             if(class_obj.options[i].selected)
             {
    	        class_id=class_obj.options[i].value;
    	        break;
             }
       }
       thevo.class_id=class_id;
       var vo_obj= document.getElementById('rest_postpone');
       if(vo_obj.checked)
       {
         thevo.rest_postpone="1";
       }else
       {
         thevo.rest_postpone="0";
       }
       vo_obj= document.getElementById('feast_postpone');
       if(vo_obj.checked)
       {
         thevo.feast_postpone="1";
       }else
       {
         thevo.feast_postpone="0";
       }
       thevo.flag="true";
       window.returnValue=thevo;
       window.close();
   }
   
     function returnResult(outparamters) {
		var resultStr = outparamters.getValue("resultStr");
		resultStr = getDecodeStr(resultStr)
   		if (resultStr == "ok") {
   			 flag_biaozhi  = 1;
   		} else {
   			flag_biaozhi  = 0;
   			alert(resultStr);
   		} 
   }
</script>
<html:form action="/kq/team/array/normal_noarray_data">
<div class="fixedDiv3">
<input type="hidden" id="a_code" value="<%=a_code %>"/>
<input type="hidden" id="nbase" value="<%=nbase %>"/>
<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!--  <td width="10" valign="top" class="tableft"></td>
       		
          <td width="130" align=center class="tabcenter"><bean:message key="kq.shift.normal"/></td>
       		<td width="10" valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>--> 
       		<td align=center class="TableRow"><bean:message key="kq.shift.normal"/></td>             	      
          </tr> 
          <tr>
            <td class="framestyle9">
               <table width="266" border="0" align="center"  cellpadding="0" cellspacing="5"  class="DetailTable" cellpmoding="0" >
        <tr> 
          <td width="14" align="right" nowrap class="tdFontcolor"></td>
          <td width="72" align="left" nowrap class="tdFontcolor"><bean:message key="kq.shift.start_date"/>:</td>
          <td width="160" align="left" nowrap class="tdFontcolor"> 
          
           <html:text name="kqClassArrayForm" property='start_date' styleId='start_date' size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);" styleClass="TEXT4"/>
            &nbsp; </td>
        </tr>
        <tr> 
		  <td align="right" nowrap class="tdFontcolor">&nbsp;&nbsp;&nbsp;&nbsp;</td>
          <td  align="left" nowrap class="tdFontcolor" > <bean:message key="kq.shift.end_date"/>: </td>
          <td align="left" class="tdFontcolor" nowrap>
           <html:text name="kqClassArrayForm" property='end_date' styleId='end_date' size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);" styleClass="TEXT4"/>
           </td>
          <!--数据值-->
        </tr>
        <tr> 
		  <td align="right" nowrap class="tdFontcolor"></td>
          <td  align="left" nowrap class="tdFontcolor" > <bean:message key="kq.shift.baseclass"/>: </td>
          <td align="left" class="tdFontcolor" nowrap> 
           <html:select name="kqClassArrayForm" property="selected_class" styleId='selected_class' size="0">
           <html:optionsCollection property="classlist" value="dataValue" label="dataName"/>
           </html:select> 
          </td>
          <!--数据值-->
        </tr>
        <tr> 
		   <td align="right" nowrap class="tdFontcolor"></td>
          <td  align="left" nowrap class="tdFontcolor"><bean:message key="kq.shift.rest_postpone"/>: </td>
          <td align="left" class="tdFontcolor" nowrap> 
          <html:multibox name="kqClassArrayForm" property="rest_postpone" styleId='rest_postpone' value="1"/>
          
          </td>
        </tr>
        <tr> 
		  <td align="right" nowrap class="tdFontcolor"></td>
          <td align="left" nowrap class="tdFontcolor"><bean:message key="kq.shift.feast_postpone"/>: </td>
          <td align="left" class="tdFontcolor" nowrap> 
          <html:multibox name="kqClassArrayForm" property="feast_postpone" styleId='feast_postpone' value="1"/>
          
          </td>
        </tr>
      </table>	            	
            </td>
          </tr>
  <tr align="center" class="list3"> 
    <td height="35" > 
      <input type="button" name="b_shift" value="<bean:message key="button.ok"/>" class="mybutton" onclick="base_shift();">
               <input type="reset" name="bc_clear" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="window.close();">
    </td>
          </tr>  
          </div>
  </table>
</html:form>