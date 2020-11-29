<%@ page contentType="text/html; charset=UTF-8"%>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
</head>
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
	
	String bosflag = "";
	if(userView != null)
	{
	  bosflag = userView.getBosflag();
	}	
    
	//add by wangchaoqun on 2014-9-26 begin
	String encryptParam = PubFunc.encrypt("a_code=" + selfVo.getString("unitcode") + "&operateObject=2");
	String encryptParam1 = PubFunc.encrypt("a_code=" + selfVo.getString("unitcode"));
	//add by wangchaoqun on 2014-9-26 end
%>


<html>
<head>
	<title>
	</title>
	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
	<hrms:themes />
	<style> 
		body {
		margin-left: none;
		margin-top: none;
		margin-right: none;
		margin-bottom: none;
		}
		input:focus {
    		outline:0;
		}
	</style>
	<script language=javascript src="/js/constant.js"></SCRIPT>
	<script language=javascript src="/js/xtree.js"></SCRIPT>
	<script language=javascript src="/js/validate.js"></SCRIPT>  
	<script language=javascript>
	
	function sub()
	{
		if(root.getSelected().length==0)
		{
			alert(SELECTCOLLECTUN+"！");
			return;
		}
		returnValue=root.getSelected();
		window.close();	
	}
	
</script>     
</head>
<body onresize="resize();" onload="resize()" style="margin:0px">
	<table id="org_treeID"  width="500" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground" style="width:expression(document.body.clientWidth);"  padding-left="0">
	<% if(!type.equals("actuarial")){ %>
	<tr style="padding-left: 0px;width:100%">
		<td style="width:100%"> 
			<table id="tabID" border="0" cellpadding="0" cellspacing="0" width="100%">
              <tr id="trID" class="toolbar" style="padding-left: 10px;height: 40px;">
                 <td  width="100%">
              		<input type="image" name="b_indexf" style="padding-left: 10px;" src="/images/add_all.gif" title="<bean:message key="report_collect.info1"/>" onclick="expland()">
				</td>
			  </tr>
			</table>
		</td>
	</tr>
	<% } %>
	
	<tr>  
		<td valign="top" >
            <%if(bosflag.equalsIgnoreCase("hcm")){%>
    			<div id="treemenu" style="overflow: auto; width: 2000;padding-left: 10px; height: expression(document.body.clientHeight-44);"></div>
    		<%}else{ %>
                <div id="treemenu" style="overflow: auto; width: 2000;padding-left: 10px; height: expression(document.body.clientHeight-24);"></div>
    		<%} %>
		</td>
	</tr>	
	
	<%  
	 if(type.equals("pigeonhole")){ %>
		
		<tr><td valign="top" align='left' ><Br>
			<Input type='button' value='<bean:message key="reporttypelist.confirm"/>'  class="mybutton" onclick="sub()" />
		</td></tr>
		<% } %>
		
</table>	

</body>
</html>


<script language=javascript>
	if(parent.document.getElementById("center_iframeb")||parent.document.getElementById("center_iframe")){//bug  50843
		document.getElementById("org_treeID").style.width="100%"
		document.getElementById("org_treeID").width="100%"
		document.getElementById("treemenu").style.width="100%"
		document.getElementById("treemenu").style.height="100%"	 
		document.getElementById("treemenu").style.paddingLeft="0"	 
		document.getElementById("treemenu").overflow='no'	
	}
function resize(){
	if(document.getElementById("tabID"))
		  document.getElementById("tabID").style.width='100%';
	  if(document.getElementById("trID"))
		  document.getElementById("trID").style.width='100%';
}
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
		var root=new xtreeItem("<%=(selfVo.getString("unitcode"))%>","<%=(selfVo.getString("unitname"))%>","/report/actuarial_report/report_collect.do?b_query=link&encryptParam=<%=encryptParam1 %>","mil_body",REPORT_UNIT,"/images/unit.gif",m_sXMLFile);	
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
	 		var root=new xtreeItem("<%=(selfVo.getString("unitcode"))%>","<%=(selfVo.getString("unitname"))%>","/report/report_collect/reportOrgCollecttree.do?b_query=link&encryptParam=<%=encryptParam %>","mil_body",REPORT_UNIT,"/images/unit.gif",m_sXMLFile);	
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
	inittreenode();
	function inittreenode(){
		 var obj=root;
		  if(obj){
		  		selectedClass("treeItem-text-"+obj.id);
		  		//href="/report/report_collect/reportOrgCollecttree.do?b_query=link&a_code="+obj.uid+"&operateObject=2";
		  		//parent.parent.mil_body.location=href;
		  }
	
	}
</script>