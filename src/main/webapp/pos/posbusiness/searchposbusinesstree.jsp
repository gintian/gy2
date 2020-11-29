<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="javascript" src="/js/constant.js"></script>
<script type="text/javascript" src="../../../../js/hjsjUrlEncode.js"></script>
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
		if("68"==uid.substring(0,2))
   	 		imgurl="/images/book.gif";
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
<body   topmargin="0" leftmargin="5" marginheight="0" marginwidth="0">
   <div id="treemenu" ></div>
    <SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
     <SCRIPT LANGUAGE=javascript>
       //屏蔽右键
       document.body.oncontextmenu=function(){return false;};
       var codesetid = "${posBusinessForm.codesetid}";
       var codesetdesc = $URL.encode("${posBusinessForm.codesetdesc}");
       var url = "/pos/posbusiness/searchposbusinesslist.do?b_query=link&fromflag=${posBusinessForm.fromflag}&a_code=${posBusinessForm.codesetid}&codeitem=${posBusinessForm.codeitem}&codesetdesc=codesetdesc";
       if(codesetid == "68")
    	   url = "/pos/posbusiness/searchposbusinesslist.do?b_query=link&fromflag=3&a_code=${posBusinessForm.codesetid}&codeitem=${posBusinessForm.codeitem}&codesetdesc=codesetdesc";
       else if (codesetid == "79")
    	   url = "/pos/posbusiness/searchposbusinesslist.do?b_query=link&fromflag=5&a_code=${posBusinessForm.codesetid}&codeitem=${posBusinessForm.codeitem}&codesetdesc=codesetdesc";
   
       var m_sXMLFile="/pos/posbusiness/get_code_tree.jsp?codesetid=${posBusinessForm.codesetid}&codeitemid=&action=/pos/posbusiness/searchposbusinesslist.do";	 //
       var root=new xtreeItem("root","${posBusinessForm.codesetdesc}",url,"mil_body","${posBusinessForm.codesetdesc}","/images/spread_all.gif",m_sXMLFile);
       root.setup(document.getElementById("treemenu"));
       root.openURL();
    </SCRIPT>
<BODY>
</HTML>
