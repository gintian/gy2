<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="javascript" src="/js/constant.js"></script>
<%
    	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
 
%>
<script type="text/javascript">
<!--
	function add(uid,text,action,target,title,imgurl,xml)
	{
		var currnode=Global.selectedItem;
		var tmp = new xtreeItem(uid,text,action,target,title,imgurl,xml);
		currnode.add(tmp);
		//if(currnode.load)
		//	tmp.expand();
	}
	function add1(uid,text,action,target,title,imgurl,xml)
	{
		var currnode=Global.selectedItem;
		imgurl="/images/table.gif";
		var tmp = new xtreeItem(uid,text,action,target,title,imgurl,xml);
		currnode.add(tmp);
		//if(currnode.load)
		//	tmp.expand();
	}
//-->
</script>
<HTML>
<HEAD>
<TITLE>
</TITLE>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
   <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
</HEAD>
<body style="margin-left:0px;margin-top:0px">
            <div id="treemenu" style="height: expression(document.body.clientHeight-25);width:expression(document.body.clientWidth);overflow-x: no;overflow-y:no;"></div>
		    <SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
		     <SCRIPT LANGUAGE=javascript>
		       var m_sXMLFile="/pos/posbusiness/get_code_tree.jsp?codesetid=${standardPosForm.codesetid}&codeitemid=&action=/pos/standardposbusiness/searchposlist.do&target=nil_body&backdate=${standardPosForm.backdate }&checked=${standardPosForm.checked }&validateflag=${standardPosForm.validateflag }";	 //
		       var root=new xtreeItem("root","${standardPosForm.codesetdesc}","/pos/standardposbusiness/searchposlist.do?b_query=link&a_code=${standardPosForm.codesetid}","nil_body","${standardPosForm.codesetdesc}","/images/spread_all.gif",m_sXMLFile);
		       root.setup(document.getElementById("treemenu"));
		       root.openURL();
		    </SCRIPT>     
<BODY>
</HTML>
