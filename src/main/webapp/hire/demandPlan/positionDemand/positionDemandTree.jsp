<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.AdminCode" %>
<%@ page import="com.hjsj.hrms.interfaces.hire.OrganizationByXml"%>
<%@ page import="com.hrms.frame.utility.CodeItem" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.hrms.frame.utility.AdminDb,com.hjsj.hrms.utils.PubFunc,
	com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter
	,com.hjsj.hrms.utils.ResourceFactory" %>
 
<%
	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String operateType="user";
	if(request.getParameter("operateType")!=null)
		operateType=request.getParameter("operateType");
	//System.out.println(operateType);
	if(userView != null)
	{
		css_url=userView.getCssurl();
		if(css_url==null||css_url.equals(""))
		css_url="/css/css1.css";
	}
	String model=request.getParameter("model");    // 11:绩效实施
	String plan_id=request.getParameter("plan_id");  
	String flag=request.getParameter("flag");
	String codeset=userView.getManagePrivCode();
	String codevalue=userView.getManagePrivCodeValue();
  
	if(userView.isSuper_admin()||userView.getGroupId().equals("1"))
	{
		codevalue="-1";
	}
	else
	{
		if(model.equals("18")||model.equals("10")){
			String tmp = userView.getUnitIdByBusi("6");
			tmp = PubFunc.getHighOrgDept(tmp.replaceAll("`",","));
			codevalue = tmp.replaceAll(",","`");
			if(tmp.trim().length()==3)
			{
			   codevalue="-1";
			}
			else if(codevalue.indexOf("`")==-1&&codevalue.trim().length()>2){
				codeset=codevalue.substring(0,2);
				codevalue=codevalue.substring(2);
			}
		}
		/*else if(model.equals("10") ||userView.getStatus()==4)
		{
			codevalue=userView.getManagePrivCodeValue();
			codeset=userView.getManagePrivCode();
			if((codeset==null||codeset.trim().length()==0)&&(codevalue==null||codevalue.trim().length()==0))
			{
					codevalue="";
			}
			else if(codeset.length()!=0&&(codevalue==null||codevalue.trim().length()==0))
					codevalue="-1";
		}*/
		else{
			if(model.equals("1")||model.equals("2")||model.equals("3")||model.equals("16")||model.equals("5")||model.equals("6")||model.equals("7")||model.equals("8")){// 1.需求报批，2.需求审批,3.审核查询,16.招聘计划,7.面试考核，8.员工录用
				String tmp = userView.getUnitIdByBusi("7");
				tmp = PubFunc.getHighOrgDept(tmp.replaceAll("`",","));
				codevalue = tmp.replaceAll(",","`");
				if(tmp.trim().length()==3)
				{
				   codevalue="-1";
				}
				else if(codevalue.indexOf("`")==-1&&codevalue.trim().length()>2){
					codeset=codevalue.substring(0,2);
					codevalue=codevalue.substring(2);
				}
			}else{
				
				if(userView.getUnit_id()==null||userView.getUnit_id().equals("")||userView.getUnit_id().equalsIgnoreCase("UN"))
				{
		   		  //操作单位没有设置 走管理范围 
		   		  if(model.equals("14") || model.equals("11")||model.equals("12") || model.equals("15") || model.equals("16") || model.equals("17"))
		   		  {
		   		 		if(codeset.equals("") && codevalue.equals(""))//管理范围没有设置 走用户所在的部门和单位
		   		 		{
		   		 			String userDeptId=userView.getUserDeptId();
							String userOrgId=userView.getUserOrgId();					
							if(userDeptId!=null&&!userDeptId.equalsIgnoreCase("null")&&userDeptId.trim().length()>0)
							{
								codevalue=userDeptId;
								codeset="UM";					
							}
							else if(userOrgId!=null&&userOrgId.trim().length()>0)
							{
								codevalue=userOrgId;
								codeset="UN";						
							}
		   		 		}else
		   		 			 codevalue=codeset+codevalue+"`";
		   		  }		   		
		   		   else
		   		  	 codevalue="";
				}else{
					codevalue=userView.getUnit_id(); //.substring(2);
					if(codevalue.trim().length()==3)
					{
					   codevalue="-1";
					}
					else if(codevalue.indexOf("`")==-1&&codevalue.trim().length()>0)
						codevalue=codevalue.substring(2);
				}		
			}
		}
	}
	
   		String	codeitemdesc="";
   		Connection conn=null;
   		String action="";
   		String target="mil_body";
 	try
 	{
		conn=AdminDb.getConnection();
		Sys_Oth_Parameter sysparam=new Sys_Oth_Parameter(conn);
		codeitemdesc=sysparam.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
		if(codeitemdesc==null||codeitemdesc.equals(""))
			codeitemdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
	
		OrganizationByXml organizationByXml = new OrganizationByXml();	

		if(!userView.isSuper_admin()&&codevalue!=null&&codevalue.trim().length()>0)
		{
			if(model.equals("1"))
				action="/hire/demandPlan/positionDemand/positionDemandTree.do?b_query=link";
			else if(model.equals("2"))
				action="/hire/demandPlan/positionDemand/positionDemandTree.do?b_query2=link";
			else if(model.equals("3"))
				action="/hire/demandPlan/positionDemand/positionDemandTree.do?b_query3=link";
			else if(model.equals("4"))	//人员筛选
				action="/hire/employActualize/personnelFilter/personnelFilterTree.do?b_query=link&operate=init";
			else if(model.equals("5")||model.equals("6"))	//面试安排 || 面试通知
				action="/hire/interviewEvaluating/interviewArrange.do?b_query=link&operate=init&code=-1";
			else if(model.equals("7"))   //面试考核
				action="/hire/interviewEvaluating/interviewExamine.do?b_query=link&operate=init&code=-1";
			else if(model.equals("8"))   //员工录用
				action="/hire/employSummarise/personnelEmploy.do?b_query=link&operate=init";
			else if(model.equals("10"))   //培训计划审核
				action="/train/plan/searchCreatPlanList.do?b_query=link&operate=init";
			else if(model.equals("13"))   //招聘订单
				action="/hire/demandPlan/hireOrder.do?b_query=1";
			else if(model.equals("14"))   //奖金管理
				action="/gz/bonus/inform.do?b_query=1";	
			else if(model.equals("11"))
					action="/performance/implement/performanceImplement.do?b_query=link&codeset="+codeset+"&operate=init";
			else if(model.equals("12"))
					action="/performance/evaluation/performanceEvaluation.do?b_query=link&codeset="+codeset+"&operate=init";
			else if(model.equals("15"))	//绩效评估统一打分
					action="/performance/evaluation/performanceEvaluation.do?b_rate=link&codeset="+codeset;					
			else if(model.equals("16"))//考核计划
					action="/performance/kh_plan/examPlanList.do?b_query=link&codeset="+codeset;		
			else if(model.equals("17"))//考核关系
					action="/performance/options/kh_relation.do?b_queryObj=link&codeset="+codeset;	
			else if(model.equals("18"))//培训自助计划审核
					action="/train/plan/searchCreatPlanList.do?b_query=link&operate=init&model=2&codeset="+codeset;	
							
			action+="&code=" + codevalue+"&model="+model;
		
			if(model.equals("10"))
				action=action.replaceAll("model=10","model=2");
		}
		else if(userView.isAdmin()&&userView.getGroupId().equals("1"))
		{
			if(model.equals("1"))
			{
				action="/hire/demandPlan/positionDemand/positionDemandTree.do?b_query=1";	
			}
			else if(model.equals("2"))
			{
				action="/hire/demandPlan/positionDemand/positionDemandTree.do?b_query2=2";
			}
			else if(model.equals("3"))
			{
				action="/hire/demandPlan/positionDemand/positionDemandTree.do?b_query3=3";
			}
			else if(model.equals("5")||model.equals("6"))	//面试安排 || 面试通知
				action="/hire/interviewEvaluating/interviewArrange.do?b_query=0&code=-1";
			else if(model.equals("7"))   //面试考核	
				action="/hire/interviewEvaluating/interviewExamine.do?b_query=0&operate=init&code=-1";
			else if(model.equals("8"))   //员工录用
				action="/hire/employSummarise/personnelEmploy.do?b_query=0&operate=init";
			else if(model.equals("13"))   //招聘订单
				action="/hire/demandPlan/hireOrder.do?b_query=1";	
			else if(model.equals("14"))   //奖金管理
				action="/gz/bonus/inform.do?b_query=1";	
			else if(model.equals("11"))
					action="/performance/implement/performanceImplement.do?b_query=query&operate=init";
			else if(model.equals("12"))
					action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init0";
			else if(model.equals("15"))	//绩效评估统一打分
					action="/performance/evaluation/performanceEvaluation.do?b_rate=link";				
			else if(model.equals("16"))//考核计划
					action="/performance/kh_plan/examPlanList.do?b_query=link";		
			else if(model.equals("17"))//考核关系
					action="/performance/options/kh_relation.do?b_queryObj=link";				
			else if(model.equals("18"))//培训自助计划审核
					action="/train/plan/searchCreatPlanList.do?b_query=link&operate=init&model=2";	
						
			if(operateType.equals("employ"))
					action+="&amp;operateType=employ";
		}
		
		int keyIndex = action.indexOf("&");
	    if (keyIndex>-1){
	        action = action.substring(0,keyIndex) + "&encryptParam=" + PubFunc.encrypt(action.substring(keyIndex+1));
	    }
		
	
		if(model.equals("10")&&!codevalue.equals("-1"))
		{
			CodeItem item=AdminCode.getCode(codeset,codevalue);
			codeitemdesc=item.getCodename();
		}
		
		
	
		if(model.equals("11") || model.equals("17"))   //绩效实施 考核关系
				target="ril_body1";
	}catch(Exception ex)
	{
		ex.printStackTrace();
	}finally{
		try
		{
			if(conn!=null)
				conn.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
%>


<HTML>
<HEAD>
	<TITLE>
	</TITLE>
	<link href="<%=css_url%>" rel="stylesheet" type="text/css">
	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
	<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
	<SCRIPT LANGUAGE=javascript>
</SCRIPT>     
</HEAD>
<hrms:themes></hrms:themes>
<body   topmargin="0" leftmargin="5" marginheight="0" marginwidth="0">
	<table width="100%" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
	<tr>  
		<td valign="top">
			<div id="treemenu"></div>
		</td>
	 
	</tr>
</table>	

<BODY>
</HTML>
<SCRIPT LANGUAGE=javascript>
	var m_sXMLFile	= "position_demand_tree.jsp?flag=<%=flag%>&codeid=<%=codevalue%>&model=<%=model%>&init=1";		
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

</SCRIPT>
