<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.performance.workdiary.WorkdiaryForm,
com.hrms.struts.constant.WebConstant,
com.hrms.struts.valueobject.UserView,
				 com.hjsj.hrms.utils.PubFunc"%>
<%		
	WorkdiaryForm wf = (WorkdiaryForm)session.getAttribute("workdiaryForm");
	String a0 = PubFunc.encryption("0");
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
	
%>

<DIV id="overDiv" style="POSITION: absolute; Z-INDEX: 1;background-color:#FFFFCC;overflow:visible;background-image:../images/mainbg.jpg"></DIV><!--LiWeichao 去掉  class="RecordRow" -->
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="workdiary.js"></script>
<script language="JavaScript" src="/js/wz_tooltip.js"></script>
<style type="text/css"> 
<!--
.RecordTop1 {
	background-position : center left;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;
	BORDER-BOTTOM: #B9D2F5 1pt solid; 
	BORDER-LEFT: #B9D2F5 1pt solid; 
	BORDER-RIGHT: #B9D2F5 0pt solid; 
	BORDER-TOP: #B9D2F5 1pt solid;
	font-size: 12px;
	width:13.5%;
}
.RecordTop2 {
	background-position : center left;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;
	BORDER-BOTTOM: #B9D2F5 1pt solid; 
	BORDER-LEFT: #B9D2F5 1pt solid; 
	BORDER-RIGHT: #B9D2F5 1pt solid; 
	BORDER-TOP: #B9D2F5 1pt solid;
	font-size: 12px;
	width:13.5%;
}
.RecordRow1 {
	BORDER-BOTTOM: #B9D2F5 1pt solid; 
	BORDER-LEFT: #B9D2F5 1pt solid; 
	BORDER-RIGHT: #B9D2F5 0pt solid; 
	BORDER-TOP: #B9D2F5 0pt solid;
	padding:0px  !important;
	font-size: 12px;
	width:13.5%;
	height:50px;
}
.RecordRowLast {
	BORDER-BOTTOM: #B9D2F5 1pt solid; 
	BORDER-LEFT: #B9D2F5 1pt solid; 
	BORDER-RIGHT: #B9D2F5 1pt solid; 
	BORDER-TOP: #B9D2F5 0pt solid;
	font-size: 12px;
	width:13.5%;
	height:50px;
}
.RecordRow2 {
	BORDER-BOTTOM: #B9D2F5 1pt solid; 
	BORDER-LEFT: #B9D2F5 1pt solid; 
	BORDER-RIGHT: #B9D2F5 1pt solid;
	BORDER-TOP: #B9D2F5 0pt solid;
	font-size: 12px;
	width:13.5%;
	height:50px;
}
.RecordRow3 {
	border: inset 1px #B9D2F5;
	BORDER-BOTTOM: #B9D2F5 1pt solid; 
	BORDER-LEFT: #B9D2F5 1pt solid; 
	BORDER-RIGHT: #B9D2F5 1pt solid; 
	BORDER-TOP: #B9D2F5 1pt solid;
	font-size: 12px;
	height:50;
	width:13.5%;
}
-->
</style>

<script type="text/javascript" language="javascript">

<!--
function addiary(){
	    workdiaryForm.action="/performance/workdiary/myworkdiaryshow.do?b_add=link&query=add&state=<%=a0 %>";
    	workdiaryForm.submit();
}
function updatediary(){
	    workdiaryForm.action="/performance/workdiary/myworkdiaryshow.do?b_add=link&query=update&state=<%=a0 %>";
    	workdiaryForm.submit();
}

function serch(){
	workdiaryForm.action="/performance/workdiary/myworkdiaryshow.do?b_search=link&search=ok&timefield=1";
    workdiaryForm.submit();
}
function searchday(){
	var currentDate   =   new   Date() ;
	var y=currentDate.getYear();
	var m=currentDate.getMonth()+1;
	if(m<=9){
		m="0"+m;
	}
	var d=currentDate.getDate();
	if(d<=9){
		d="0"+d;
	}
	var startime=y+"-"+m+"-"+d;
	workdiaryForm.ymd.value="2";
	workdiaryForm.action="/performance/workdiary/myworkdiaryshow.do?b_query=link&search=ok&currday=1&startime="+startime;
    workdiaryForm.submit();
}
function searchweek(){
	var currentDate   =   new   Date() ;
	var y=currentDate.getYear();
	var m=currentDate.getMonth()+1;
	workdiaryForm.ymd.value="1";
	workdiaryForm.action="/performance/workdiary/myworkdiaryshow.do?b_query=link&search=ok&currweek=1";
    workdiaryForm.submit();
}
function searchmonth(){
	var currentDate   =   new   Date() ;
	var y=currentDate.getYear();
	var m=currentDate.getMonth()+1;
	if(m<=9){
		m="0"+m;
	}
	var startime=y+"-"+m+"-01";
	workdiaryForm.ymd.value="0";
	workdiaryForm.action="/performance/workdiary/myworkdiaryshow.do?b_query=link&search=ok&currmonth=1&startime="+startime;
    workdiaryForm.submit();
}
function showsjfw(){
	var sjfw=$('sjfw');
	workdiaryForm.ymd.value="3";
	sjfw.style.display="block";
}
function outContent(p0100,pid){
	var hashvo=new ParameterSet();
	hashvo.setValue("p0100",p0100);	
	hashvo.setValue("pid",pid);
   	var request=new Request({method:'post',asynchronous:true,onSuccess:viewContent,functionId:'9020010011'},hashvo);
}
function viewContent(outparamters){
	var content=outparamters.getValue("content");
	Tip(getDecodeStr(content),STICKY,true);
	return true;
}
function returnblack(){
	workdiaryForm.action="/performance/workdiary/index.jsp";
	workdiaryForm.submit();
}
function lieTable(){
	workdiaryForm.action="/performance/workdiary/myworkdiaryshow.do?b_query=link&state=0";
	workdiaryForm.submit();
}
	-->
</script>
<hrms:themes />
<html:form action="/performance/workdiary/myworkdiaryshow">
<center>
<%if("hl".equals(hcmflag)){ %>
<table width="98%" border="0" cellspacing="0"  cellpadding="0" align="center" style="margin-top:5px;">
<%}else{ %>
<table width="98%" border="0" cellspacing="0"  cellpadding="0" align="center" >
<%} %>

<tr>
	<td width="90" nowrap height="20" style="padding-bottom: 5px;">
		&nbsp;<html:select name="workdiaryForm" property="yearnum" style="width:60" onchange="serch();">
			<html:optionsCollection property="yearlist" value="dataValue" label="dataName" />
		</html:select>
		<bean:message key="datestyle.year"/> 
		
	</td>
	<td style="padding-bottom: 5px;">
		<html:select name="workdiaryForm" property="monthnum" style="width:60"  onchange="serch();">
			 <html:optionsCollection property="monthlist" value="dataValue" label="dataName" />
		</html:select><bean:message key="datestyle.month"/> 
	</td>
</tr>
</table>
${workdiaryForm.tablestr}
<table width="98%" align="center">
<tr>
	<td align="center" style="height:20px;">
		<BUTTON name="return" class="mybutton"  onclick="lieTable();" ><bean:message key="workdiary.info.details"/></BUTTON>&nbsp;
		<%if(wf.getZxgflag()!=null&&wf.getZxgflag().equals("1")){ %>
			<BUTTON name="return" class="mybutton"  onclick="window.close()" >关闭</BUTTON>&nbsp;
		<%}else { %>
			<BUTTON name="return" class="mybutton"  onclick="returnblack();" ><bean:message key="button.return"/></BUTTON>&nbsp;
		<%} %>
		
	</td>
</tr>
</table>
</center>
</html:form>