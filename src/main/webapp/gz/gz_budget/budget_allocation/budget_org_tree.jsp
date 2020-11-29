<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.dao.RecordVo,
                 com.hjsj.hrms.utils.ResourceFactory,
                 com.hjsj.hrms.actionform.gz.gz_budget.budget_allocation.BudgetAllocationForm,
                 com.hrms.frame.utility.AdminCode"%>

<%
	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
		css_url=userView.getCssurl();
		if(css_url==null||css_url.equals(""))
			css_url="/css/css1.css";
	}
	BudgetAllocationForm budgetAllocationForm = (BudgetAllocationForm)session.getAttribute("budgetAllocationForm");
	String rootUnitcode = budgetAllocationForm.getTopUnitId();
	String rootText = null;
	String XMLFile = null;	 
	String rootAction = null;
	if(rootUnitcode!=null&&rootUnitcode.length()>0)
	{
		rootText = AdminCode.getCode("UN", rootUnitcode).getCodename();
		XMLFile = "budget_org_tree_xml.jsp?topunit="+rootUnitcode;	 
		rootAction = "/gz/gz_budget/budget_allocation/budget_allocation.do?b_query=query&a_code=UN"+rootUnitcode;
	}
	else{
		rootText = ResourceFactory.getProperty("tree.orgroot.orgdesc");
		XMLFile = "budget_org_tree_xml.jsp?topunit=";	 
		rootAction = "/gz/gz_budget/budget_allocation/budget_allocation.do?b_query=query&a_code=UN";
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
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
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
</HEAD>
<body>
<html:form action="/gz/gz_budget/budget_allocation/budget_allocation"> 
	<table  align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
	<tr>  
		<td valign="top" >
			<div id="treemenu"></div>
		</td>
	 
	</tr>
</table>	

</html:form>
<BODY>
</HTML>		
<script>
    initDocument();

	var root=new xtreeItem("root","<%=rootText%>","<%=rootAction%>","mil_body",ORGANIZATION,"/images/root.gif","<%=XMLFile%>");

	root.setup(document.getElementById("treemenu"));
    root.select();
    selectedClass("treeItem-text-"+root.id);
 
</script>
