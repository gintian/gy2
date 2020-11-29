
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
String ver = (String)request.getParameter("ver");
ver=ver!=null&&ver.length()>0?ver:"";
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
//判断当前用户是否自助用户（4）还是业务用户（0）
int status=0;
status = userView.getStatus();
String bosflag="";
if(userView != null)
{
	bosflag=userView.getBosflag();
}
	String returnvalue = request.getParameter("returnvalue")==null?"":request.getParameter("returnvalue");
%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<br>
<br>
<div id=warnResultShow>
<table id='warnInfoTable' width="50%"height="0" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
<script type="text/javascript" language="javascript">
var marqueeContent=new Array();
var marqueeInterval=new Array();

function initMarquee(outparamters){
	var tempArray=outparamters.getValue("userViewResult");
	if(typeof(tempArray)=="undefined"||tempArray.length<1){
		Element.hide('warnResultShow');
		return;
	}
	document.write('<thead>');
	document.write('<tr>');
	document.write('	<td align="center" class="TableRow" nowrap>预警信息(共'+tempArray.length+'项)</td>');
	document.write('</tr>');
	document.write('</thead>');
	var lines=tempArray.length;
	for(var i=0;i<lines;i++) 
	{
		if(tempArray[i].dataValue.indexOf("@")!=-1)
			marqueeContent[i]='<font siz=1><a href="/performance/warnPlan/noScorePersonList.do?b_query=link&dbpre=&dbPre=&plan_id='+tempArray[i].dataValue.substring(tempArray[i].dataValue.indexOf("@")+1,tempArray[i].dataValue.length)+'">'+(i+1)+'、'+tempArray[i].dataName+'</a></font>';
		else if(tempArray[i].dataValue.indexOf("$")!=-1)
			marqueeContent[i]='<font siz=1><a href="/performance/warnPlan/noAppCardPersonList.do?b_query=link&dbpre=&&dbPre=&plan_id='+tempArray[i].dataValue.substring(tempArray[i].dataValue.indexOf("$")+1,tempArray[i].dataValue.length)+'">'+(i+1)+'、'+tempArray[i].dataName+'</a></font>';
		else
			marqueeContent[i]='<font siz=1><a href="/system/warn/result_manager.do?b_query=link&dbpre=&dbPre=&returnvalue=list&warn_wid='+tempArray[i].dataValue+'">'+(i+1)+'、'+tempArray[i].dataName+'</a></font>';
		document.write('<tr class="'+(i%2==0?'trDeep':'trShallow')+'"><td class="RecordRow">'+marqueeContent[i]+'</td></tr>');
	}
}
function initScan() {
	var tatolPars="isRole=true";
	var request=new Request({method:'post',asynchronous:false,parameters:tatolPars,onSuccess:initMarquee,functionId:'1010020307'});
}
</script>
<script type="text/javascript" language="javascript">
	initScan();
</script>
</table>
</div>
<table width="50%" align="center">
	<tr>
		<td align="center">
		<%
		if(returnvalue.equals("dxt")){
		%>
		<input type="submit"  value="返回" class="mybutton" onclick="history.back();">
		<%}else{ %>
		 <%if(ver!=null&&ver.equals("5")){ %>
			 <%if("hcm".equals(bosflag)){ %>
			    <input type="submit"  value="返回" class="mybutton" onclick="window.location.replace('/templates/index/hcm_portal.do?b_query=link');"> 
			 <%}else { %>
			 	<input type="submit"  value="返回" class="mybutton" onclick="window.location.replace('/templates/index/portal.do?b_query=link');"> 
			 <%} %>
		 <%}else if(ver!=null&&ver.equals("bi")){ %>
		    <input type="submit"  value="返回" class="mybutton" onclick="window.location.replace('/templates/index/bi_portal.do?b_query=link');"> 
		
		 <%}else{ %>		 
         	<input type="submit"  value="返回" class="mybutton" onclick="history.back();"> 
         <%} }%>	
        </td>
	</tr>
</table>

