<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import=" com.hjsj.hrms.actionform.general.relation.GenRelationForm" %>
<%
	String css_url = "/css/css1.css";
	UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	if (userView != null)
	{
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
			css_url = "/css/css1.css";
	}
	GenRelationForm genRelationForm=(GenRelationForm)session.getAttribute("genRelationForm");
	String actor_type =(String)genRelationForm.getActor_type();
	String codeitemdesc = "组织机构";
	String image = "";
	if("4".equals(actor_type)){
		codeitemdesc = "用户组";
		image="/images/group.gif";
	}else{
		codeitemdesc = "组织机构";
		image="/images/root.gif";
	}
	//String model = request.getParameter("model"); // 11:绩效实施 12:绩效评估 15:绩效评估统一打分 16:考核计划 17：考核关系 19：绩效自助考核表分发 25:人事异动审批关系
	String flag = request.getParameter("flag");
	if(flag==null)
	flag = "";
	String codeset = userView.getManagePrivCode();
	String codevalue = userView.getManagePrivCodeValue();

		//操作单位没有设置 走管理范围 
		if (userView.getUnit_id() == null || userView.getUnit_id().equals("") || userView.getUnit_id().equalsIgnoreCase("UN"))
		{
			codevalue = userView.getManagePrivCodeValue();
			codeset = userView.getManagePrivCode();	
			if ((codeset == null || codeset.trim().length() == 0) && (codevalue == null || codevalue.trim().length() == 0))
				codevalue = "";
			else if (codeset.length() != 0 && (codevalue == null || codevalue.trim().length() == 0))
				codevalue = "-1";
		} else
		{
			codevalue = userView.getUnit_id(); //.substring(2);
			if (codevalue.trim().length() == 3)
				codevalue = "-1";
			else if (codevalue.indexOf("`") == -1 && codevalue.trim().length() > 0)
				codevalue = codevalue.substring(2);
		}
	
	if(userView.isSuper_admin()||userView.getGroupId().equals("1")){
	    codevalue = "-1";
	}

	String action = "";
	if (!userView.isSuper_admin() && codevalue != null && codevalue.trim().length() > 0)
	{
		
			action = "/general/relation/relationobjectlist.do?b_query=link&codeset=" + codeset + "&operate=init";
		
		
	} else if (userView.isSuper_admin() && userView.getGroupId().equals("1"))
	{
	   
	  // action = "/general/relation/relationobjectlist.do?b_query=query&operate=init&selectAllFlag=1";
	  //田野修改添加selectAllFlag=1,标记用于判断超级用户组是否查询所以的数据  2013-2-22
			action = "/general/relation/relationobjectlist.do?b_query=query&operate=init&selectAllFlag=1";

		
	}

	String target = "ril_body1";
%>


<HTML>
	<HEAD>
		<TITLE></TITLE>
		<link href="<%=css_url%>" rel="stylesheet" type="text/css">
		<link href="/css/xtree.css" rel="stylesheet" type="text/css">
		<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
		<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
		<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
		<SCRIPT LANGUAGE=javascript>
</SCRIPT>
	</HEAD>
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
	var m_sXMLFile	= "";
	var actor_type = "${genRelationForm.actor_type}";
	if(actor_type=="1"){
	m_sXMLFile = "/hire/demandPlan/positionDemand/position_demand_tree.jsp?flag=<%=flag%>&codeid="+$URL.encode("<%=codevalue%>")+"&model=25&init=1";
	}	
	else{	
	m_sXMLFile = "/hire/demandPlan/positionDemand/position_demand_tree.jsp?flag=<%=flag%>&model=26&init=1&codeid=<%=userView.getGroupId()%>";		
	}
	var newwindow;
	var root=new xtreeItem("root","<%=codeitemdesc%>","<%=action%>","<%=target%>","<%=codeitemdesc%>","<%=image%>",m_sXMLFile);
	
	root.setup(document.getElementById("treemenu"));	
	if(newwindow!=null)
	{
		newwindow.focus();
	}
	if(parent.parent.myNewBody!=null)
	{
		parent.parent.myNewBody.cols="*,0"
	}
	
</SCRIPT>
