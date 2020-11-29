<%@ page language="java" %>
<HTML>
<HEAD>
<TITLE>
</TITLE>

<link rel="stylesheet" type="text/css" href="/css/XMLSelTree.css">

</HEAD>
<body   topmargin="0" leftmargin="5" marginheight="0" marginwidth="0" bgcolor="#f2f8ff" text="#000000" oncontextmenu="return false">
   <DIV id="SrcDiv" onselectstart="selectstart()"></DIV>
<BODY>
</HTML>

<SCRIPT LANGUAGE=javascript src="/js/XMLSelTree.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript>
<!--
var m_sXMLFile	= "/system/getfunctionlist.jsp?parentId=0";	 //
//var m_sXMLFile	= "TreeNode.xml";	 //

var m_sXSLPath	= "/css";			 //xls
var m_oSrcDiv	= SrcDiv;			 // 
function window.onload()
{
    InitTree(m_sXMLFile, m_sXSLPath, m_oSrcDiv);
}

/************************************************
** GoLink(p_sHref, p_sTarget)
************************************************/
function GoLink(p_sHref, p_sTarget)
{
	var sHref	= p_sHref;
	var sTarget	= p_sTarget;
	window.open(sHref, sTarget);
}
//-->
</SCRIPT>