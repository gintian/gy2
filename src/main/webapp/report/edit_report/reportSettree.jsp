<%@page import="java.net.URLEncoder"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.report.edit_report.EditReportForm"%>
<%
	String css_url = "/css/css1.css";
	UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	EditReportForm editReportForm = (EditReportForm) session.getAttribute("editReportForm");
	if (userView != null) {
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
			css_url = "/css/css1.css";
	}
	String tabid = editReportForm.getTabid();

	String operate = "edit";
	if (request.getParameter("operate") != null)
		operate = request.getParameter("operate");
	String selectuid = "";
	if (request.getParameter("selectuid") != null)
		selectuid = request.getParameter("selectuid");
%>


<HTML>
<HEAD>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
<link href="<%=css_url%>" rel="stylesheet" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<hrms:themes />
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
<script language="javascript" src="/js/constant.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
</HEAD>
<body topmargin="0" leftmargin="5" marginheight="0" marginwidth="0"
	style="overflow-y: visible;height:99%">
	<table width="100%" align="left" border="0" cellpadding="0"
		cellspacing="0" class="mainbackground">
		<tr>
			<td valign="top">
				<div id="treemenu" style="overflow-y: visible"></div>
			</td>
		</tr>
	</table>
<BODY>
</HTML>


<SCRIPT LANGUAGE=javascript>
	
	var m_sXMLFile	= "report_set_tree.jsp?flag=1&codeid=0&userName=<%=(URLEncoder.encode(userView.getUserName(),"UTF-8"))%>&operate=<%=operate%>";		
	var newwindow;
	var href;
	
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
	
	initTreeNode();
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
	
function initTreeNode()
{
//  initDocument();
	  var obj=root.childNodes[0];
	  if(obj)
	  {
	   
	   <% if(!operate.equals("collect")){%>
		    obj.expand();
		    var objfirst=obj.childNodes[0];
		    var ss = "<%=selectuid %>";
		    var selectid ="";
		     <% if(!selectuid.equals("")){%>
		    for(var j=0;j<root.childNodes.length;j++)
					{
					 var obj2 = root.childNodes[j];
					 obj2.expand();
					for(var a=0;a<obj2.childNodes.length;a++){
						if(obj2.childNodes[a].uid==ss)
						{
							selectid=obj2.childNodes[a].id;
							break;
						}
						if(a==obj2.childNodes.length-1)
						obj2.collapse();
						}
					}
					if(selectid!=""){
					  selectedClass("treeItem-text-"+selectid);
					}else{
			    selectedClass("treeItem-text-"+objfirst.id);
			    }
			href="/report/edit_report/reportSettree.do?b_query=link&operateObject=1&code=<%=selectuid %>";
			parent.mil_body.location=href;
				<%}else{%>
			selectedClass("treeItem-text-"+objfirst.id);
			href="/report/edit_report/reportSettree.do?b_query=link&operateObject=1&code="+objfirst.uid;
			parent.mil_body.location=href;
				<%}%>
		<%}else{%>
			obj.expand();
	    	var objfirst=obj.childNodes[0];
	    	selectedClass("treeItem-text-"+objfirst.id);
	    	objfirst.select();
			<%}%>
	}

	}
</SCRIPT>