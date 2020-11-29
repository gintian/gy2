<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hjsj.hrms.actionform.kq.team.KqClassArrayForm" %>
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
<script language="javascript" src="/js/function.js"></script>
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

   function checkInputDate(dateObj, isStart) 
   {
	   if (!isDate(dateObj.value, "yyyy-MM-dd") && !isDate(dateObj.value, "yyyy.MM.dd")) {
		   dateObj.focus();
		   if (isStart)
			   msg = STARTDATE_ERROR;
		   else
			   msg = ENDDATE_ERROR;
		   msg +=  "\n" + DATE_FORMATTER_EXAMPLE + "\n" + DATE_FORMATTER_EXAMPLE_DOT;
           alert(msg);
           return false;
       }

       return true;
   }
   
   function base_shift()
   {	var hashvo=new ParameterSet();
   		var start_dateObj = document.getElementsByName("start_date")[0];
   		if (!checkInputDate(start_dateObj,true))
   		    return;
   		
   		var end_dateObj = document.getElementsByName("end_date")[0];
   		if (!checkInputDate(end_dateObj,false))
            return;

   		document.getElementById("shiftSubmit").disabled = "disabled";
   		
   		var a_code = document.getElementsByName("a_code")[0];
   		var nbase = document.getElementsByName("nbase")[0];
   		hashvo.setValue("z1",start_dateObj.value);
  		hashvo.setValue("z1str","<bean:message key='kq.shift.start_date'/>");
  		hashvo.setValue("z3",end_dateObj.value);
  		hashvo.setValue("z3str","<bean:message key='kq.shift.end_date'/>");
  		hashvo.setValue("a_code", a_code.value);
  		hashvo.setValue("nbase", nbase.value);
   		var request=new Request({asynchronous:false,onSuccess:returnResult,functionId:'1510010055'},hashvo);
   }
   
    function returnResult(outparamters) {
		var resultStr = outparamters.getValue("resultStr");
		resultStr = getDecodeStr(resultStr);
   		if (resultStr == "ok") {
			kqClassArrayForm.action="/kq/team/array/normal_array_data.do?b_save=link"; 
			kqClassArrayForm.target="_self";      			
			kqClassArrayForm.submit();
			var waitInfo=eval("wait");
			waitInfo.style.display="block"; 
   		} else {
   			alert(resultStr);
   			document.getElementById("shiftSubmit").disabled = "";
   			return ;
   		} 
   }
</script>
<html:form action="/kq/team/array/normal_array_data">
<div id='wait' style='position:absolute;top:30%;left:50%;display:none;margin-left:-200px; '>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr class="TableRow common_background_color">
             <td class="tableRow" height=24>正在排班，请稍候...</td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10" >
                 <table cellspacing="1" cellpadding="0">
                   <tr height=8>
                     <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
        </table>
</div>
<div class="fixedDiv3">
<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!--  <td width="10" valign="top" class="tableft"></td>
       		
          <td width="130" align=center class="tabcenter"><bean:message key="kq.shift.normal"/></td>
       		<td width="10" valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="250"></td> -->
       		<td align=center class="TableRow"><bean:message key="kq.shift.normal"/></td>             	      
          </tr> 
          <tr>
            <td  class="framestyle9">
               <table width="266" border="0" align="center"  cellpadding="0" cellspacing="5"  class="DetailTable" cellpmoding="0" >
        <tr> 
          <td width="14" align="right" nowrap class="tdFontcolor"></td>
          <td width="72" align="left" nowrap class="tdFontcolor"><bean:message key="kq.shift.start_date"/>:</td>
          <td width="160" align="left" nowrap class="tdFontcolor"> 
          
           <html:text name="kqClassArrayForm" property='start_date'  size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);" styleClass="TEXT4"/>
            &nbsp; </td>
        </tr>
        <tr> 
		  <td align="right" nowrap class="tdFontcolor">&nbsp;&nbsp;&nbsp;&nbsp;</td>
          <td  align="left" nowrap class="tdFontcolor" > <bean:message key="kq.shift.end_date"/>: </td>
          <td align="left" class="tdFontcolor" nowrap>
           <html:text name="kqClassArrayForm" property='end_date'  size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);" styleClass="TEXT4"/>
           </td>
          <!--数据值-->
        </tr>
        <tr> 
		  <td align="right" nowrap class="tdFontcolor"></td>
          <td  align="left" nowrap class="tdFontcolor" > <bean:message key="kq.shift.baseclass"/>: </td>
          <td align="left" class="tdFontcolor" nowrap> 
           <html:select name="kqClassArrayForm" property="selected_class" size="0">
           <html:optionsCollection property="classlist" value="dataValue" label="dataName"/>
           </html:select> 
          </td>
          <!--数据值-->
        </tr>
        <tr> 
		   <td align="right" nowrap class="tdFontcolor"></td>
          <td  align="left" nowrap class="tdFontcolor"><bean:message key="kq.shift.rest_postpone"/>: </td>
          <td align="left" class="tdFontcolor" nowrap> 
          <html:multibox name="kqClassArrayForm" property="rest_postpone" value="1"/>
          
          </td>
        </tr>
        <tr> 
		  <td align="right" nowrap class="tdFontcolor"></td>
          <td align="left" nowrap class="tdFontcolor"><bean:message key="kq.shift.feast_postpone"/>: </td>
          <td align="left" class="tdFontcolor" nowrap> 
          <html:multibox name="kqClassArrayForm" property="feast_postpone" value="1"/>
           <html:hidden name="kqClassArrayForm" property="a_code" styleClass="text"/> 
         <html:hidden name="kqClassArrayForm" property="nbase" styleClass="text"/>
          </td>
        </tr>
         <% 
            KqClassArrayForm kqClassArrayForm = (KqClassArrayForm) session.getAttribute("kqClassArrayForm"); 
          	 String a_code = kqClassArrayForm.getA_code();
          	 String code = a_code.substring(0,2);
          	 if (code.equalsIgnoreCase("gp")) {
          %>
         
          <%} %>
       
      </table>	            	
            </td>
          </tr>
         
  <tr align="center" class="list3"> 
    <td height="35" > 
      <input id="shiftSubmit" type="button" name="b_shift" value="<bean:message key="button.ok"/>" class="mybutton" onclick="base_shift();">
               <input type="reset" name="bc_clear" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="window.close();">
    </td>
          </tr>  
  </table>
  </div>
</html:form>