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
   function base_shift()
   {
   	if(confirm("确定自动分配班组吗？"))
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
               var c="起始时间不能大于或等于终止时间！";           
               if(start_date>=end_date)
               {
                  alert(c);
                  return false;
               }
             } 
       }
       var waitInfo=eval("wait");	   
	   waitInfo.style.display="block"; 
       thevo.start_date=start_date;
       thevo.end_date=end_date;       
       thevo.flag="true";
       window.returnValue=thevo;
       window.close();
   	}
   }
</script>
<html:form action="/kq/team/array_group/load_zidong_class">
	<div class="fixedDiv3">
	<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!--  <td width="10" valign="top" class="tableft"></td>
       		
          <td width="130" align=center class="tabcenter"><bean:message key="kq.shift.normal"/></td>
       		<td width="10" valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td> --> 
       		<td align=center class="TableRow"><bean:message key="kq.shift.normal"/></td>            	      
          </tr> 
          <tr>
            <td  class="framestyle9">
               <table width="266" border="0" align="center"  cellpadding="0" cellspacing="5"  class="DetailTable" cellpmoding="0" >
        <tr> 
          <td width="14" align="right" nowrap class="tdFontcolor"></td>
          <td width="72" align="left" nowrap class="tdFontcolor"><bean:message key="kq.shift.start_date"/>:</td>
          <td width="160" align="left" nowrap class="tdFontcolor"> 
          
           <html:text name="arrayGroupForm" property='start_date' styleId='start_date' size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);"  styleClass="TEXT4"/>
            &nbsp; </td>
        </tr>
        <tr> 
		  <td align="right" nowrap class="tdFontcolor">&nbsp;&nbsp;&nbsp;&nbsp;</td>
          <td  align="left" nowrap class="tdFontcolor" > <bean:message key="kq.shift.end_date"/>: </td>
          <td align="left" class="tdFontcolor" nowrap>
           <html:text name="arrayGroupForm" property='end_date' styleId='end_date' size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'  onchange="rep_dateValue(this);"  styleClass="TEXT4"/>
           </td>
          <!--数据值-->
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
          
  </table>
  </div>
</html:form>
<div id='wait' style='position:absolute;top:100;left:60;display:none;'>
  <table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>

             <td class="td_style common_background_color" height=24><bean:message key="classdata.isnow.wiat"/></td>

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