<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.businessobject.report.tt_organization.TTorganization"%>
<%@ page import="com.hrms.frame.dao.RecordVo"%>
<%@ page import="java.util.*,java.text.SimpleDateFormat,
				 com.hjsj.hrms.actionform.report.actuarial_report.ActuarialCollectReportForm,
				 com.hjsj.hrms.utils.PubFunc" %>
<%

	

	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
		css_url=userView.getCssurl();
		if(css_url==null||css_url.equals(""))
		css_url="/css/css1.css";
	}
	TTorganization ttorganization=new TTorganization();
	RecordVo selfVo=ttorganization.getSelfUnit3(userView.getUserName());
	//update by wangchaoqun on 2014-9-24 加密sql语句
	String params=PubFunc.encrypt("where parentid='"+selfVo.getString("unitcode")+"' and parentid!=unitcode");
	
	String type="";
	if(request.getParameter("type")!=null)
		type=request.getParameter("type");	
	if(type.equals("pigeonhole"))
		params+="&isAction=0";
	else if(type.equals("actuarial"))
	{
		params+="&isAction=2";
		String cycle_id="";
		if(request.getParameter("cycle_id")!=null)
		{
			cycle_id=request.getParameter("cycle_id");
		}
		else
		{
			ActuarialCollectReportForm actuarialCollectReportForm=(ActuarialCollectReportForm)session.getAttribute("actuarialCollectReportForm"); 
			cycle_id=actuarialCollectReportForm.getCycle_id();
		}
		params+="&cycle_id="+cycle_id;
	}	
	else if(type.equals("batchprint"))
		params+="&isAction=3";
	else
		params+="&isAction=1";
%>


<HTML>
<HEAD>
	<TITLE>
	</TITLE>
	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
	<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>  
	<link rel="stylesheet" href="/css/css1.css" type="text/css">   
	<hrms:themes />
	<SCRIPT LANGUAGE=javascript>
	
	function sub()
	{
		if(root.getSelected().length==0)
		{
			alert(SELECTCOLLECTUN+"！");
			return;
		}
		//兼容谷歌、ie wanbgs 20190318
		if(getBrowseVersion()){
            if(parent.Ext){
                if(parent.Ext.getCmp("selectArchiveUnit")){
                    parent.Ext.getCmp("selectArchiveUnit").info = root.getSelected();
                    parent.Ext.getCmp("selectArchiveUnit").close();
                }else{
                    returnValue=root.getSelected();
                    window.close();
				}
            }else{
                returnValue=root.getSelected();
                window.close();
            }
        }else{
		    if(parent.Ext){
                if(parent.Ext.getCmp("selectArchiveUnit")){
                    parent.Ext.getCmp("selectArchiveUnit").info = root.getSelected();
                    parent.Ext.getCmp("selectArchiveUnit").close();
				}
			}
		}
	}
	
</SCRIPT>     
</HEAD>
<style> 
<!--
	body {
	margin-left: 8px;
	margin-top: 0px;
	margin-right: 10px;
	margin-bottom: 0px;
	}
-->
div#treemenu {
 margin-left:0px;
 margin-top: 0px;
 margin-right: 0px;
 margin-bottom: 0px;
 width: 100%;
 height: 250px;
 overflow: auto;
}
.mainbackground{
	margin-top:10px;
	margin-left:-3px;
	width:expression(document.body.clientWidth-10);
}
</style>
<body>
	<table  width="95%" align="center" border="0" cellpadding="0" cellspacing="0" class="mainbackground"  >
	
	<tr align="center">  
		<td valign="middle" width="100%" align="center">
	<table align="center" width='100%' border="0" cellpadding="0" cellspacing="0">
	<tr>
	<td width="100%" align="center" class="listtable">
		<fieldset  style="width:100%;">
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
			<td>
			<div id="treemenu"></div>
			</td>
			</tr>
		
			</table>
			</fieldset>
		
			</td>
			</tr>
					<%  
	 if(type.equals("pigeonhole")){ %>
		
		<tr><td valign="top" align='center' style="padding-top: 5px">
			<Input type='button' value='<bean:message key="reporttypelist.confirm"/>'  class="mybutton" onclick="sub()" />
		</td></tr>
		<% } %>
			</table>
		</td>
	</tr>	
	
	
		
</table>	

<BODY>
</HTML>


<SCRIPT LANGUAGE=javascript>
	
	var m_sXMLFile= "report_org_collect_tree.jsp?params=<%=params%>";		
	var newwindow;
	
	<%   if(type.equals("pigeonhole")) 
		 {
	%>
			var root=new xtreeItem("<%=(selfVo.getString("unitcode"))%>","<%=(selfVo.getString("unitname"))%>","","",REPORT_UNIT,"/images/unit.gif",m_sXMLFile);	
			Global.defaultInput=1;   
			Global.showroot=true;   
	<%	
		 }
		 else  if(type.equals("actuarial"))
		 {
	%>	 
		var root=new xtreeItem("<%=(selfVo.getString("unitcode"))%>","<%=(selfVo.getString("unitname"))%>","/report/actuarial_report/report_collect.do?b_query=link&a_code=<%=(selfVo.getString("unitcode"))%>","mil_body",REPORT_UNIT,"/images/unit.gif",m_sXMLFile);	
		root.select();
	
	<% 
		 }
		  else  if(type.equals("batchprint"))
		 {
	%>	 
		var root=new xtreeItem("<%=(selfVo.getString("unitcode"))%>","<%=(selfVo.getString("unitname"))%>","","mil_body",REPORT_UNIT,"/images/unit.gif",m_sXMLFile);	
		root.select();
		Global.defaultInput=1;
		Global.showroot=true;
	
	<% 
		 }
		 else
		 {
	 %>
	 		var root=new xtreeItem("<%=(selfVo.getString("unitcode"))%>","<%=(selfVo.getString("unitname"))%>","/report/report_collect/reportOrgCollecttree.do?b_query=link&a_code=<%=(selfVo.getString("unitcode"))%>&operateObject=2","mil_body",REPORT_UNIT,"/images/unit.gif",m_sXMLFile);	
	 <% } %>
	
	//Global.showroot=false;
	
	
	root.setup(document.getElementById("treemenu"));

	if(newwindow!=null)
	{
		newwindow.focus();
	}
	if(parent.parent.myNewBody!=null)
	{
		parent.parent.myNewBody.cols="*,0"
	}

  //  root.expandAll();
	function expland()
	{
		root.expandAll();
	}
</SCRIPT>
<script>
	//报表归档-归档单位弹窗 ie11非兼容模式样式修改  wangbs 20190319
	if(getBrowseVersion()==10){
        var targetTable = document.getElementsByTagName("table")[0];
        targetTable.style.marginLeft = "-20px";
	}
</script>