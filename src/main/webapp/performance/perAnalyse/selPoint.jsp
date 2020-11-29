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
width: 380px;
height: 260px;
overflow: auto;
BORDER-COLLAPSE: collapse;
 border: inset 1px #C4D8EE;
 BORDER-BOTTOM: #C4D8EE 1pt solid; 
 BORDER-LEFT: #C4D8EE 1pt solid; 
 BORDER-RIGHT: #C4D8EE 1pt solid; 
 BORDER-TOP: #C4D8EE 1pt solid; 
}

</style>
<body topmargin="0" leftmargin="5" marginheight="0" marginwidth="0" >
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

<SCRIPT LANGUAGE=javascript>
	
	var busitype = <%=request.getParameter("busitype")%>;		
	var planids=getCookie('plansSel3');
	if(busitype!=null && busitype=='1')
		planids =  getCookie('modalPlansSel3');
	if(planids==null)
		planids="";
	var m_sXMLFile	= "/performance/perAnalyse/create_point_tree.jsp?planids="+planids+"&pointsetid=0&flag=0&busitype="+busitype;		
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
        parent.window.returnValue="";
		if(!window.showModalDialog){
			window.parent.Ext.getCmp("selectPointWin").close();
		}else{
			window.close();
		}
	}
	
	function enter()
	{
		if(root.getSelected()=="")
		{
			if(busitype!=null && busitype=='1')
				alert("请选择素质指标！");
			else
				alert("请选择绩效指标！");
			return;
		}	
		
		var temps=root.getSelected().split(",");
		var points="";
		for(var i=0;i<temps.length;i++)
		{
			if(trim(temps[i]).length>0)
			{	
				points+=","+temps[i];
			}
		}
		
		if(!window.showModalDialog){
			window.parent.selectPoint_OK(points);
			window.parent.Ext.getCmp("selectPointWin").close();
		}else{
            parent.window.returnValue=points;
            parent.window.close();
		}
	}
	
</script>
	
