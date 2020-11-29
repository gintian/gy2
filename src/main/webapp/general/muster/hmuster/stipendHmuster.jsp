<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="com.hjsj.hrms.actionform.general.muster.hmuster.StipendHmusterForm" %>
<%
	String isMobile = request.getParameter("isMobile");
	StipendHmusterForm stipendHmusterForm = (StipendHmusterForm)session.getAttribute("stipendHmusterForm");
	String dbpre = PubFunc.encrypt(stipendHmusterForm.getDbpre());
	String a0100 = PubFunc.encrypt(stipendHmusterForm.getA0100());
 %>
<hrms:themes></hrms:themes>
<link rel="stylesheet" href="/css/css1_brokenline.css" type="text/css">
<script language="JavaScript" src="/js/validateDate.js"></script>
<%if("1".equals(isMobile)){%>
<script type="text/javascript" src="/phone-app/jquery/jquery-3.5.1.min.js"></script>
<script type="text/javascript" src="/phone-app/jquery/rpc_command.js"></script>
<%} %>
<html>

<style>
.mybuttons{
	border:1px solid #c5c5c5 ;
	padding:2px 4px 2px 4px ;
	background-color:#f9f9f9 ;
	font:12px/16px 微软雅黑, 宋体, tahoma, arial, verdana, sans-serif;
	cursor:pointer
}
body {  
	/*background-color:#E1F1FB;*/
	background-color:#FFFFFF;
	font-size: 12px;
	margin:4 0 0 4;
}
</style>
<script language='javascript'>

function displayDiv()
{
	var obj=eval("d"+document.stipendHmusterForm.operate.value);
	var obj1=eval("d1");
	var obj2=eval("d2");
	var obj3=eval("d3");
	var obj4=eval("d4");
	if(obj1)
		obj1.style.display="none";
	if(obj2)
		obj2.style.display="none";
	if(obj3)
		obj3.style.display="none";
	if(obj4)
		obj4.style.display="none";
		
	if(obj)
		obj.style.display="block";
	if(document.stipendHmusterForm.operate.value=='2'||document.stipendHmusterForm.operate.value=='3')
		obj1.style.display="block";
}

function sub(){
   var groupCount="0";
    var obj = document.getElementById("groupCount");
    if(obj)
    {
        if(obj.checked)
            groupCount="1";
    }
	var opt=document.stipendHmusterForm.operate.value;
	if(opt==4)
	{
		if(document.stipendHmusterForm.startDate.value.length>0&&!validate(document.stipendHmusterForm.startDate))
		{
			document.stipendHmusterForm.startDate.value="";
			document.stipendHmusterForm.startDate.focus();
			return;
		}
		if(document.stipendHmusterForm.endDate.value.length>0&&!validate(document.stipendHmusterForm.endDate))
		{
			document.stipendHmusterForm.endDate.value="";
			document.stipendHmusterForm.endDate.focus();
			return;
		}
	}
	document.stipendHmusterForm.action="/general/muster/hmuster/executeStipendHmuster.do?b_query=link&musterFlag=${stipendHmusterForm.musterFlag}&a0100=${stipendHmusterForm.a0100}&musterID=${stipendHmusterForm.musterID}&dbpre=${stipendHmusterForm.dbpre}";
	document.stipendHmusterForm.submit();

}


function showMuster(musterID)
{
	//点击按钮后，按钮禁用防止多次点击报错  wangb 32299 20171027
	var musterBtn = document.getElementById('musterBtn');
	musterBtn.setAttribute('disabled','disabled');
	document.stipendHmusterForm.action="/general/muster/hmuster/executeStipendHmuster.do?b_query=link&isInit=init&musterFlag=${stipendHmusterForm.musterFlag}&a0100=${stipendHmusterForm.a0100}&musterID="+musterID+"&dbpre=${stipendHmusterForm.dbpre}";
	document.stipendHmusterForm.submit();
}


function showExcel(outparamters)
{
	<%if(!"1".equals(isMobile)){ %>
	var outName=outparamters.getValue("outName");
	window.open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName,"xls");
	<%}else{%>
	var map=JSON.parse(outparamters);
		if(map.succeed){
			var outName=map.outName;
			window.location.target="_blank";
			window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
		}	
	<%}%>
}

function excecutePDF()
{
	<%if("1".equals(isMobile)){ %>
	var map = new HashMap();
	map.put("modelFlag","stipend");
	map.put("tabID","${stipendHmusterForm.musterID}");
	map.put("dbpre","<%=dbpre%>");
	map.put("a0100","<%=a0100%>");
    map.put("groupCount","${stipendHmusterForm.groupCount}");
	map.put("exce","PDF"); 
	map.put("infor_Flag","stipend");
	var platform=navigator.platform;
	map.put("platform",platform);  
	Rpc({functionId:'0550000005',success:showFieldList},map);
	<%}else{%>
	var hashvo=new ParameterSet();
	hashvo.setValue("modelFlag","stipend");
	hashvo.setValue("infor_Flag","stipend");
	hashvo.setValue("tabID","${stipendHmusterForm.musterID}");
	hashvo.setValue("dbpre","<%=dbpre%>");
	hashvo.setValue("a0100","<%=a0100%>");
    hashvo.setValue("groupCount","${stipendHmusterForm.groupCount}");
	var In_paramters="exce=PDF";   
   	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showFieldList,functionId:'0550000005'},hashvo);
	<%}%>
}



function excecuteExcel()
{
	<%if("1".equals(isMobile)){ %>
	var map = new HashMap();
	map.put("modelFlag","stipend");
	map.put("tabID","${stipendHmusterForm.musterID}");
	map.put("dbpre","<%=dbpre%>");
	map.put("a0100","<%=a0100%>");
	map.put("infor_Flag","stipend");
	map.put("paperRows","${stipendHmusterForm.paperRows}");
	map.put("groupCount","${stipendHmusterForm.groupCount}");
	map.put("exce","excel");  
	Rpc({functionId:'0550000009',success:showExcel},map);
	<%}else{%>
	var hashvo=new ParameterSet();
	hashvo.setValue("modelFlag","stipend");
	hashvo.setValue("tabID","${stipendHmusterForm.musterID}");
	hashvo.setValue("dbpre","<%=dbpre%>");
	hashvo.setValue("infor_Flag","stipend");
	hashvo.setValue("a0100","<%=a0100%>");
	hashvo.setValue("paperRows","${stipendHmusterForm.paperRows}");
	hashvo.setValue("groupCount","${stipendHmusterForm.groupCount}");
	 var In_paramters="exce=excel";  
	 var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showExcel,functionId:'0550000009'},hashvo);
	<%}%>
}

function showFieldList(outparamters)
{
	<%if(!"1".equals(isMobile)){ %>
	var url=outparamters.getValue("url");
	var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+url,"pdf");
	<%}else{%>
	var map=JSON.parse(outparamters);
		if(map.succeed){
			var url=map.url;
			window.location.target="_blank";
			window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+url;
		}	
	<%}%>
	
}
function queryData(obj)
{
    var groupCount="0";
    if(obj.checked)
        groupCount="1";
    document.stipendHmusterForm.action="/general/muster/hmuster/executeStipendHmuster.do?b_query=link&groupCount="+groupCount+"&a0100=${stipendHmusterForm.a0100}&musterID=${stipendHmusterForm.musterID}&musterFlag=${stipendHmusterForm.musterFlag}&dbpre=${stipendHmusterForm.dbpre}";
	document.stipendHmusterForm.submit();
}
function returnback(){
	document.stipendHmusterForm.action="/phone-app/app/emolument.do?b_init=link&flag=infoself";
    document.stipendHmusterForm.submit();
}
</script>
<body>
<html:form action="/general/muster/hmuster/executeStipendHmuster" >
<input type="hidden" name="isMobile" value="<%=isMobile %>" />
<input type="hidden" id="musterFlag" value="{stipendHmusterForm.musterFlag}" />
<table> <tr>
<logic:equal name="stipendHmusterForm" property="isTimeIdentifine" value="1">
<td nowrap="nowrap"><bean:message key="kq.formula.fashion"/></td>
<td nowrap="nowrap">
<html:select name="stipendHmusterForm" property="operate" size="1" onchange='displayDiv()' >
                              <html:optionsCollection property="operateList" value="dataValue" label="dataName"/>
    </html:select>&nbsp; &nbsp; &nbsp; 
		</td><td nowrap="nowrap">
	<div id='d1' style='display:none; vertical-align: middle;'>
		<html:select name="stipendHmusterForm" property="year" size="1">
	         <html:optionsCollection property="yearList" value="dataValue" label="dataName"/>
	    </html:select> <bean:message key="kq.wizard.year"/>
	</div>
	</td><td nowrap="nowrap">
	<div id='d2' style='display:none; vertical-align: middle;'>
		
	    <html:select name="stipendHmusterForm" property="month" size="1">
	        <html:optionsCollection property="monthList" value="dataValue" label="dataName"/>
	    </html:select> <bean:message key="kq.wizard.month"/>
	</div>
	</td><td nowrap="nowrap">
	<div id='d3' style='display:none; vertical-align: middle;'>		
	    <html:select name="stipendHmusterForm" property="quarter" size="1">
	        <html:optionsCollection property="quarterList" value="dataValue" label="dataName"/>
	    </html:select> <bean:message key="kq.wizard.quarter"/>
	</div>
	</td><td nowrap="nowrap">
	<div id='d4' style='display:none; vertical-align: middle;'>
		<input  type="text" name="startDate" extra="editor"  id="editor4"  
								dropDown="dropDownDate"  value="${stipendHmusterForm.startDate}">
		<bean:message key="kq.init.tand"/>
		<input  type="text" name="endDate" extra="editor"  id="editor4"  
								dropDown="dropDownDate"    value="${stipendHmusterForm.endDate}">
	
	</div>
	</td>
	<td nowrap="nowrap" align="right">
    <hrms:priv func_id="0102010201,0302010201"> 
	&nbsp;&nbsp;<html:checkbox property="groupCount" name="stipendHmusterForm" value="1" onclick="queryData(this);">分组合计</html:checkbox>
    </hrms:priv>	
     <input type='button' value="&nbsp;<bean:message key='infor.menu.query'/>&nbsp;" class="mybuttons" onclick='sub()' />
	</td>
</logic:equal>

<td style="margin-left: -20px">
<logic:equal name="stipendHmusterForm" property="flag" value="infoself">
<hrms:priv func_id="01020101">  <input type='button' value="<bean:message key='button.createpdf'/>" class="mybuttons" onclick='excecutePDF()' /></hrms:priv>
<hrms:priv func_id="01020104">  <input type='button' value="<bean:message key='button.createescel'/>" class="mybuttons" onclick='excecuteExcel()' /></hrms:priv>
</logic:equal>
<logic:notEqual name="stipendHmusterForm" property="flag" value="infoself">
<hrms:priv func_id="03020101">  <input type='button' value="<bean:message key='button.createpdf'/>" class="mybuttons" onclick='excecutePDF()' /></hrms:priv>
<hrms:priv func_id="03020104">  <input type='button' value="<bean:message key='button.createescel'/>" class="mybuttons" onclick='excecuteExcel()' /></hrms:priv>
</logic:notEqual>
<%if("1".equals(isMobile)){ %>
<input type="button" class="mybuttons" value="&nbsp;返回&nbsp;" onclick="javascript:returnback();" />
<%} %>
</td>
</tr> 
</table>
<logic:notEqual name="stipendHmusterForm" property="musterFlag" value="infoself">
<logic:notEqual name="stipendHmusterForm" property="musterFlag" value="statCount">
<logic:iterate  id="element"    name="stipendHmusterForm"  property="hmusterList" indexId="index"> 
 <!-- 添加id属性 获取button元素  wangb 32288 20171027  -->
 <button id="musterBtn" class="mybuttons" onclick="showMuster('<bean:write  name="element" property="dataValue"/>')"><bean:write  name="element" property="dataName"/></button>
</logic:iterate>
</logic:notEqual>
</logic:notEqual>
${stipendHmusterForm.html}

</html:form>

<script language='javascript'>

<logic:equal name="stipendHmusterForm" property="isTimeIdentifine" value="1">
	var obj0=eval("d${stipendHmusterForm.operate}");
	obj0.style.display="block";
	<logic:equal name="stipendHmusterForm" property="operate" value="2">
		var aobj=eval("d1");
		aobj.style.display="block";
	</logic:equal>
	<logic:equal name="stipendHmusterForm" property="operate" value="3">
		var aobj=eval("d1");
		aobj.style.display="block";
	</logic:equal>
</logic:equal>

</script>

</body>
</html>