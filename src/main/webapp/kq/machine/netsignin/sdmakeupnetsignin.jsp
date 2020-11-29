<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<style type="text/css">
.RecordRowC {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;
}
</style>
<style type="text/css">
body {
	background-color: transparent;
	margin:0px;
}
.m_frameborder {
	border-left: 1px inset #D4D0C8;
	border-top: 1px inset #D4D0C8;
	border-right: 1px inset #D4D0C8;
	border-bottom: 1px inset #D4D0C8;
	width: 40px;
	height: 20px;
	background-color: transparent;
	overflow: hidden;
	text-align: right;	
	font-size: 6px;
}
.m_arrow {
	width: 16px;
	height: 8px;
	font-family: "Webdings";
	font-size: 7px;
	line-height: 2px;
	padding-left: 2px;
	cursor: default;
}
.m_input {
	width: 18px;
	height: 14px;
	border: 0px solid black;
	font-family: "Tahoma";
	font-size: 9px;
	text-align: right;
}

input_text {
	border: inset 1px #000000;
	BORDER-BOTTOM: #FFFFFF 0pt dotted; 
	BORDER-LEFT: #FFFFFF 0pt dotted; 
	BORDER-RIGHT: #FFFFFF 0pt dotted; 
	BORDER-TOP: #FFFFFF 0pt dotted;	
}
.unnamed2 {
	border: 1px solid #666666;
	background-color: #FFFFFF;
}
</style>
<script language="javascript">
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
   var flag_biaozhi = 0;
function save_makeup()
{
	var hashvo=new ParameterSet();
	var sdjudge="";
	//for(var i=0;i<document.netSigninForm.sdjudge.length;i++)
	//{
	//	if(document.netSigninForm.sdjudge[i].checked)
	//	{
	//		sdjudge=document.netSigninForm.sdjudge[i].value;
	//	}
	//}
	sdjudge = document.getElementById("sdjudge").value;
	if(sdjudge=="")
	{
		alert("请选择补签类型！");
		return false;
	}
	var makeup_date_o=document.getElementById("sdmakeup_date"); 
	if(makeup_date_o.value=="")
	 {
	   alert("补签日期不能为空");
	   makeup_date_o.focus();
	   return false;
	 }
	 var sdmakeup_date=makeup_date_o.value;
	 sdmakeup_date=sdmakeup_date.replace(".","-");
	 sdmakeup_date=sdmakeup_date.replace(".","-");	
	 if(!checkDateTime(sdmakeup_date))
	 {
	    alert("补签日期格式不正确!");
	    return false;
	 }
	 if(!isDate(sdmakeup_date,"yyyy-MM-dd"))
       {
                alert("补签日期格式不正确,请输入正确的日期格式！\nyyyy-MM-dd");
                return false;
       }
    hashvo.setValue("a0100","${netSigninForm.a0100sign}");
	hashvo.setValue("nbase","${netSigninForm.dbsign}");
	hashvo.setValue("sdmakeup_date",sdmakeup_date);
	hashvo.setValue("sdjudge",sdjudge);  //1=签到 0 补签
	hashvo.setValue("z1",sdmakeup_date);
	 hashvo.setValue("z1str","补签日期");
	 var request=new Request({asynchronous:false,onSuccess:returnResult,functionId:'1510010055'},hashvo);
	 
	 if (flag_biaozhi == 1) {
	var request=new Request({method:'post',asynchronous:false,onSuccess:showReturn,functionId:'15221400013'},hashvo);
	}
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
  function showReturn(outparamters)
   {
      var mess=outparamters.getValue("mess");
      alert(mess);
      netSigninForm.action="/kq/machine/netsignin/sdsigninlist.do?b_sdlist=link";
      netSigninForm.submit();
   } 
</script>
<script language="javascript">

function gback()
{
	netSigninForm.action="/kq/machine/netsignin/sdsigninlist.do?b_sdlist=link";
    netSigninForm.submit();
}	 
</script>
<html:form action="/kq/machine/netsignin/sdsigninlist">
	 <br>
  <br>
  <html:hidden styleId="dbsign" name="netSigninForm" property="dbsign"/>
  <html:hidden styleId="a0100sign" name="netSigninForm" property="a0100sign"/>
  <table width="60%" border="0" cellspacing="1" align="center" cellpadding="1" class="ListTable">
  		<thead>
			<tr>
				<td align="left" class="TableRow" colspan="2" nowrap>
					&nbsp;&nbsp;&nbsp;上岛补签申请&nbsp;
				</td>
			</tr>
		</thead>
		<tr>
			 <td width="20%" height="30" align="center" class="RecordRowC" nowrap >
		        申请日期
		      </td>
		      <td  class="RecordRowC" nowrap >		        
		          <html:text name="netSigninForm" property='sdmakeup_date' styleId="sdmakeup_date" size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' styleClass="TEXT4"/>
		      </td>  
		</tr>
		<tr>
		      <td  height="30" class="RecordRowC" nowrap >
		        
		      </td>
		      <td  class="RecordRowC" nowrap >
		      		<html:radio name="netSigninForm" property="sdjudge"  value="1"/>上岛补签到
		      </td> 
		</tr>
		<tr>
			<td class="RecordRow" nowrap colspan="2">
		     <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' class="mybutton" onclick="save_makeup()">&nbsp;
		     <input type="button" name="btnreturn" value='<bean:message key="button.return"/>' class="mybutton" onclick="gback();">&nbsp;
		   </td>
		</tr>
  </table>
</html:form>