<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="java.util.*,
                 com.hjsj.hrms.actionform.performance.PerAnalyseForm,
                 org.apache.commons.beanutils.LazyDynaBean" %>
<%
	String css_url="/css/css1.css";	
	
	String model="0";
	if(request.getParameter("model")!=null)
		model=request.getParameter("model");
		
	PerAnalyseForm perAnalyseForm=(PerAnalyseForm)session.getAttribute("perAnalyseForm");
	String busitype=perAnalyseForm.getBusitype(); // 业务分类字段 =0(绩效考核); =1(能力素质)
	String plan_ids=perAnalyseForm.getPlan_ids();
	String codeitemid = perAnalyseForm.getCodeitemid();
	String mcontrastids = perAnalyseForm.getMcontrastids();
	if(model.equals("10")||model.equals("6"))
	{
		if(perAnalyseForm.getObjectType().equals("0"))
			model="6";
		else
			model="10";
	
	}
		
%>

<HTML>
<HEAD>
	<TITLE>
	</TITLE>
	<hrms:themes />
    <meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
	<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>   
	<script language="javascript" src="/js/constant.js"></script>  
	<SCRIPT LANGUAGE=javascript>
</SCRIPT>     
</HEAD>



<body   topmargin="10" leftmargin="5" marginheight="0" marginwidth="0">
<html:form action="/performance/perAnalyse">

<table width="100%" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
	<tr>  
		<td valign="center">
			<div id="treemenu"></div>
		</td>
	 
	</tr>
</table>	



<script language='javascript' >
	var objectID="";	
	var mainbodyID="";
	
	var m_sXMLFile	= "/performance/implement/dataGather/per_object_tree.jsp?planId=<%=plan_ids%>&codeid=-1&model=<%=model%>&codesetid=UN";		
	var newwindow;
	var root=new xtreeItem("root",ORGANIZATION,"","mil_body",ORGANIZATION,"/images/unit.gif",m_sXMLFile);
	Global.defaultInput=0;
	<% if(model.equals("5")||model.equals("10")){ out.println("Global.defaultInput=1;"); } %>
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
	<% if(model.equals("10")||model.equals("5")|| model.equals("4") ||model.equals("9")){ %>
		root.expandAll();
	<% } %>
	
	//单指标分析
	function singlePointAnalyse(a0100)
	{
         parent.mil_body.singlePointAnalyse0(a0100);
	}
		
	//多指标分析
	function multiplePointAnalyse(a0100)
	{		
		 parent.mil_body.multiplePointAnalyse(a0100);	
	}
	
	//单人对比分析
	function singleContrastAnalyse(a0100)
	{
		parent.mil_body.singleContrastAnalyse(a0100);
	}	
	
	//多人对比分析
	function multipleContrastAnalyse()
	{
		return(root.getSelected());
	}
	
	//综合评测表
	function getPerResultTable(object_id)
	{
	    parent.mil_body.getPerResultTable(object_id);	
	}
	
	//选票统计
	function voteStat(codeitemid)
	{
		parent.mil_body.voteStat(codeitemid);	
	}
	
	//评语
	function getPerRemark(codeitemid)
	{
		parent.mil_body.getPerRemark(codeitemid);
	}
	
	//了解程度对比分析
	function knowContrastAnalyse(codeitemid)
	{
		parent.mil_body.knowContrastAnalyse(codeitemid);
	}
	
	// 主体分类对比分析(单考核对象)
	function mainbodyAnalyse(codeitemid)
	{		
		parent.mil_body.mainbodyAnalyse(codeitemid);	
	}
	
	
	// 人岗匹配
	function personStationAnalyse(a0100)
	{
		parent.mil_body.personStationAnalyse(a0100);
	}	
	//单指标分析 多指标分析 单人对比分析 评语 了解程度 主体分类对比分析(单考核对象)([38669]综合测评表，希望默认定位在第一个人身上，这样不至于右侧空白。,
	//综合测评表model=4,但是用的window.main.localtion,这里先选中，页面加载完之后用window.main.localtion把对应的数据加载)
	<% if(model.equals("1") || model.equals("2") || model.equals("3") || model.equals("11")  || model.equals("8") || model.equals("7") || model.equals("6") || model.equals("4")){ %>
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

	if(obj)
	{		
			selectedClass("treeItem-text-"+obj.id);	
			var plansSel=getCookie('plansSel');
			<% if(busitype!=null && busitype.trim().length()>00 && busitype.equals("1")){ %>				
				plansSel =  getCookie('modalPlansSel');
			<% } %>
			
			if(plansSel==null)
				plansSel="";
				<% if(model.equals("1") ){ %>
			parent.mil_body.location="/performance/perAnalyse.do?b_singlePointAnalyse=query0&objId="+obj.uid+"&cooki_planids="+plansSel;
				<% }else if(model.equals("2")){ %>
			parent.mil_body.location="/performance/perAnalyse.do?b_multiplePointAnalyse=query0&a0100="+obj.uid+"&cooki_planids="+plansSel;
				<% } else if(model.equals("3")){ %>
			parent.mil_body.location="/performance/perAnalyse.do?b_contrastAnalyse=query&opt=1&a0100="+obj.uid;
				<% } else if(model.equals("8")){ %>
			parent.mil_body.location="/performance/perAnalyse.do?b_perRemark=query&codeitemid="+obj.uid;
				<% }else if(model.equals("7")){ %>
			parent.mil_body.location="/performance/perAnalyse.do?b_knowAnalyse=query&codeitemid="+obj.uid;
				<% }else if(model.equals("6")){ %>
			parent.mil_body.location="/performance/perAnalyse.do?b_perMainbodyAnalyse=query&codeitemid="+obj.uid;
				<% }else if(model.equals("11")){ %>
			parent.mil_body.location="/performance/perAnalyse.do?b_personStation=query&opt=1&a0100="+obj.uid+'&isfromKhResult=0';
				<% }%>
	}
	<% } %>
	//主体分类对比分析(多考核对象)
		<% if(model.equals("10") && !codeitemid.equals("")){ %>
		    var checkitems=document.getElementsByName("treeItem-check");
        for(var i=0;i<checkitems.length;i++)
        {
          var currnode=checkitems[i];
          if('usr${perAnalyseForm.codeitemid}'.indexOf(currnode.value)!=-1)          
          	currnode.checked=true;          
        }
		parent.mil_body.location="/performance/perAnalyse.do?b_perMainbodyAnalyse=query&objects=usr${perAnalyseForm.codeitemid}";
		<% } %>
	//多人对比分析
	<% if(model.equals("5") && !mcontrastids.equals("")){ %>
		var checkitems=document.getElementsByName("treeItem-check");
		var objects='';
        for(var i=0;i<checkitems.length;i++)
        {
          var currnode=checkitems[i];
          if('${perAnalyseForm.mcontrastids}'.indexOf(currnode.value)!=-1)
          {
          	objects=objects+currnode.value+',';
          	currnode.checked=true;
          }         	          
        }
		parent.mil_body.location="/performance/perAnalyse.do?b_mcontrastAnalyse=query&opt=2&objects="+objects;
	<% } %>
	</script>
	

</html:form>
<BODY>
</HTML>
	
	

