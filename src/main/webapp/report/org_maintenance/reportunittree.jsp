<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.businessobject.report.tt_organization.TTorganization"%>
<%@ page import="com.hrms.frame.dao.RecordVo,
com.hjsj.hrms.actionform.report.org_maintenance.SearchReportUnitForm,
                 com.hjsj.hrms.utils.ResourceFactory,com.hjsj.hrms.utils.PubFunc"%>

<%
	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
		css_url=userView.getCssurl();
		if(css_url==null||css_url.equals(""))
		css_url="/css/css1.css";
	}
	String bosflag = "";
	if(userView != null)
	{
	  bosflag = userView.getBosflag();
	}	
	TTorganization ttorganization=new TTorganization();
	RecordVo selfVo=ttorganization.getSelfUnit3(userView.getUserName());
	String uc = selfVo.getString("unitcode");
	String backdate ="";

	if((!userView.isSuper_admin()&& uc == null)||(!userView.isSuper_admin() && uc.equals("")) ){
%>
<HTML>
<HEAD>

</HEAD>
</HTML>
<%
}else{
    backdate="";
	SearchReportUnitForm searchReportUnitForm=(SearchReportUnitForm)session.getAttribute("searchReportUnitForm");
	if(searchReportUnitForm!=null){
	 backdate = searchReportUnitForm.getBackdate();
	searchReportUnitForm.getFormHM().put("backdate",backdate);
	
	}
	
	String params=PubFunc.encrypt("where parentid='"+selfVo.getString("unitcode")+"' and unitcode <> parentid ");
	String action = "/report/org_maintenance/reportunitlist.do?b_query=link&code="+uc;
	String nodehead = selfVo.getString("unitname");
	if(userView.isSuper_admin()){
		nodehead=ResourceFactory.getProperty("report.appealUnit");
		params=PubFunc.encrypt("where parentid = unitcode");  //update by wangchaoqun on 2014-9-24
		action="/report/org_maintenance/reportunitlist.do?b_query=link";
	}
%>
		
<HTML>
<HEAD>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
	<hrms:themes/> 
	<script language="JavaScript"src="../../../module/utils/js/template.js"></script>
	<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript>
	function add(uid,text,action,target,title,imgurl,xml)
	{
		var currnode=Global.selectedItem;
		var tmp = new xtreeItem(uid,text,action,target,title,imgurl,xml);
		currnode.add(tmp);
	}
	var openerConfig = ["_blank"];
	function backDate(){
		var theurl = '/report/org_maintenance/reportunittree.do?br_backdate=link';
		var iTop = (window.screen.height-30-305)/2; //获得窗口的垂直位置;
		var iLeft = (window.screen.width-10-470)/2;  //获得窗口的水平位置;
		window.open(theurl,'_blank','height=360, width=470,top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no');
	}
		
	function dateReturn(backdate){
		if(backdate&&backdate.length>9) {
			searchReportUnitForm.action="/report/org_maintenance/reportunittree.do?b_query2=link&backdate="+backdate;
			searchReportUnitForm.target="il_body"
			searchReportUnitForm.submit();
		}else
			return false; 
	}
	function setAnalyseCond(){
			var codeFlag = '${searchReportUnitForm.codeFlag}';
			var backdate = '${searchReportUnitForm.backdate}';
			var temp ="&analysereportflag=1&analysereportinitflag=1";
			if(codeFlag!=null&&codeFlag.length>0){
			if(backdate==null||backdate.length==0)
			backdate="";
			temp+="&code="+codeFlag+"&backdate=";
			}
			searchReportUnitForm.action="/report/org_maintenance/reportunitlist.do?b_query=link"+temp;
			searchReportUnitForm.target="mil_body";
			searchReportUnitForm.submit();
	}
	function openwin(url)
	{
	   window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
	}
</SCRIPT>
  <style>
	<!-- 
	body {
		margin-left: 0px;
		margin-top: 0px;
		margin-right: 0px;
		margin-bottom: 0px;
	}
    a{
       margin-right:5px;
    }
	-->
</style>   
</HEAD>
<body>
<html:form action="/report/org_maintenance/reportunittree"> 
	<table border="0" cellpadding="0" cellspacing="0" class="mainbackground" style="width:100%;">
		<tr style="padding-left: 0px;">
			<td >
                 <table border="0" cellpadding="0" cellspacing="0" width="100%">
                  <tr valign="middle" class="toolbar" style="padding-left: 10px;height: 38px;line-height: 38px;">
                   <td width="100%" style="padding-left: 4px;">
					  <INPUT type="hidden" id="flg">
					  <a href="###" onclick="return backDate();" ><img src="/images/quick_query.gif" title="历史时点查询" border="0" align="absmiddle"></a>               
                  	  <hrms:priv func_id="2905008">
                    	<a href="###" onclick="return setAnalyseCond();"><img src="/images/45.bmp" title="报表分析用表的范围定义" border="0" align="absmiddle"></a>    
                      </hrms:priv>
                   </td>
                  </tr>
                 </table>
			</td>
		</tr>
	<tr>  
		<td valign="top" >
            <%if(bosflag.equalsIgnoreCase("hcm")){%>
    			<div id="treemenu" style="overflow: auto; height: expression(document.body.clientHeight-37);"></div>
    		<%}else{ %>
                <div id="treemenu" style="overflow: auto; height: expression(document.body.clientHeight-24);"></div>
    		<%} %>
		</td>
	 
	</tr>
</table>	

</html:form>
<BODY>
</HTML>		
<SCRIPT LANGUAGE=javascript>
	var m_sXMLFile= "report_unit_tree.jsp?params=<%=params%>&backdate=<%=backdate%>";	
	var newwindow;
	var root=new xtreeItem("root","<%=nodehead%>","<%=action%>","mil_body",REPORT_UNIT,"/images/unit.gif",m_sXMLFile);
	root.setup(document.getElementById("treemenu"));
	if(newwindow!=null)
		newwindow.focus();
	
	if(parent.parent.myNewBody!=null)
		parent.parent.myNewBody.cols="*,0"
			
</SCRIPT>
		
<%}%>

