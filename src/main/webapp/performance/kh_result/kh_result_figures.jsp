<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.performance.kh_result.KhResultForm"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%  KhResultForm khResultForm=(KhResultForm)session.getAttribute("khResultForm");
    String chart_type=khResultForm.getChart_type();
    UserView userview=(UserView)session.getAttribute(WebConstant.userView);
    int width=700;
    if(userview.getVersion()>=50)
      width=-1;
%>
<html>
<head>
<title></title>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<script type="text/javascript">
<!--
function changeBgColor(id)
{
   var e = document.getElementById(id);
   e.style.backgroundColor="#98C2E8";
  
}
function goBackBgColor(id)
{
   var e = document.getElementById(id);
   e.style.backgroundColor="white";
}
function hiddenElement()
	{
   		 setTimeout("closeMenu()",500);	
	}
	function closeMenu()
	{
	   var obj=document.getElementById('menu_');
	   obj.style.display="none";
	}
function showMenu()
	{
		 var obj=document.getElementById('menu_');
		 obj.style.display="block";
		 obj.style.position="absolute";
		 obj.style.left=event.clientX;	
		 obj.style.top=event.clientY;
		 document.getElementById("menu_").focus();
		 
	}
function subtype(type)
{
   khResultForm.action="/performance/kh_result/kh_result_figures.do?b_init=init&opt=2&drawtype="+type;
   khResultForm.submit();
}
function set()
{
	var type = "0";
	var obj = document.getElementsByName("drawtype");
	for(var i=0;i<obj.length;i++)
	{
  		if(obj[i].checked)
  		{
     		type = obj[i].value;
  		}
	}
	var objmenu = document.getElementById('menu_');
	objmenu.style.display = "none";
	var title = ""
	var str = "";
	var w_l = "";
	if(document.khResultForm.chartParameters.value.length>0)
	{	
		var temps=document.khResultForm.chartParameters.value.split("`");
		title=temps[0];
		str=document.khResultForm.chartParameters.value.substring(title.length+1);
			
		if(temps.length>=10&&temps[9].length>0)
		{
			var wl=temps[9].split(",");
			w_l=wl[0]+","+wl[1];
		}
	}
	window.drawtype = type;
	var chart = jfreechartSet2(title,"${khResultForm.scoreGradeStr}",str,0,w_l);
	if(/msie/i.test(navigator.userAgent)){
		set_ok(chart);
	}
//	var chart = jfreechartSet("${khResultForm.title}","${khResultForm.scoreGradeStr}","${khResultForm.titleAlign}",1);
}
function set_ok(chart){
	if(chart!=null&&chart!='undefined')
	{
		khResultForm.action="/performance/kh_result/kh_result_figures.do?b_init=init&opt=2&drawtype="+drawtype+"&chartParameters="+$URL.encode(chart);
        khResultForm.submit();
	}else
		return;		
}
	function alertOption(type)
	{
	   var drawElement=document.getElementsByName("drawtype");
	   var drawtype="0";
	   for(var i=0;i<drawElement.length;i++)
	   {
	      if(drawElement[i].checked)
	      {
	         drawtype=drawElement[i].value;
	      }
	   }
       if(type=='1')
       {
           var isd=document.getElementById("isd");//"${khResultForm.isShow3D}";
           if(isd.value=="1")
           {
               document.getElementById("isd").value="0";
           }
           else
           {
               document.getElementById("isd").value="1";
           }
       }
       else if(type=='2')
       {
           var isd=document.getElementById("iss");//"${khResultForm.isShowScore}";
           if(isd.value=="1")
               document.getElementById("iss").value="0";
           else
               document.getElementById("iss").value="1";
       }
       else if(type=='3')
       {
          document.getElementById("ct").value="4";
       }
       else if(type=='4')
       {
          document.getElementById("ct").value="29";
       }
       else if(type=='5')
       {
          document.getElementById("ct").value="41";
       }
	   khResultForm.action="/performance/kh_result/kh_result_figures.do?b_init=init&opt=2&drawtype="+drawtype;
       khResultForm.submit();
	}
	function changeDepartment()
	{
		khResultForm.action="/performance/kh_result/kh_result_figures.do?b_init=init&opt=2&graphType=3";
        khResultForm.submit();
	}
//-->
</script>
 <hrms:themes />
</head>
<body style="margin: 0px" oncontextmenu='showMenu();return false;'>
<html:form action="/performance/kh_result/kh_result_figures">
<table align="center" border="0" width="100%" cellpmoding="0" cellspacing="0" cellpadding="0" style="margin-top: 3px">
<tr>
<td>
<logic:equal  name="khResultForm" property="graphType"  value="1" >
    <html:radio name="khResultForm" property="drawtype" value="0" onclick="subtype('0');">按分值</html:radio>
    <html:radio name="khResultForm" property="drawtype" value="1"  onclick="subtype('1');">按得分率</html:radio>
		   
	<logic:equal  name="khResultForm" property="drawtype"  value="0" >(单位:分值)</logic:equal>
	<logic:equal  name="khResultForm" property="drawtype"  value="1">(单位:百分比)</logic:equal>
	&nbsp;&nbsp;
</logic:equal>
	<%--去掉3D立体图  --%>
	<%--<input type='checkbox' value="1" onclick="alertOption('1');" id="3d" name='3D'<logic:equal name="khResultForm" property="isShow3D" value="1"> checked </logic:equal>/><a href="javascript:alertOption('1')"><bean:message key="lable.performance.3DChart"/></a>--%>
    &nbsp;&nbsp;
    <%if(userview.getVersion()<50){%>
    <input type='checkbox' value="1" onclick="alertOption('2');" id="score" name="Score" <logic:equal name="khResultForm" property="isShowScore" value="1"> checked </logic:equal>/><a href="javascript:alertOption('2')"><bean:message key="lable.performance.showScore"/></a>
    &nbsp;&nbsp;
    <%} %>
    <a href="javascript:alertOption('3')"><bean:message key="lable.performance.graph"/></a>
	&nbsp;&nbsp;
	<a href="javascript:alertOption('4')"><bean:message key="lable.performance.histogram"/></a>
	&nbsp;&nbsp;
	<a href="javascript:alertOption('5')"><bean:message key="lable.performance.radar"/></a>
	
	<logic:notEqual  name="khResultForm" property="graphType"  value="3" >
		&nbsp;&nbsp;
		<input type="button" value="选项..." onclick="set()" class="mybutton">
	</logic:notEqual>
	
<logic:equal  name="khResultForm" property="graphType"  value="3" >
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<hrms:optioncollection name="khResultForm" property="departmentList" collection="list" />
		<html:select name="khResultForm" property="department" size="1" onchange="changeDepartment();">
		<html:options collection="list" property="dataValue" labelProperty="dataName"/>
		</html:select>
</logic:equal>
</td>
</tr>
<tr>
<td align="center" nowrap>
<table align="center" border="0" width="100%" cellpmoding="0" cellspacing="0" cellpadding="0">
<tr>
<td align="center" width="100%" nowrap>
<% if(chart_type.equals("4")||chart_type.equals("30") || chart_type.equals("41")){ %>
<hrms:chart yAxisAuto="true" name="khResultForm" title="" scope="session" legends="figuresmap" data="" width="<%=width%>" height="500" chart_type="${khResultForm.chart_type}"  numDecimals="2" label_enabled="${khResultForm.label_enabled}" labelIsPercent="0" isneedsum="false" xangle="45" chartParameter="chartParameter"  >
   		</hrms:chart>
   		<%} 
   		if(chart_type.equals("29")||chart_type.equals("31")){ %>
   		<hrms:chart yAxisAuto="true" name="khResultForm" title="" scope="session" legends="dataList" data="" width="<%=width%>" height="500" chart_type="${khResultForm.chart_type}" numDecimals="2" label_enabled="${khResultForm.label_enabled}" labelIsPercent="0" isneedsum="false" xangle="45" chartParameter="chartParameter"  >
   		</hrms:chart>
   		<%} %>
</td>
</tr>
</table>
</td>
</tr>
<tr>
<td align="center" nowrap>
<html:hidden name="khResultForm" property="planid"/>
<html:hidden name="khResultForm" property="object_id"/>
<html:hidden name="khResultForm" property="distinctionFlag"/>
<input type="hidden" value="${khResultForm.isShow3D}" id="isd" name="isShow3D"/>
<html:hidden name="khResultForm" styleId="iss" property="isShowScore"/>
<html:hidden name="khResultForm" styleId="ct" property="chart_type"/>
 <table align="center" border="0" width="100%" cellpmoding="0" cellspacing="0" cellpadding="0">
 <% int k=0; %>
 <logic:iterate id="element" name="khResultForm" property="pointList">
<%
		k++;
		%>
		
		<td valign="top">&nbsp;<bean:write name="element" property="point_id" filter="true"/></td>
		<td width="300" valign="top"><bean:write name="element" property="pointname" filter="true"/></td>
		<%
		if(k%3==0)
		{
		%>
		</tr><tr>
		<%
		}
		%>
		</logic:iterate>
		<%
		if(k%3!=0 && k%3==1)
		{
		%>
		<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
		<%
		}
		if(k%3!=0 && k%3==2)
		{
		%>
		<td>&nbsp;</td><td>&nbsp;</td></tr>
		<%
		}
		%>
 </table>
</td>
</tr>
</table>
<%if(userview.getVersion()<50){%>
<div id='menu_'  tabindex='1' onblur='hiddenElement()' hidefocus="true"  style="background:#ffffff;border:1px groove black;outline:0;width:85;height:100 " >
<%}else{ %>
<div id='menu_'  tabindex='1' onblur='hiddenElement()' hidefocus="true"  style="background:#ffffff;border:1px groove black;outline:0;width:85;height:80 " >
<%} %>
	<table>
	<%--去掉3D立体图  --%>
	<%--<tr onclick="alertOption('1');"><td><input type='checkbox' value="1" id="3d" name='3D'<logic:equal name="khResultForm" property="isShow3D" value="1"> checked </logic:equal>/></td><td id="b_1" style="cursor:hand" onMouseOver="changeBgColor('b_1')" onMouseOut="goBackBgColor('b_1')"><bean:message key="lable.performance.3DChart"/></td></tr>--%>
	<%if(userview.getVersion()<50){%>
	<tr onclick="alertOption('2');"><td><input type='checkbox' value="1" id="score" name="Score" <logic:equal name="khResultForm" property="isShowScore" value="1"> checked </logic:equal>/></td><td id="b_2" style="cursor:hand" onMouseOver="changeBgColor('b_2')" onMouseOut="goBackBgColor('b_2')"><bean:message key="lable.performance.showScore"/></td></tr>
	<%} %>
	<tr onclick="alertOption('3');"><td><Img src='/images/45.bmp' /></td><td id="b_3" style="cursor:hand" onMouseOver="changeBgColor('b_3')" onMouseOut="goBackBgColor('b_3')"><bean:message key="lable.performance.graph"/></td></tr>
	<tr onclick="alertOption('4');"><td><Img src='/images/42.bmp' /></td><td id="b_4" style="cursor:hand" onMouseOver="changeBgColor('b_4')" onMouseOut="goBackBgColor('b_4')"><bean:message key="lable.performance.histogram"/></td></tr>
	<tr onclick="alertOption('5');"><td><Img src='/images/47.bmp' /></td><td id="b_6" style="cursor:hand" onMouseOver="changeBgColor('b_6')" onMouseOut="goBackBgColor('b_6')"><bean:message key="lable.performance.radar"/></td></tr>
	
	<logic:notEqual  name="khResultForm" property="graphType"  value="3" >
		<tr><td id="b_5" style="cursor:hand" onMouseOver="changeBgColor('b_5')" onMouseOut="goBackBgColor('b_5')" colspan="2" align='center' onclick='set()' ><bean:message key="conlumn.investigate.questionItem"/>...</td></tr>
	</logic:notEqual>
	
	</table>
	</div>
	<input type='hidden' name='chartParameters'  value="${khResultForm.chartsets}" />
	<script language='javascript'>
	document.getElementById('menu_').style.display="none";
	</script>
</html:form>
</body>
</html>