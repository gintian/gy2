<%@ page contentType="text/html; charset=UTF-8"%>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
</head>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.businessobject.report.tt_organization.TTorganization"%>
<%@ page import="com.hrms.frame.dao.RecordVo,
				 com.hjsj.hrms.utils.ResourceFactory,
				 com.hjsj.hrms.utils.PubFunc"%>

<%
	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
		css_url=userView.getCssurl();
		if(css_url==null||css_url.equals(""))
		css_url="/css/css1.css";
	}
	//获取当前用户对应的填报单位编码，生成SQL语句的where部分
	TTorganization ttorganization=new TTorganization();
	RecordVo selfVo=ttorganization.getSelfUnit3(userView.getUserName());
	String uc = selfVo.getString("unitcode");
	//System.out.println("uc = " + uc);
	//不是超级用户且无对应的填报单位
	if((!userView.isSuper_admin()&& uc == null)||(!userView.isSuper_admin() && uc.equals("")) ){
%>
<HTML>
	<HEAD>
		<link href="<%=css_url%>" rel="stylesheet" type="text/css">
	</HEAD>
</HTML>
<%
}else{ 
	session.removeAttribute("dmltab");
	String params=PubFunc.encrypt("where parentid='"+selfVo.getString("unitcode")+"' and unitcode <> parentid ");
	String action = "/report/report_state/reportstatepanel.do?b_query=link&ucode="+uc;
	String nodehead = selfVo.getString("unitname");
	if(userView.isSuper_admin()){
		nodehead=ResourceFactory.getProperty("report.appealUnit");
		params=PubFunc.encrypt("where parentid = unitcode");  //update by wangchaoqun on 2014-9-24
		action="/report/report_state/reportstatepanel.do?b_query=link";
	}
%>
<script type="text/javascript">
	function refresh1(){
				 var currnode,base_id,target_url;
				   currnode=Global.selectedItem;
				   if(currnode==null)
    	    			return;
    	    	base_id=currnode.uid;
    	    	if(base_id=='root'){
    	    		return;
    	    	}else{
    	    		var parent =currnode.parent;
    	    		parent.clearChildren();
    	    		parent.loadChildren();
					parent.expand();
					for(var j=0;j<parent.childNodes.length;j++){
						var obj=parent.childNodes[j];
						if(obj.uid==base_id){
							var obj=parent.childNodes[j];
						  	selectedClass("treeItem-text-"+obj.id);
	     				 	 obj.select();
								break;
						}
					}
    	    	}
			}
</script>
<HTML>
	<HEAD>
		<link href="<%=css_url%>" rel="stylesheet" type="text/css">
		<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>
		<link href="/css/xtree.css" rel="stylesheet" type="text/css">
		<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
		<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
	</HEAD>
	<hrms:themes />
	<body topmargin="0" leftmargin="5" marginheight="0" marginwidth="0">
		<div id="treemenu" style="width: 98%;padding-left: 10px; height:100%"></div>
		<!-- <table width="100%" height="100%" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
			<tr>
				<td valign="top">
    				<div id="treemenu" style="overflow: no; width: 100%;padding-left: 10px; height:100%"></div>
				</td>
			</tr>
		</table> -->
	</body>
</HTML>


<SCRIPT LANGUAGE=javascript>

	var m_sXMLFile= "report_unit_tree.jsp?params=<%=params%>";	

	var newwindow;
	var root=new xtreeItem("root","<%=nodehead%>","<%=action%>","mil_body",REPORT_UNIT,"/images/unit.gif",m_sXMLFile);

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

<%}%>









