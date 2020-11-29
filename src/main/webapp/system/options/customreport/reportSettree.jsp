<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
		css_url=userView.getCssurl();
		if(css_url==null||css_url.equals(""))
		css_url="/css/css1.css";
	}
	
	String operate="edit";
	if(request.getParameter("operate")!=null)
		operate=request.getParameter("operate");
	
%>


<HTML>
<HEAD>
	<TITLE>
	</TITLE>
	<hrms:themes></hrms:themes>
	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
	<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
	<script language="javascript" src="/js/constant.js"></script>     
	<SCRIPT LANGUAGE=javascript>
</SCRIPT>     
</HEAD>
<body   topmargin="0" leftmargin="0" marginheight="0" marginwidth="0">
	<table width="100%" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">	
	<tr>  
		<td valign="top">
			<div id="treemenu" style="width:470px;height: 380px;overflow:auto;" class="complex_border_color"></div>
			 
		</td>
	</tr>
	<tr>
		<td align="center" height="35px;">
    <input type="button" name="btnok" value="确定" class="mybutton" onclick="savecode();">
    <%--<input type="button" name="btnok" value="确定" class="mybutton" onclick="savecode();window.close();">--%>
    <%--<input type="button" name="btncancel" value="取消" class="mybutton" onclick="window.close();"> --%>
    <input type="button" name="btncancel" value="取消" class="mybutton" onclick="parent.winClose();">
		</td>
	</tr>
</table>	

<BODY>
</HTML>

<SCRIPT LANGUAGE=javascript>
	Global.defaultInput=2;  
    //Global.defaultradiolevel=2; 
    Global.showroot = false;
	var m_sXMLFile	= "/system/options/customreport/report_set_tree.jsp?flag=1&codeid=0&userName=<%=(userView.getUserName())%>&operate=<%=operate%>";		
	var newwindow;
	var root=new xtreeItem("root",REPORTCLASSIFY,"","",REPORTCLASSIFY,"/images/add_all.gif",m_sXMLFile);
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
	function savecode() {
		var thevo=new Object();
		thevo.content=root.getSelected();
		thevo.title=root.getSelectedTitle();
         // window.returnValue=thevo;
     	// window.close();
        parent.return_vo = thevo;
        if(parent.Ext.getCmp('customreport')){
            parent.Ext.getCmp('customreport').close();
        }
    }

</SCRIPT>