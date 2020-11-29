<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="javascript" src="/js/constant.js"></script>
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
<hrms:themes/>
   <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
</HEAD>
<body style="margin-left:0px;margin-top:10px">
            <div id="treemenu" style="height: 100%;width:100%"></div>
		    <SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
		     <SCRIPT LANGUAGE=javascript>
		       var m_sXMLFile="/pos/posbusiness/get_code_tree.jsp?codesetid=${courseForm.codesetid}&codeitemid=&action=/train/resource/course/pos.do&target=nil_body&backdate=${courseForm.backdate }&checked=${courseForm.checked }&validateflag=${courseForm.validateflag }";	 //
		       var root=new xtreeItem("root","${courseForm.codesetdesc}","/train/resource/course/pos.do?b_query=link&a_code=${courseForm.codesetid}","nil_body","${courseForm.codesetdesc}","/images/spread_all.gif",m_sXMLFile);
		       root.setup(document.getElementById("treemenu"));
		       root.openURL();
		    </SCRIPT>     
<BODY>
</HTML>
