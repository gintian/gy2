<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<html>
<head>

<title>Insert title here</title>
</head>

	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
	<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>    
<style>

div#treemenu 
{
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-COLLAPSE: collapse;
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
	width: 386px;
	height: 280px;
	overflow: auto;
}

</style>
<link href="/performance/objectiveManage/objectiveCard/objectiveCard.css" rel="stylesheet" type="text/css">
<body>
  	<table width="100%" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
	<tr>  
		<td valign="top">
			<table><tr><td>&nbsp;&nbsp;</td><td>	
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
String planid = request.getParameter("planid");
%>
<SCRIPT LANGUAGE=javascript>
    var info=new Array(3);  // 0:objectid  1:objectType  2:planid
    info[0]='<%=objectid%>';
    info[1]='<%=objectType%>';
    info[2]='<%=planid%>';
	var m_sXMLFile	= "/performance/objectiveManage/objectiveCard/per_point_tree.jsp?objectid="+info[0]+"&objectType="+info[1]+"&flag=0&id=0&planid="+info[2];		
	var newwindow;
	var root=new xtreeItem("root","<bean:message key="static.select"/>","","mil_body","<bean:message key="static.select"/>","/images/add_all.gif",m_sXMLFile);
	Global.defaultInput=1;
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
		if(parent && parent.parent && parent.parent.Ext && parent.parent.importPerPoint_closeWin){
			parent.parent.importPerPoint_closeWin();
		} else {
			parent.window.returnValue="";
			window.close();
		}
	}
	
	function enter()
	{
		if(root.getSelected()=="")
		{
				alert(ACHIEVEMENT_INFO4+"！");
				return;
		}	
		
		var temps=root.getSelected().split(",");
		var points="";
		for(var i=0;i<temps.length;i++)
		{
			if(trim(temps[i]).length>0)
			{
				if(temps[i].length<4||temps[i].substring(0,3)!='pp_')
				{
					alert(ACHIEVEMENT_INFO5);
					return;
				}
				points+=","+temps[i].substring(3);
			}
		}
		if(parent && parent.parent && parent.parent.importPerPoint_ok){
			parent.parent.importPerPoint_ok(points);
		} else {
			parent.window.returnValue=points;
			window.close();
		}
	}
	
</script>
	
