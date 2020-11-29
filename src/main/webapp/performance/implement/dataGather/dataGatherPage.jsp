<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%
	String css_url="/css/css1.css";
	
%>

<HTML>
<HEAD>
	<TITLE>
	</TITLE>

	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
	<hrms:themes />
	<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>  
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
	<SCRIPT LANGUAGE=javascript>
</SCRIPT>     
</HEAD>



<body   topmargin="10" leftmargin="5" marginheight="0" marginwidth="0">
<html:form action="/performance/implement/dataGather">

<table width="100%" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
	<tr style="display:none">
		<td nowrap><bean:message key="kh.field.plan"/>:&nbsp;
		<html:select name="dataGatherForm" property="planId" size="1" onchange="changePlan()">
	  	 <html:optionsCollection property="planList" value="dataValue" label="dataName"/>
		</html:select>
		<Br>  </td>
	</tr>
	
	<tr>  
		<td valign="center">
			<div id="treemenu"></div>
		</td>
	 
	</tr>
</table>	



</html:form>
<BODY>
</HTML>
	
	<script language='javascript' >
	var objectID="";	
	var mainbodyID="";
	var m_sXMLFile	= "/performance/implement/dataGather/per_object_tree.jsp?planId=${dataGatherForm.planId}&codeid=-1&model=0&codesetid=UN";		
	var newwindow;
	var root=new xtreeItem("root",P_PEROBJECT,"","mil_body",P_PEROBJECT,"/images/unit.gif",m_sXMLFile);
	Global.defaultInput=0;
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
	
	var obj=root;
	var i=0;
	var ori_text="";
	while(obj.getFirstChild()&&ori_text!=obj.getFirstChild().text)
	{
		ori_text=obj.getFirstChild().text;
		i++;
		obj.getFirstChild().expand();
		var a_obj=obj.getFirstChild();
		obj=a_obj;
		if(i==8)
			break;
	}
	//	obj.openURL();
	if(obj)
	{
			pointObject(obj.uid);
			selectedClass("treeItem-text-"+obj.id);
	}

	
	
	//得到考核对象对应的考核主体数据
	function pointObject(objectId)
	{ 
	//	if(document.dataGatherForm.planId.options.length>0)
			parent.l_menu2.location="/performance/implement/dataGather.do?b_mainbody=query&objectId="+objectId;
		
		//	var hashvo=new ParameterSet();
		//	objectID=objectId;
		//	hashvo.setValue("objectId",objectId);
		//	hashvo.setValue("planid",'${dataGatherForm.planId}');		
		//	var request=new Request({method:'post',asynchronous:true,onSuccess:returnIsOk,functionId:'9023000008'},hashvo);
	
	
	}
	

	// 保存 or 提交
	function save(opt)
	{
		document.frames.grade.subScore(opt);
	}
	
	
	function changePlan()
	{
		document.dataGatherForm.action="/performance/implement/dataGather.do?b_query=query";
		document.dataGatherForm.target="il_body";
		document.dataGatherForm.submit();
	}
	
	</script>
	

