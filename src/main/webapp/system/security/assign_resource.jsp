<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.struts.constant.SystemConfig,com.hrms.hjsj.sys.VersionControl,com.hjsj.hrms.utils.ResourceFactory,com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="com.hjsj.hrms.actionform.sys.ResourceForm" %>
<%@ page import="com.hjsj.hrms.utils.PubFunc" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<head>
	<style type="text/css"  >

	</style>
</head>
<body oncontextmenu=return(onloadMenu()) onclick="showoff();" onload="toNext()">
<html:form action="/system/security/assign_resource" style="margin:10px 0 0 -2px;">
<%!
	String menuname="tab1";
	
 %>
<%
	UserView userView = (UserView)session.getAttribute("userView");
	boolean bSuperAdmin=false;
	bSuperAdmin=userView.isSuper_admin()&&(!userView.isBThreeUser());	
	StringBuffer tabstr= new StringBuffer();	
	tabstr.append("<table id=\"tableShow\" width=\"130\" ");
	tabstr.append("cellpadding=\"0\"  cellspacing=\"0\" border=\"0\">");
	int i=0;
	VersionControl ver_ctrl=new VersionControl();
	ResourceForm resourceForm= (ResourceForm) session.getAttribute("resourceForm");


	String role_name="3".equals(resourceForm.getFromflag())? PubFunc.decrypt(resourceForm.getRole_name()):resourceForm.getRole_name();

	String user_flag= resourceForm.getFlag();
	String userFlagText="1".equals(user_flag)?"操作角色：":"操作用户：";
	EncryptLockClient lock = (EncryptLockClient)session.getServletContext().getAttribute("lock");
	
	//系统管理，资源分配，URL参数加密  jingq add 2014.09.23
	String tjbstr = com.hjsj.hrms.utils.PubFunc.encrypt("type=11&res_flag=1&sorttype=1");
	String cardstr = com.hjsj.hrms.utils.PubFunc.encrypt("type=10&res_flag=0&sorttype=1");
	String musterstr = com.hjsj.hrms.utils.PubFunc.encrypt("type=14&res_flag=4&sorttype=1");
	String hmusterstr = com.hjsj.hrms.utils.PubFunc.encrypt("type=15&res_flag=5&sorttype=1");
	String staticstr = com.hjsj.hrms.utils.PubFunc.encrypt("res_flag=3");
	String querystr =  com.hjsj.hrms.utils.PubFunc.encrypt("res_flag=2");
	String rulestr = com.hjsj.hrms.utils.PubFunc.encrypt("res_flag=6");
	String rsbdstr = com.hjsj.hrms.utils.PubFunc.encrypt("type=1&res_flag=7");
	String gzsetstr = com.hjsj.hrms.utils.PubFunc.encrypt("res_flag=12");
	String gzchartstr = com.hjsj.hrms.utils.PubFunc.encrypt("res_flag=20");
	String ins_setstr = com.hjsj.hrms.utils.PubFunc.encrypt("res_flag=18");
	String gzbdstr = com.hjsj.hrms.utils.PubFunc.encrypt("type=2&res_flag=8");
	String ins_bdstr = com.hjsj.hrms.utils.PubFunc.encrypt("type=8&res_flag=17");
	String orgstr = com.hjsj.hrms.utils.PubFunc.encrypt("type=B&res_flag=31");
	String posstr = com.hjsj.hrms.utils.PubFunc.encrypt("type=K&res_flag=32");
	String psorgansstr = com.hjsj.hrms.utils.PubFunc.encrypt("type=3&res_flag=34");
	String psorgans_jcgstr = com.hjsj.hrms.utils.PubFunc.encrypt("type=6&res_flag=35");
	String psorgans_fgstr = com.hjsj.hrms.utils.PubFunc.encrypt("type=4&res_flag=36");
	String psorgans_gxstr = com.hjsj.hrms.utils.PubFunc.encrypt("type=5&res_flag=37");
	String gzreportstylestr = com.hjsj.hrms.utils.PubFunc.encrypt("res_flag=28");
	String invstr = com.hjsj.hrms.utils.PubFunc.encrypt("res_flag=9");
	String trainjobstr = com.hjsj.hrms.utils.PubFunc.encrypt("res_flag=10");
	String announcestr = com.hjsj.hrms.utils.PubFunc.encrypt("res_flag=11");
	String archtypestr = com.hjsj.hrms.utils.PubFunc.encrypt("res_flag=14");
	String kq_machstr = com.hjsj.hrms.utils.PubFunc.encrypt("res_flag=15");
	String titlestr = com.hjsj.hrms.utils.PubFunc.encrypt("res_flag=24");
	String groupstr = com.hjsj.hrms.utils.PubFunc.encrypt("res_flag=26");
	String filetypestr = com.hjsj.hrms.utils.PubFunc.encrypt("res_flag=19");
	String khfieldstr = com.hjsj.hrms.utils.PubFunc.encrypt("type=23&res_flag=23");
	String khmodulestr = com.hjsj.hrms.utils.PubFunc.encrypt("type=22&res_flag=22");
	String keyeventsetstr = com.hjsj.hrms.utils.PubFunc.encrypt("res_flag=33");
	
	pageContext.setAttribute("tjbstr", tjbstr);
	pageContext.setAttribute("cardstr", cardstr);
	pageContext.setAttribute("musterstr", musterstr);
	pageContext.setAttribute("hmusterstr", hmusterstr);
	pageContext.setAttribute("staticstr", staticstr);
	pageContext.setAttribute("querystr", querystr);
	pageContext.setAttribute("rulestr", rulestr);
	pageContext.setAttribute("rsbdstr", rsbdstr);
	pageContext.setAttribute("ins_bdstr", ins_bdstr);
	pageContext.setAttribute("gzsetstr", gzsetstr);
	pageContext.setAttribute("gzchartstr", gzchartstr);
	pageContext.setAttribute("ins_setstr", ins_setstr);
	pageContext.setAttribute("gzbdstr", gzbdstr);
	pageContext.setAttribute("orgstr", orgstr);
	pageContext.setAttribute("posstr", posstr);
	pageContext.setAttribute("psorgansstr", psorgansstr);
	pageContext.setAttribute("psorgans_jcgstr", psorgans_jcgstr);
	pageContext.setAttribute("psorgans_fgstr", psorgans_fgstr);
	pageContext.setAttribute("psorgans_gxstr", psorgans_gxstr);
	pageContext.setAttribute("gzreportstylestr", gzreportstylestr);
	pageContext.setAttribute("invstr", invstr);
	pageContext.setAttribute("trainjobstr", trainjobstr);
	pageContext.setAttribute("announcestr", announcestr);
	pageContext.setAttribute("archtypestr", archtypestr);
	pageContext.setAttribute("kq_machstr", kq_machstr);
	pageContext.setAttribute("titlestr", titlestr);
	pageContext.setAttribute("groupstr", groupstr);
	pageContext.setAttribute("filetypestr", filetypestr);
	pageContext.setAttribute("khfieldstr", khfieldstr);
	pageContext.setAttribute("khmodulestr", khmodulestr);
	pageContext.setAttribute("keyeventsetstr", keyeventsetstr);
	
	int versionFlag = 1;
	if (userView != null){
		versionFlag = userView.getVersion_flag(); // 1:专业版 0:标准版 
	} 
%>

<hrms:tabset name="pageset" width="99%" height="90%" type="true"> 
<hrms:tab name="tab1" label="sys.res.tjb" function_id="081000,3003400" visible="true" url="/general/template/assign_template_tree.do?b_sort=link&encryptParam=${tjbstr }">
</hrms:tab>
<%
	//zhangh 2019-11-21 【51940】资源授权模块以前只能使用浏览器兼容模式，特殊处理以适应非兼容模式
	if(bSuperAdmin||userView.hasTheFunction("081000")||userView.hasTheFunction("3003400")){
		i++;
		tabstr.append("<tr height=\"20\" id=\"tab1_1\" onclick=\"itemHref('");
		tabstr.append("tab1");
		tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab1');\"");
		tabstr.append(" onMouseout=\"mout(this,'tab1');\">");
		tabstr.append("<td style=\"cursor:pointer\">");
		if(i<10)
			tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.tjb"));
		else
			tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.tjb"));
		tabstr.append("</td></tr>");
	}
%>

<hrms:tab name="tab2" label="sys.res.card" function_id="081001,3003401" visible="true" url="/general/template/assign_template_tree.do?b_sort=link&encryptParam=${cardstr }">
</hrms:tab>
<%
if(bSuperAdmin||userView.hasTheFunction("081001")||userView.hasTheFunction("3003401")){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab2_1\" onclick=\"itemHref('");
	tabstr.append("tab2");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab2');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab2');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.card"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.card"));
	tabstr.append("</td></tr>");
}
%>
<!-- 简单花名册优化，花名册不需要资源授权，按照用户的权限范围与花名册的所属单位的关系来显示。zyh 2019-05-15 -->
<%-- <hrms:tab name="tab3" label="sys.res.muster" function_id="081002,3003402" visible="true" url="/general/template/assign_template_tree.do?b_query=link&encryptParam=${musterstr }">
</hrms:tab>
<%
if(bSuperAdmin||userView.hasTheFunction("081002")||userView.hasTheFunction("3003402")){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab3_1\" onclick=\"itemHref('");
	tabstr.append("tab3");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab3');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab3');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.muster"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.muster"));
	tabstr.append("</td></tr>");
}
%> --%>
<hrms:tab name="tab4" label="sys.res.hmuster" function_id="081003,3003403" visible="true" url="/general/template/assign_template_tree.do?b_sort=link&encryptParam=${hmusterstr }">
</hrms:tab>
<%
if(bSuperAdmin||userView.hasTheFunction("081003")||userView.hasTheFunction("3003403")){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab4_1\" onclick=\"itemHref('");
	tabstr.append("tab4");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab4');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab4');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.hmuster"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.hmuster"));
	tabstr.append("</td></tr>");
}
%>
<hrms:tab name="tab5" label="sys.res.static" function_id="081004,3003404" visible="true" url="/system/security/open_resource.do?b_query=link&encryptParam=${staticstr }">
</hrms:tab>
<%
if(bSuperAdmin||userView.hasTheFunction("081004")||userView.hasTheFunction("3003404")){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab5_1\" onclick=\"itemHref('");
	tabstr.append("tab5");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab5');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab5');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.static"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.static"));
	tabstr.append("</td></tr>");
}
%>
<hrms:tab name="tab6" label="sys.res.query" function_id="081005,3003405" visible="true" url="/system/security/open_resource.do?b_query=link&encryptParam=${querystr }">
</hrms:tab>
<%
if(bSuperAdmin||userView.hasTheFunction("081005")||userView.hasTheFunction("3003405")){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab6_1\" onclick=\"itemHref('");
	tabstr.append("tab6");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab6');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab6');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.query"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.query"));
	tabstr.append("</td></tr>");
}
%>
<hrms:tab name="tab7" label="sys.res.rule" mod_id="12" function_id="081006,3003406" visible="true" url="/selfservice/lawbase/lawtext/assign_law_dir.do?b_query=link&encryptParam=${rulestr }">
</hrms:tab>
<%
if(lock.isHaveBM(12) && (bSuperAdmin||userView.hasTheFunction("081006")||userView.hasTheFunction("3003406"))){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab7_1\" onclick=\"itemHref('");
	tabstr.append("tab7");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab7');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab7');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.rule"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.rule"));
	tabstr.append("</td></tr>");
}
%>
<hrms:tab name="tab8" label="sys.res.rsbd" function_id="3003407" visible="true" url="/general/template/assign_template_tree.do?b_sort=link&encryptParam=${rsbdstr }">
</hrms:tab>
<%
if(bSuperAdmin||userView.hasTheFunction("081007")){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab8_1\" onclick=\"itemHref('");
	tabstr.append("tab8");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab8');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab8');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.rsbd"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.rsbd"));
	tabstr.append("</td></tr>");
}
%>
<hrms:tab name="tab13" label="sys.res.gzset" function_id="3003413" visible="true" url="/system/security/open_resource.do?b_query=link&encryptParam=${gzsetstr }">
</hrms:tab>
<%
if(bSuperAdmin||userView.hasTheFunction("3003413")){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab13_1\" onclick=\"itemHref('");
	tabstr.append("tab13");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab13');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab13');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.gzset"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.gzset"));
	tabstr.append("</td></tr>");
}
%>
<% boolean haveChartPriv = versionFlag == 1; //专业版有薪资分析图功能 %>
<hrms:tab name="tab20" label="sys.res.gzchart" function_id="3003420" visible="<%=haveChartPriv %>" url="/system/security/open_resource.do?b_query=link&encryptParam=${gzchartstr }">
</hrms:tab>
<%
if(bSuperAdmin||userView.hasTheFunction("3003420")){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab20_1\" onclick=\"itemHref('");
	tabstr.append("tab20");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab20');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab20');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.gzchart"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.gzchart"));
	tabstr.append("</td></tr>");
}
%>
<hrms:tab name="tab18" label="sys.res.ins_set" function_id="3003416" visible="true" url="/system/security/open_resource.do?b_query=link&encryptParam=${ins_setstr }">
</hrms:tab>
<%
if(bSuperAdmin||userView.hasTheFunction("3003416")){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab18_1\" onclick=\"itemHref('");
	tabstr.append("tab18");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab18');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab18');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.ins_set"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.ins_set"));
	tabstr.append("</td></tr>");
}
%>
<hrms:tab name="tab9" label="sys.res.gzbd" function_id="3003408" visible="true" url="/general/template/assign_template_tree.do?b_sort=link&encryptParam=${gzbdstr }">
</hrms:tab>
<%
if(bSuperAdmin||userView.hasTheFunction("3003408")){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab9_1\" onclick=\"itemHref('");
	tabstr.append("tab9");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab9');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab9');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.gzbd"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.gzbd"));
	tabstr.append("</td></tr>");
}
%>
<hrms:tab name="tab17" label="sys.res.ins_bd" function_id="3003415" visible="true" url="/general/template/assign_template_tree.do?b_sort=link&encryptParam=${ins_bdstr }">
</hrms:tab>
<%
if(bSuperAdmin||userView.hasTheFunction("3003415")){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab17_1\" onclick=\"itemHref('");
	tabstr.append("tab17");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab17');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab17');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.ins_bd"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.ins_bd"));
	tabstr.append("</td></tr>");
}
%>
<%if(ver_ctrl.searchFunctionId("23067")){ %>
<hrms:tab name="tab25" label="sys.res.org" function_id="3003424" visible="true" url="/general/template/assign_template_tree.do?b_sort=link&encryptParam=${orgstr }">
</hrms:tab>
<%
if(bSuperAdmin||userView.hasTheFunction("3003424")){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab25_1\" onclick=\"itemHref('");
	tabstr.append("tab25");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab25');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab25');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.org"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.org"));
	tabstr.append("</td></tr>");
}
}
%>
<%if(ver_ctrl.searchFunctionId("231102")){ %>
<hrms:tab name="tab26" label="sys.res.pos" function_id="3003425" visible="true" url="/general/template/assign_template_tree.do?b_sort=link&encryptParam=${posstr }">
</hrms:tab>
<%
if(bSuperAdmin||userView.hasTheFunction("3003425")){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab26_1\" onclick=\"itemHref('");
	tabstr.append("tab26");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab26');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab26');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.pos"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.pos"));
	tabstr.append("</td></tr>");
}
}
%>
<%
	String unit_property ="";
 if(SystemConfig.getPropertyValue("unit_property")!=null&&SystemConfig.getPropertyValue("unit_property").trim().length()>0){
 	unit_property = SystemConfig.getPropertyValue("unit_property");
 	unit_property = unit_property.replace("psorgans_","");
 }
 if(unit_property.equals("jcg")){
 %>
 <hrms:tab name="tab28" label="sys.res.psorgans" function_id="370" visible="true" url="/general/template/assign_template_tree.do?b_sort=link&encryptParam=${psorgansstr }">
</hrms:tab>
<%
if(bSuperAdmin||userView.hasTheFunction("370")){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab28_1\" onclick=\"itemHref('");
	tabstr.append("tab28");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab28');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab28');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.psorgans"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.psorgans"));
	tabstr.append("</td></tr>");
}
%>
<hrms:tab name="tab29" label="sys.res.psorgans_jcg" function_id="371" visible="true" url="/general/template/assign_template_tree.do?b_sort=link&encryptParam=${psorgans_jcgstr }">
</hrms:tab>
<%
if(bSuperAdmin||userView.hasTheFunction("371")){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab29_1\" onclick=\"itemHref('");
	tabstr.append("tab29");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab29');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab29');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.psorgans_jcg"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.psorgans_jcg"));
	tabstr.append("</td></tr>");
}
}else  if(unit_property.equals("fg")){
%>
 <hrms:tab name="tab28" label="sys.res.psorgans" function_id="370" visible="true" url="/general/template/assign_template_tree.do?b_sort=link&encryptParam=${psorgansstr }">
</hrms:tab>
<%
if(bSuperAdmin||userView.hasTheFunction("370")){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab28_1\" onclick=\"itemHref('");
	tabstr.append("tab28");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab28');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab28');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.psorgans"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.psorgans"));
	tabstr.append("</td></tr>");
}
%>
<hrms:tab name="tab29" label="sys.res.psorgans_fg" function_id="372" visible="true" url="/general/template/assign_template_tree.do?b_sort=link&encryptParam=${psorgans_fgstr }">
</hrms:tab>
<%
if(bSuperAdmin||userView.hasTheFunction("372")){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab29_1\" onclick=\"itemHref('");
	tabstr.append("tab29");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab29');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab29');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.psorgans_fg"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.psorgans_fg"));
	tabstr.append("</td></tr>");
}
}else  if(unit_property.equals("gx")){
%>
 <hrms:tab name="tab28" label="sys.res.psorgans" function_id="370" visible="true" url="/general/template/assign_template_tree.do?b_sort=link&encryptParam=${psorgansstr }">
</hrms:tab>
<%
if(bSuperAdmin||userView.hasTheFunction("370")){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab28_1\" onclick=\"itemHref('");
	tabstr.append("tab28");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab28');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab28');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.psorgans"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.psorgans"));
	tabstr.append("</td></tr>");
}
%>
<hrms:tab name="tab29" label="sys.res.psorgans_gx" function_id="373" visible="true" url="/general/template/assign_template_tree.do?b_sort=link&encryptParam=${psorgans_gxstr }">
</hrms:tab>
<%
if(bSuperAdmin||userView.hasTheFunction("373")){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab29_1\" onclick=\"itemHref('");
	tabstr.append("tab29");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab29');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab29');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.psorgans_gx"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.psorgans_gx"));
	tabstr.append("</td></tr>");
}
}
%>
<hrms:tab name="tab24" label="sys.res.gzreportstyle" function_id="3003423" visible="true" url="/system/security/open_resource.do?b_query=link&encryptParam=${gzreportstylestr }">
</hrms:tab>
<%
if(bSuperAdmin||userView.hasTheFunction("3003423")){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab24_1\" onclick=\"itemHref('");
	tabstr.append("tab24");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab24');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab24');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.gzreportstyle"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.gzreportstyle"));
	tabstr.append("</td></tr>");
}
%>

<% if (lock.isHaveBM(2)) {%>
<hrms:tab name="tab11" label="sys.res.trainjob" function_id="081010,3003410" visible="true" url="/system/security/open_resource.do?b_query=link&encryptParam=${trainjobstr }">
</hrms:tab>
<% } else if (lock.isHaveBM(10)) { %>
<hrms:tab name="tab11" label="sys.res.trainjob" function_id="081010,3003410" visible="true" url="/system/security/open_resource.do?b_query=link&encryptParam=${trainjobstr }">
</hrms:tab>
<%} %>
<%
if((lock.isHaveBM(6) || lock.isHaveBM(22)) && (bSuperAdmin||userView.hasTheFunction("081010")||userView.hasTheFunction("3003410"))){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab11_1\" onclick=\"itemHref('");
	tabstr.append("tab11");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab11');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab11');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.trainjob"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.trainjob"));
	tabstr.append("</td></tr>");
}
%>
<hrms:tab name="tab12" label="sys.res.announce" function_id="081011,3003411" visible="true" url="/system/security/open_resource.do?b_query=link&encryptParam=${announcestr }">
</hrms:tab>
<%
if(bSuperAdmin||userView.hasTheFunction("081011")||userView.hasTheFunction("3003411")){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab12_1\" onclick=\"itemHref('");
	tabstr.append("tab12");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab12');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab12');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.announce"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.announce"));
	tabstr.append("</td></tr>");
}
%>
<hrms:tab name="tab15" label="sys.res.archtype" function_id="081012,3003412" visible="true" url="/system/security/open_resource.do?b_query=link&encryptParam=${archtypestr }">
</hrms:tab>
<%
if(bSuperAdmin||userView.hasTheFunction("081012")||userView.hasTheFunction("3003412")){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab15_1\" onclick=\"itemHref('");
	tabstr.append("tab15");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab15');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab15');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.archtype"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.archtype"));
	tabstr.append("</td></tr>");
}
%>
<% if (lock.isHaveBM(6)) {%>
<hrms:tab name="tab16" label="sys.res.kq_mach" function_id="3003414" visible="true" url="/system/security/open_resource.do?b_query=link&encryptParam=${kq_machstr }">
</hrms:tab>
<% } else if (lock.isHaveBM(22)) { %>
<hrms:tab name="tab16" label="sys.res.kq_mach" function_id="3003414" visible="true" url="/system/security/open_resource.do?b_query=link&encryptParam=${kq_machstr }">
</hrms:tab>
<%} %>
<%
if((lock.isHaveBM(6) || lock.isHaveBM(22)) && (bSuperAdmin||userView.hasTheFunction("3003414"))){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab16_1\" onclick=\"itemHref('");
	tabstr.append("tab16");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab16');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab16');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.kq_mach"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.kq_mach"));
	tabstr.append("</td></tr>");
}
%>
<% if (lock.isHaveBM(6)) {%>
<hrms:tab name="tab23" label="kq.class.title" function_id="081014,3003421" visible="false" url="/system/security/kq_class_priv.do?b_query=link&encryptParam=${titlestr }">
</hrms:tab>
<% } else if (lock.isHaveBM(22)) { %>
<hrms:tab name="tab23" label="kq.class.title" function_id="081014,3003421" visible="false" url="/system/security/kq_class_priv.do?b_query=link&encryptParam=${titlestr }">
</hrms:tab>
<%} %>
<%
/**右键菜单 基本班次 注释掉
if((lock.isHaveBM(6) || lock.isHaveBM(22)) && (bSuperAdmin||userView.hasTheFunction("081014")||userView.hasTheFunction("3003421"))){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab23_1\" onclick=\"itemHref('");
	tabstr.append("tab23");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab23');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab23');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("kq.class.title"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("kq.class.title"));
	tabstr.append("</td></tr>");
}
**/
%>
<!-- 班次已改为按所属部门进行管理，去掉资源授权 -->
<% if (lock.isHaveBM(6)) {%>
<hrms:tab name="tab32" label="kq.group" function_id="081015,3003422" visible="true" url="/system/security/kq_group_priv.do?b_query=link&encryptParam=${groupstr }">
</hrms:tab>
<% } else if (lock.isHaveBM(22)) { %>
<hrms:tab name="tab32" label="kq.group" function_id="081015,3003422" visible="true" url="/system/security/kq_group_priv.do?b_query=link&encryptParam=${groupstr }">
</hrms:tab>
<%} %>
<%
if((lock.isHaveBM(6) || lock.isHaveBM(22)) && (bSuperAdmin||userView.hasTheFunction("081015")||userView.hasTheFunction("3003422"))){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab32_1\" onclick=\"itemHref('");
	tabstr.append("tab32");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab32');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab32');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("kq.group"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("kq.group"));
	tabstr.append("</td></tr>");
}
%>
<hrms:tab name="tab19" label="sys.res.filetype" mod_id="12" function_id="3003417" visible="true" url="/selfservice/lawbase/lawtext/assign_law_dir.do?b_query=link&encryptParam=${filetypestr }">
</hrms:tab>
<%
if(lock.isHaveBM(12) && (bSuperAdmin||userView.hasTheFunction("3003417"))){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab19_1\" onclick=\"itemHref('");
	tabstr.append("tab19");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab19');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab19');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.filetype"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.filetype"));
	tabstr.append("</td></tr>");
}
%>


<hrms:tab name="tab21" label="sys.res.khfield" function_id="3003418" visible="true" url="/general/template/assign_template_tree.do?b_sort=link&encryptParam=${khfieldstr }">
</hrms:tab>

<%
if(bSuperAdmin||userView.hasTheFunction("3003418")){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab21_1\" onclick=\"itemHref('");
	tabstr.append("tab21");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab21');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab21');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.khfield"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.khfield"));
	tabstr.append("</td></tr>");
}
%>

<hrms:tab name="tab22" label="sys.res.khmodule" function_id="3003419" visible="true" url="/general/template/assign_template_tree.do?b_sort=link&encryptParam=${khmodulestr }">
</hrms:tab>



<%
if(bSuperAdmin||userView.hasTheFunction("3003419")){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab22_1\" onclick=\"itemHref('");
	tabstr.append("tab22");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab22');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab22');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.khmodule"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.khmodule"));
	tabstr.append("</td></tr>");
}
%>
<hrms:tab name="tab27" label="sys.res.keyeventset" function_id="3003427" visible="true" url="/system/security/open_resource.do?b_query=link&encryptParam=${keyeventsetstr }">
</hrms:tab>
<%
if((bSuperAdmin||userView.hasTheFunction("3003427"))&&ver_ctrl.searchFunctionId("3003427")){
	i++;
	tabstr.append("<tr height=\"20\" id=\"tab27_1\" onclick=\"itemHref('");
	tabstr.append("tab27");
	tabstr.append("',this,"+i+");\" onMouseover=\"mover(this,'tab27');\"");
	tabstr.append(" onMouseout=\"mout(this,'tab27');\">");
	tabstr.append("<td style=\"cursor:pointer\">");
	if(i<10)
		tabstr.append("&nbsp;0"+i+"."+ResourceFactory.getProperty("sys.res.keyeventset"));
	else
		tabstr.append("&nbsp;"+i+"."+ResourceFactory.getProperty("sys.res.keyeventset"));
	tabstr.append("</td></tr>");
}
%>
<%tabstr.append("</table>"); %>
</hrms:tabset>
<div id="mlay" style="position:absolute;display:none;overflow:auto;height:150px;width:160px;">
		<%=tabstr.toString() %>
</div>
<table  cellpadding="0" cellspacing="0"   width="100%" align="center" style="margin-left:8px;">
          <tr>
            <td align="left" height="35px;">
	 	<logic:equal name="resourceForm" property="fromflag" value="0">            
         	  <hrms:submit style="float:left" styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 		  </hrms:submit>
	 	</logic:equal>
	 	<logic:equal name="resourceForm" property="fromflag" value="1">            
         	  <hrms:submit style="float:left" styleClass="mybutton" property="br_return0">
            		<bean:message key="button.return"/>
	 		  </hrms:submit>
	 	</logic:equal>
	 	<logic:equal name="resourceForm" property="fromflag" value="2">            
         	  <hrms:submit style="float:left" styleClass="mybutton" property="br_return1">
            		<bean:message key="button.return"/>
	 		  </hrms:submit>
	 	</logic:equal>	
	 	<logic:equal name="resourceForm" property="fromflag" value="3">            
         	  <hrms:submit style="float:left" styleClass="mybutton" property="br_return2">
            		<bean:message key="button.return"/>
	 		  </hrms:submit>
	 	</logic:equal>
				<%if(StringUtils.isNotBlank(role_name)) {%>
				<div style="float: right; color: #5c6070;margin-right: 15px"><span style="margin-right: 5px"><%=userFlagText%></span><span><%=role_name%></span></div>
				<%}%>
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
	if(top>30) {
		return false;
	}
	mlay.style.display="";
	mlay.style.pixelTop=top;
	mlay.style.top=top;
	mlay.style.pixelLeft=event.clientX;
	mlay.style.left=event.clientX;
	mlay.style.background=bgc;
	mlay.style.color=txc;
 	return false;
}
function toNext(){
	var tabid="<%=menuname%>";
	if(tabid!=null&&tabid.length>0){
		var tab=$('pageset');
		if(tab.setSelectedTab)
			tab.setSelectedTab("<%=menuname%>");
		menutable = tabid+"_1";
		var obj = document.getElementById(menutable);
		if(obj!=null){
			obj.style.background=cbgc;
			obj.style.color=ctxc ;
		}
	}
}
function itemHref(menuname,obj,index){
	if(menuname!=null&&menuname.length>0){
		var tab=$('pageset');
		//zhangh 2019-11-21 【51940】资源授权模块以前只能使用浏览器兼容模式，特殊处理以适应非兼容模式
		//兼容模式下easyUI自动给生成了table，非兼容模式下则是ul
		if(tab.setSelectedTab){
			tab.setSelectedTab(menuname);
		}else{
			//获取菜单ul
			var tabs = document.getElementsByClassName("tabs")[0];
			//获取所有的菜单li
			var menuList = tabs.children;
			//模拟easyUI点击事件以及设置对应的样式
			menuList[index-1].className = "tabs-selected";
			menuList[index-1].click();
		}
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
if(!getBrowseVersion() || getBrowseVersion()==10){//处理非IE浏览器 样式问题  wangb 20190614 bug 46790
	var t1 = window.setInterval(function(){
		var form = document.getElementsByName('resourceForm')[0];
		form.style.width='99%';
		var _tabsetpane_pageset = document.getElementById('_tabsetpane_pageset');
		_tabsetpane_pageset.style.height = parseInt(_tabsetpane_pageset.style.height)-60;
		_tabsetpane_pageset.style.width = document.body.clientWidth - 10;
		var tabsPanels = _tabsetpane_pageset.getElementsByClassName('tabs-panels')[0];
		var tabsWrap = _tabsetpane_pageset.getElementsByClassName('tabs-wrap')[0];
		var tabsHeader = _tabsetpane_pageset.getElementsByClassName('tabs-header')[0];
		if(tabsPanels){
			window.clearInterval(t1);
			document.getElementsByClassName('tabs-scroller-right')[0].style.zIndex='1';
			tabsPanels.style.height = parseInt(tabsPanels.style.height)-60;
			tabsPanels.style.width = document.body.clientWidth - 12;
			tabsHeader.style.width = document.body.clientWidth - 10;
			tabsWrap.style.width = document.body.clientWidth - 50;
			var panels = tabsPanels.children;
			for(var i = 0 ; i < panels.length ; i++){
				panels[i].style.height = parseInt(panels[i].style.height)-60;
				panels[i].firstChild.style.height = parseInt(panels[i].firstChild.style.height)-60;
				panels[i].style.width = document.body.clientWidth - 12;
				panels[i].firstChild.style.width = document.body.clientWidth - 12;
			}
		}
	},1000);
}
</script>
</body>
