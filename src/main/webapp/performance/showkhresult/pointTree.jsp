<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ page import="com.hjsj.hrms.interfaces.performance.PointTreeByXml,
				 com.hjsj.hrms.actionform.performance.showkhresult.DirectionAnalyseForm" %>
<%
	String css_url="/css/css1.css";
	DirectionAnalyseForm directionAnalyseForm=(DirectionAnalyseForm)session.getAttribute("directionAnalyseForm");
	String isItem=directionAnalyseForm.getIsItem();
	String template_id=directionAnalyseForm.getTemplate_id();
	//String template_id="KTDF_01";
	String flag="2";
	if(isItem.equals("1"))
		flag="1";
%>


<HTML>
<HEAD>
	<TITLE>
	</TITLE>
<hrms:themes />
	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
	<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
	<SCRIPT LANGUAGE=javascript>
</SCRIPT>     
</HEAD>
<body   topmargin="0" leftmargin="5" marginheight="0" marginwidth="0">
	<table width="100%" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
	
	<tr>  
		<td valign="top">
			<div id="treemenu"></div>
		</td>
	</tr>
	<tr>  
		<td valign="top"  >
			<table width='100%' border=0 ><tr>
			<td align='right' width='105' ><input type='checkbox' name='totalscore' value='totalscore' >&nbsp;总分</td>
			<td align='left' >&nbsp;&nbsp;<input type='button'  class="mybutton" value='生成分析图'  onclick='execute()' /></td>
			</tr></table>
		</td>
	</tr>
	
	
</table>	
<BODY>
</HTML>


<SCRIPT LANGUAGE=javascript>
	
	function execute()
	{
		var values="";
		var oo=eval('totalscore');
		if(oo.checked==true)
			values+=oo.value+",";
		values=values+root.getSelected();
	   
		if(values.length>0)
		{
			
			 window.open("/performance/showkhresult/showDirectionAnalyse.do?b_show=show&values="+values,"mil_body");
		}
		else
			alert("请选择考核项目！");
	}
	
	
	
	
	
	
	
	var m_sXMLFile	= "point_tree.jsp?isItem=<%=isItem%>&template_id=<%=template_id%>&id=0&flag=<%=flag%>";		
	var newwindow;
	var root=new xtreeItem("root","考核项目","","mil_body","考核项目","/images/add_all.gif",m_sXMLFile);	
	Global.defaultInput=1;
	Global.showroot=false;
	
	root.setup(document.getElementById("treemenu"));	
	if(newwindow!=null)
	{
		newwindow.focus();
	}
	if(parent.parent.myNewBody!=null)
	{
		parent.parent.myNewBody.cols="*,0"
	}
	
	root.allSelect();
	execute();
	
	
	

</SCRIPT>