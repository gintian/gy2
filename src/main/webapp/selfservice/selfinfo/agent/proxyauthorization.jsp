<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.struts.constant.SystemConfig,com.hrms.hjsj.sys.VersionControl,com.hjsj.hrms.utils.ResourceFactory,com.hjsj.hrms.actionform.selfinfomation.AgentForm,com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hjsj.hrms.actionform.sys.options.otherparam.SysOthParamForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<hrms:themes />
<body oncontextmenu=return(onloadMenu())   onload="toNext()">
<html:form action="/selfservice/selfinfo/agent/proxyauthorization">
<%!
	String menuname="tab1";
	
 %>
<%
    AgentForm agentForm=(AgentForm)session.getAttribute("agentForm");
    String id = agentForm.getId();
    String role_id = agentForm.getRole_id();
    String user_flag = agentForm.getUser_flag();
	UserView userView = (UserView)session.getAttribute("userView");
	boolean bSuperAdmin=false;
	bSuperAdmin=userView.isSuper_admin()&&(!userView.isBThreeUser());	
	StringBuffer tabstr= new StringBuffer();	
	tabstr.append("<table id=\"tableShow\" width=\"130\" ");
	tabstr.append("cellpadding=\"0\"  cellspacing=\"0\" border=\"0\">");
	VersionControl ver_ctrl=new VersionControl();
%>
<hrms:tabset name="pageset" width="100%" height="480" type="true"> 
<hrms:tab name="tab1" label="功能授权" function_id="" visible="true" url="/selfservice/selfinfo/agent/purviewagent.do?b_query=link&operate=1">
</hrms:tab>
<%
	tabstr.append("<tr height=\"20\" id=\"tab1_1\" onclick=\"itemHref('");
	tabstr.append("tab1");
	tabstr.append("',this);\" onMouseover=\"mover(this,'tab1');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab1');\">");
	tabstr.append("</tr>");
%>
<hrms:tab name="tab2" label="业务授权" function_id="" visible="true" url="/selfservice/selfinfo/agent/purviewagent.do?b_query=link&operate=2">
</hrms:tab>
<%
	tabstr.append("<tr height=\"20\" id=\"tab2_1\" onclick=\"itemHref('");
	tabstr.append("tab2");
	tabstr.append("',this);\" onMouseover=\"mover(this,'tab2');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab2');\">");
	tabstr.append("</tr>");
%>
</hrms:tabset>
<div id="mlay" style="position:absolute;overflow:auto;height:150px;width:160px;">
		<%=tabstr.toString() %>
</div>
<table  width="100%" align="center">
          <tr>
            <td align="left">
	 		  <input type="button"  value="返回" onclick="window.location.replace('/selfservice/selfinfo/agent/agentinfo.do?b_search=link');" class="mybutton">
            </td>
          </tr>  
</table>
</html:form>
<script language="javascript">
//菜单没有选中的背景色和文字色 
var bgc="#FFFFFF",txc="black";
//菜单选中的选项背景色和文字色
var cbgc="#FFF8D2",ctxc="black";
var menutable = "tab1_1";

function mover(obj,menuname){
	var tabid = menuname+"_1";
	if(menutable!=tabid){
		obj.style.background=cbgc;
		obj.style.color=ctxc ;
	}
}
function mout(obj,menuname){
	var tabid = menuname+"_1";
	if(menutable!=tabid){
		obj.style.background=bgc;
		obj.style.color=txc ;
	}
}
function showoff() { 
	mlay.style.display="none"; 
} 
function onloadMenu(){
	var top=event.clientY;
	if(top>30)
		return false;
	mlay.style.display=""; 
	mlay.style.pixelTop=top; 
	mlay.style.pixelLeft=event.clientX; 
	mlay.style.background=bgc; 
	mlay.style.color=txc; 
 	return false;
}
function toNext(){
	var tabid="<%=menuname%>";
	if(tabid!=null&&tabid.length>0){
		var tab=$('pageset');
		tab.setSelectedTab("<%=menuname%>");
		menutable = tabid+"_1";
		var obj = document.getElementById(menutable);
		if(obj!=null){
			obj.style.background=cbgc;
			obj.style.color=ctxc ;
		}
	}
}
function itemHref(menuname,obj){
	if(menuname!=null&&menuname.length>0){
		var tab=$('pageset');
		tab.setSelectedTab(menuname);
		var obj1 = document.getElementById(menutable);
		obj1.style.background=bgc;
		obj1.style.color=txc ;
		
		obj.style.background=cbgc;
		obj.style.color=ctxc ;
		
		menutable = menuname+"_1";
	}
}
function selectHref(){
	var tab=$('pageset');
	var seltab = tab.getSelectedTab();
	var menuname = seltab.tabName;
	
	if(menuname!=null&&menuname.length>0){
		var obj1 = document.getElementById(menutable);
		if(!obj1)
			return false;
		obj1.style.background=bgc;
		obj1.style.color=txc ;

		var obj = document.getElementById(menuname+"_1");
		if(!obj)
			return false;
		obj.style.background=cbgc;
		obj.style.color=ctxc ;
		
		menutable = menuname+"_1";
	}else{
		var obj = document.getElementById("tab1_1");
		if(!obj)
			return false;
		obj.style.background=cbgc;
		obj.style.color=ctxc ;
	}
}
</script>
</body>

