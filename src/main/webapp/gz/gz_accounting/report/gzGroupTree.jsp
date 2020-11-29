<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>

<HTML>
<HEAD>
	<TITLE>
	</TITLE>
	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
	<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
	<SCRIPT LANGUAGE=javascript>
	
	function goback()
	{
		window.close();
	}
	
	
	function enter()
	{
		returnValue=root.getSelected();
		window.close();
	
	}
	
	
	
</SCRIPT>     
</HEAD>
<style>
div#treemenu {
BORDER-BOTTOM:#94B6E6 1pt inset; 
BORDER-COLLAPSE: collapse;
BORDER-LEFT: #94B6E6 1pt inset; 
BORDER-RIGHT: #94B6E6 1pt inset; 
BORDER-TOP: #94B6E6 1pt inset; 
width: 340px;
height: 240px;
overflow: auto;
}

</style>
<hrms:themes />
<body   topmargin="0" leftmargin="5" marginheight="0" marginwidth="0">
<html:form action="/gz/gz_accounting/report">

	<table width="100%" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
	<tr>
		<td colspan="2">&nbsp;&nbsp;&nbsp;请选择第一分组项范围...&nbsp;&nbsp;<br></td>
	</tr>
	<tr>  
		<td valign="top">
			
			<table><tr><td>&nbsp;</td><td>	
			<div id="treemenu"></div>
			</td></tr></table>
			
		</td>	
		<td valign="top"  >
			
			
					<table border=0 width="100%" height="100%" >
					<tr><td height='150' valign='top' >
						 <input type='button'  class="mybutton" value=' 打开 '  onclick='enter()' />
			           <br> 
			           <input type='button'  class="mybutton" value=' 取消 '  onclick='goback()' />
					</td></tr>
					<tr><td height='90' align='left'  valign='bottom' > 
					&nbsp;
			          
					</td></tr>
					</table>
		
		</td>
	</tr>
	
	
</table>	

</html:form>
<BODY>
</HTML>


<SCRIPT LANGUAGE=javascript>
	var rsid;
	var rsdtlid;
	var focus_obj_node;

	
	var m_sXMLFile	= "/gz/gz_accounting/report/gz_group_tree.jsp?codesetid=${gzReportForm.gzGroupCodesetid}&codeitemid=${gzReportForm.gzGroupCodesetid}/${gzReportForm.gzGroupCodeitemid}";		
	var newwindow;
	var root=new xtreeItem("root","分组范围","","mil_body","分组范围","/images/add_all.gif",m_sXMLFile);
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
	
	
	var focus_obj_node=root.getFirstChild();
	focus_obj_node.expand();
	
	
</script>
	