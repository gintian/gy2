<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%
	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String statusInfo=(String)userView.getHm().get("statusInfo");
	
	if(userView != null)
	{
		css_url=userView.getCssurl();
		if(css_url==null||css_url.equals(""))
		css_url="/css/css1.css";
	}
	
%>


<HTML>
<HEAD>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
<link href="<%=css_url%>" rel="stylesheet" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
</HEAD>
<hrms:themes />
<body topmargin="0" leftmargin="5" marginheight="0" marginwidth="0">
	<table width="100%" align="left" border="0" cellpadding="0"
		cellspacing="0" class="mainbackground">
		<tr>
			<td valign="top">
				<div id="treemenu"></div>
			</td>
		</tr>
	</table>

<BODY>
</HTML>


<SCRIPT LANGUAGE=javascript>
	
	var m_sXMLFile	= "report_set_tree.jsp?statusInfo=<%=statusInfo%>&flag=1";		
	var newwindow;
	var root=new xtreeItem("root",REPORTCLASSIFY,"","il_body",REPORTCLASSIFY,"/images/add_all.gif",m_sXMLFile);
	
	root.setup(document.getElementById("treemenu"));

	if(newwindow!=null)
	{
		newwindow.focus();
	}
	if(parent.parent.myNewBody!=null)
	{
		parent.parent.myNewBody.cols="*,0"
	}
	
	
	
	
	function updateState()
	{
		var currnode=Global.selectedItem;  
		if(currnode.text.indexOf("(未")!=-1)
		{
	    	var codeitemdesc=currnode.text.substring(0,currnode.text.indexOf("(未"))+"("+EDITING+")";
	    	currnode.setText(codeitemdesc);
	    	currnode.reload(1);
		}
	
	}
	initTreeNode();
	function initTreeNode()
	{
		 var obj=root.childNodes[0];
	 	 if(obj)
	  	{
	  		obj.expand();
	    	var objfirst=obj.childNodes[0];
	    	selectedClass("treeItem-text-"+objfirst.id);
	    	objfirst.select();
	    }
	   }
</SCRIPT>