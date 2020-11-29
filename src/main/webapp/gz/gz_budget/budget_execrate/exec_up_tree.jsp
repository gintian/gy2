<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.businessobject.report.tt_organization.TTorganization"%>
<%@ page import="com.hrms.frame.dao.RecordVo,
com.hjsj.hrms.actionform.report.org_maintenance.SearchReportUnitForm,
                 com.hjsj.hrms.utils.ResourceFactory"%>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_budget.BudgetExecRateForm,
                 com.hrms.frame.utility.AdminCode,
                 com.hjsj.hrms.businessobject.gz.gz_budget.budget_examination.BudgetExamBo" %>
<%@ page import="java.sql.*"%>
<%@ page import="com.hrms.frame.utility.AdminDb"%>
<%
	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
		css_url=userView.getCssurl();
		if(css_url==null||css_url.equals(""))
		css_url="/css/css1.css";
	}
	BudgetExecRateForm budgetForm = (BudgetExecRateForm)session.getAttribute("budgetExecRateForm");
	String Budget_id = budgetForm.getBudget_id();	
	String rootUnitcode =budgetForm.getRootUnitCode();
	
	String rootTitle = ResourceFactory.getProperty("tree.orgroot.orgdesc");
	String rootcode = "root";
	String rootText = null;
	String XMLFile = null;	 
	String rootAction = null;
	if(rootUnitcode!=null&&rootUnitcode.length()>0)	{
		String status="";
		if (!status.equals(""))  status ="("+status+")";
		rootcode="UN"+rootUnitcode;
		rootText = AdminCode.getCode("UN", rootUnitcode).getCodename()+status;		
		rootTitle =AdminCode.getCode("UN", rootUnitcode).getCodename();
		XMLFile = "exec_orgtree_xml.jsp?topunit="+rootUnitcode;	 
		rootAction = "/gz/gz_budget/budget_execrate.do?b_query=query&a_code=UN"+rootUnitcode;
	}
	else{
		rootText = ResourceFactory.getProperty("tree.orgroot.orgdesc");
		XMLFile = "exec_orgtree_xml.jsp?topunit=";	 
		rootAction = "/gz/gz_budget/budget_execrate.do?b_query=query&a_code=UN";
	}

%>
		
	
<HTML>
<HEAD>
	<link href="<%=css_url%>" rel="stylesheet" type="text/css">
	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
	<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>  
	<script LANGUAGE=javascript src="/js/xtree.js"></script> 
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
<script language="javascript" src="/js/dict.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script language="javascript">
    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>

 
<SCRIPT LANGUAGE=javascript>
	 
	var ViewProperties=new ParameterSet();	
	function hides(targetId){
		if (document.getElementById(targetId)){
			target = document.getElementById(targetId);
			target.style.display = "none";
		}
	}
	function toggles(targetId){
		if (document.getElementById(targetId)){
			target = document.getElementById(targetId);
			target.style.display = "block";
		}
	} 
	
	
</SCRIPT>
<style> 
<!--
	body {
	margin-left: 0px;
	margin-top: 0px;
	margin-right: 0px;
	margin-bottom: 0px;
	}
-->
</style>     
<hrms:themes />
<hrms:themes />
</HEAD>
<body >
<html:form action="/gz/gz_budget/budget_execrate"> 
<input type ="hidden" id="topunit" value=<%=rootcode %> >
	<table  align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
		<tr>  
			<td valign="top" >
				
				<div id="treemenu"></div>
	        
			</td>
		 
		</tr>
</table>	

</html:form>
</BODY>
</HTML>		
<SCRIPT LANGUAGE=javascript>

    initDocument();

	var root=new xtreeItem("<%=rootcode%>","<%=rootText%>","<%=rootAction%>","mil_body","<%=rootTitle%>","/images/root.gif","<%=XMLFile%>");

	root.setup(document.getElementById("treemenu"));
    var rootunitdcode = '${budgetExecRateForm.rootUnitCode}';

	if (rootunitdcode==""){
	   if(root.getFirstChild())
	   {
	     root.getFirstChild().select();
	     selectedClass("treeItem-text-"+root.getFirstChild().id);
	   }
	}
	else {
       root.select();
       selectedClass("treeItem-text-"+root.id);
	}




</script>
