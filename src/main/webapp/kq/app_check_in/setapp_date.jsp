<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/gz/salary.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script type="text/javascript">

function getdatevalue()
{
  var value = document.getElementById('time').value;
 var thevo=new Object(); 
  thevo.text=value;
  thevo.value=value;
  window.returnValue=thevo;
  window.close();  
}

var outObject;
var weeks="";
var feasts ="";
var turn_dates="";
var week_dates="";
var easy_app_start_date="";
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
       easy_app_start_date=outparamters.getValue("easy_app_start_date");  
}
</script>
<body style="width:100%">
<html:form action="/kq/app_check_in/all_app_data"> 
	 <table align="center">
    		<tr >
		  	  <td>
				<input type="hidden" value="${appRegisterForm.easy_app_start_date}" onchange='getdatevalue();' id="time"/>
			  </td>
	    	</tr>
    	 </table>
</html:form>
</body>
<script type="text/javascript">
	//createCalendarPanel($('datepnl'));
	//var d= new Date(); 
	//setCalendarDate(d);

	var tNow=document.getElementById('time');
	getKqCalendarVar();
	popUpCalendar(tNow,tNow,weeks,feasts,turn_dates,week_dates,false,false);
</script>
  