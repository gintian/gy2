<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="java.util.*,
                 com.hjsj.hrms.actionform.performance.PerAnalyseForm,
                 org.apache.commons.beanutils.LazyDynaBean" %>
<%
	String css_url="/css/css1.css";	
%>

<HTML>		

<HEAD>
	<TITLE>
	</TITLE>
	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
	<hrms:themes />
	<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>
	<script LANGUAGE=javascript src="/performance/achivement/achivementTask/achievement.js"></script> 
	<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT> 
	
	<script language="javascript" src="/ajax/constant.js"></script>
	<script language="javascript" src="/ajax/basic.js"></script>
	<script language="javascript" src="/ajax/common.js"></script>
	<script language="javascript" src="/ajax/control.js"></script>
	<script language="javascript" src="/ajax/dataset.js"></script>
	<script language="javascript" src="/ajax/editor.js"></script>
	<script language="javascript" src="/ajax/dropdown.js"></script>
	<script language="javascript" src="/ajax/table.js"></script>
	<script language="javascript" src="/ajax/menu.js"></script>
	<script language="javascript" src="/ajax/tree.js"></script>
	<script language="javascript" src="/ajax/pagepilot.js"></script>
	<script language="javascript" src="/ajax/command.js"></script>
	<script language="javascript" src="/ajax/format.js"></script>
	<script language="javascript" src="/js/validate.js"></script>
	    
	<SCRIPT LANGUAGE=javascript>
    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
		
</SCRIPT>     
</HEAD>



<body   topmargin="10" leftmargin="5" marginheight="0" marginwidth="0">
<html:form action="/performance/perAnalyse">

<table width="100%" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
	<tr>  
		<td valign="top">
			<div id="treemenu" style='padding-top:5px;'></div>
		</td>
	 
	</tr>
</table>	



	<script language='javascript' >  
	unityGrade();
	
	var m_sXMLFile	= "/performance/achivement/achivementTask/achivement_task_tree.jsp?opt=0&codeid=0&num1="+num;		
	var newwindow;
	var root=new xtreeItem("root",ACHIEVEMENTTASK,"","mil_body",ACHIEVEMENTTASK,"/images/add_all.gif",m_sXMLFile);
	Global.defaultInput=0;
	root.setup(document.getElementById("treemenu"));	
	root.expandAll();
	if(newwindow!=null)
	{
		newwindow.focus();
	}
	if(parent.parent.myNewBody!=null)
	{
		parent.parent.myNewBody.cols="*,0"
	}
	autoSelectNode();
	
	
	
	
	
	
	</script>
	
</html:form>
</body>
</HTML>
	
	

