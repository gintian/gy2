<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<html>
<head>

<title>Insert title here</title>
</head>

	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
	<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>    
<style>
div#treemenu {
BORDER-BOTTOM:#94B6E6 1pt solid; 
BORDER-COLLAPSE: collapse;
BORDER-LEFT: #94B6E6 1pt solid; 
BORDER-RIGHT: #94B6E6 1pt solid; 
BORDER-TOP: #94B6E6 1pt solid; 
width: 380px;
height: 280px;
overflow: auto;
}

</style>
<body >
  	<table width="100%" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
	<tr>  
		<td valign="top">
			<table><tr><td>&nbsp;</td><td>	
			<div id="treemenu"></div>
			</td></tr></table>
		</td>	
	</tr>
	<tr>
		<td align='center' >
			<input type='button'  class="mybutton" value='<bean:message key="lable.tz_template.enter"/>'  onclick='enter()' />
			<input type='button'  class="mybutton" value='<bean:message key="lable.tz_template.cancel"/>'  onclick='goback()' />&nbsp;&nbsp;
		</td>
	</tr>
	</table>

</body>
</html>
<%
String objectid = request.getParameter("objectid");
String objectType = request.getParameter("objectType");
%>
<SCRIPT LANGUAGE=javascript>

	var info = [];
	if(window.dialogArguments){
    	info=dialogArguments;  // 0:objectid  1:objectType  2:planid
	} else {
		info.push('<%=objectid%>');
		info.push('<%=objectType%>');
	}
	var m_sXMLFile	= "/performance/kh_system/kh_template/create_point_tree.jsp?templateID="+info[0]+"&pointsetid=0&flag=0&subsys_id="+info[1];		
	var newwindow;
	var root=new xtreeItem("root","<bean:message key="static.select"/>","","mil_body","<bean:message key="static.select"/>","/images/add_all.gif",m_sXMLFile);
	Global.defaultInput=2;
	Global.showroot=false;
	root.setup(document.getElementById("treemenu"));	
	if(newwindow!=null)
	{
		newwindow.focus();
	}
	if(parent.parent.myNewBody!=null)
	{
		parent.parent.myNewBody.cols="*,0"
	}
	
	
	
	function goback()
	{
		if(parent && parent.parent && parent.parent.Ext && parent.parent.selectpointWinClose){
			parent.parent.selectpointWinClose();
		} else {
			returnValue="";
			window.close();
		}
	}
	
	function enter()
	{
		if(root.getSelected()=="")
		{
				alert("请选择绩效指标！");
				return;
		}	
		
		var temps=root.getSelected().split(",");
		var points="";
		for(var i=0;i<temps.length;i++)
		{
			if(trim(temps[i]).length>0)
			{
				if(temps[i].substring(0,2)!='1_')
				{
					alert("不能选择指标类别");
					return;
				}
				points+=","+temps[i].substring(2);
			}
		}
		if(parent && parent.parent && parent.parent.Ext && parent.parent.selectPoint_ok){
			parent.parent.selectPoint_ok(points);
		} else {
			returnValue=points;
			window.close();
		}
		
	}
	
</script>
	
