<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.AdminCode"%>
<%@ page import="com.hjsj.hrms.interfaces.hire.OrganizationByXml"%>
<%@ page import="com.hrms.frame.utility.CodeItem"%>
<%@ page import="java.net.URLEncoder" %>
<%
		// JinChunhai 2011.08.24 
		String spmodel="1";
		/**获得所有COOKIE*/
	    Cookie[] cookies=request.getCookies();
	    /**要找单独的Cookie就要用循环遍历*/
	    Cookie cookie=null;	  	
	    if(cookies!=null)
	    {
	      	for (int i = 0; i < cookies.length ; i++)
	      	{
	        	if (cookies[i].getName().equals("model"))
	        	{
	        		cookie=cookies[i];
	        		break;
	        	}
	        	cookie=null;     	           	        	    	      	
	      	}	      	
	      	if(cookie!=null)
	      	{
	         	spmodel=cookie.getValue();	         	
	      	}	      
	    }

	String css_url = "/css/css1.css";
	UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	String operateType = "user";
	if (request.getParameter("operateType") != null)
		operateType = request.getParameter("operateType");
	//System.out.println(operateType);
	if (userView != null)
	{
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
			css_url = "/css/css1.css";
	}
	String model = request.getParameter("model"); // 11:绩效实施 12:绩效评估 15:绩效评估统一打分 16:考核计划 17：考核关系 19：绩效自助考核表分发
	String flag = request.getParameter("flag");
	String codeset = userView.getManagePrivCode();
	String codevalue = userView.getManagePrivCodeValue();
	/**绩效管理通用机构树*/
	//业务用户(用户管理里的用户)
	//-----------有操作单位就按操作单位 没操作单位就按管理范围
	//自助用户(帐号分配里的用户)  
	//-----------如果被关联给了某个业务用户而且该业务用户设置了操作单位，就将这个操作单位作为自己的操作单位，先按照操作单位再按自己的管理范围。
	//-----------如果没有被关联给了某个业务用户就按自己的管理范围。
	
	//  绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11 getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
//	if (userView.isSuper_admin() || userView.getGroupId().equals("1"))
//		codevalue = "-1";
//	else
	{
		//操作单位没有设置 走管理范围 
		String _unitId = userView.getUnitIdByBusi("5");
		if (_unitId == null || _unitId.equals("") || _unitId.equalsIgnoreCase("UN") || _unitId.equalsIgnoreCase("UN`")) // 兼容UN和UN` lium
		{
			codevalue = userView.getManagePrivCodeValue();
			codeset = userView.getManagePrivCode();	
			if ((codeset == null || codeset.trim().length() == 0) && (codevalue == null || codevalue.trim().length() == 0))
				codevalue = "";
			else if (codeset.length() != 0 && (codevalue == null || codevalue.trim().length() == 0))
				codevalue = "-1";
		} else
		{
			codevalue = _unitId; //.substring(2);
			if (codevalue.trim().length() == 3)
				codevalue = "-1";
			else if (codevalue.indexOf("`") == -1 && codevalue.trim().length() > 0)
				codevalue = codevalue.substring(2);
		}
	}
    codevalue = URLEncoder.encode(codevalue);
	String codeitemdesc = "组织机构";
	String action = "";
	if (!userView.isSuper_admin() && codevalue != null && codevalue.trim().length() > 0)
	{
		if (model.equals("11"))
			action = "/performance/implement/performanceImplement.do?b_query=link&codeset=" + codeset + "&operate=init";
		else if (model.equals("12"))
			action = "/performance/evaluation/performanceEvaluation.do?b_query=link&codeset=" + codeset + "&operate=init";
		else if (model.equals("15")) //绩效评估统一打分
			action = "/performance/evaluation/performanceEvaluation.do?b_rate=link&codeset=" + codeset;
		else if (model.equals("16"))//考核计划
			action = "/performance/kh_plan/examPlanList.do?b_query=link&codeset=" + codeset;
		else if (model.equals("17"))//考核关系
			action = "/performance/options/kh_relation.do?b_queryObj=link&codeset=" + codeset;
		else if (model.equals("19"))//绩效自助 考核表分发
			action = "/selfservice/performance/performanceImplement.do?b_query=link&codeset=" + codeset;
		else if (model.equals("20"))//绩效自助 业绩数据录入 考核对象类型为人员
			action = "/performance/achivement/dataCollection/dataCollect.do?b_query2=link&codeset=" + codeset;	
		else if (model.equals("21"))//绩效自助 业绩数据录入 考核对象类型为单位
			action = "/performance/achivement/dataCollection/dataCollect.do?b_query2=link&codeset=" + codeset;	
		else if (model.equals("22"))//绩效自助 业绩数据录入 考核对象类型为部门 和 团队
			action = "/performance/achivement/dataCollection/dataCollect.do?b_query2=link&codeset=" + codeset;	
			
		action += "&code=" + codevalue + "&model=" + model;
	} else if (userView.isAdmin() && userView.getGroupId().equals("1"))
	{
	    if (model.equals("11"))
			action = "/performance/implement/performanceImplement.do?b_query=query&operate=init";
		else if (model.equals("12"))
			action = "/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init";
		else if (model.equals("15")) //绩效评估统一打分
			action = "/performance/evaluation/performanceEvaluation.do?b_rate=link";
		else if (model.equals("16"))//考核计划
			action = "/performance/kh_plan/examPlanList.do?b_query=link";
		else if (model.equals("17"))//考核关系
			action = "/performance/options/kh_relation.do?b_queryObj=link";
		else if (model.equals("19"))//绩效自助考核表分发
			action = "/selfservice/performance/performanceImplement.do?b_query=link";
		else if (model.equals("20")||model.equals("21")||model.equals("22"))//绩效自助 业绩数据录入
			action = "/performance/achivement/dataCollection/dataCollect.do?b_query2=link";
			
		if (operateType.equals("employ"))
			action += "&amp;operateType=employ";
	}

	String target = "mil_body";
	if (model.equals("11") || model.equals("17")) //绩效实施 考核关系
		target = "ril_body1";
%>


<HTML>
	<HEAD>
		<TITLE></TITLE>
		<hrms:themes />
        <meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
        <link href="/css/xtree.css" rel="stylesheet" type="text/css">
		<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
		<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
		<SCRIPT LANGUAGE=javascript>
</SCRIPT>
	</HEAD>
	<body topmargin="10" leftmargin="5" marginheight="0" marginwidth="0">
		<table width="100%" align="left" border="0" cellpadding="0"
			cellspacing="0" class="mainbackground" id="table_main_tree">
			<tr>
				<td valign="top">
					<div id="treemenu"></div>
				</td>

			</tr>
		</table>
	<BODY>
</HTML>
<SCRIPT LANGUAGE=javascript>
	var m_sXMLFile	= "/hire/demandPlan/positionDemand/position_demand_tree.jsp?flag=<%=flag%>&codeid=<%=codevalue%>&model=<%=model%>&init=1";
	var newwindow;
	var root=new xtreeItem("root","<%=codeitemdesc%>","<%=action%>","<%=target%>","<%=codeitemdesc%>","/images/root.gif",m_sXMLFile);
	
	root.setup(document.getElementById("treemenu"));	
	if(newwindow!=null)
	{
		newwindow.focus();
	}
	if(parent.parent.myNewBody!=null)
	{
		parent.parent.myNewBody.cols="*,0"
	}
	
	var isModel = <%=model%>;
	if(isModel==16)  //考核计划
	{		
		var khObject=root;
		khObject.expand();
		parent.mil_body.location="/performance/kh_plan/examPlanList.do?b_query=query&model=16&operateType=employ&spmodel=<%=spmodel%>";	
	}
<%//绩效自助 业绩数据录入
	if (model.equals("20")||model.equals("21")||model.equals("22"))	{	%>
		if(root.getFirstChild())
			root.getFirstChild().openURL();
	<%}%>
	//非ie浏览器高度调整下
	if(!getBrowseVersion()) {
		document.getElementById("table_main_tree").style.padding = "15px 0 0 0";
	}
</SCRIPT>
