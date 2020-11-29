<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient,com.hjsj.hrms.businessobject.sys.options.UserPopedom,com.hjsj.hrms.actionform.sys.options.UserPopedomForm,java.util.TreeMap,java.util.Iterator" %>

<%
	EncryptLockClient lockclient = (EncryptLockClient)session.getServletContext().getAttribute("lock");
	/*弹窗模式显示权限明细下 获取是否显示返回按钮参数    wangb  20171204 32858*/
	UserPopedomForm upf1 = (UserPopedomForm)session.getAttribute("userPopedomForm");
	String btnBack = upf1.getBtnBack();
	//从账号管理查看权限 wangb 20190523  bug 48267
	String callback = request.getParameter("callback");
	callback = callback == null? "false":callback;
  %>

<html>
	<head>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>	
	</head>

	<style type="text/css">
		<!--
		.pagebgk{
			border:2px solid #c0c0c0;
			background-color: #FFFFFF;
		}
				
		.mytable {
			border: 1px solid #000000;
		}
		.mylefttd {
			border-right-width: 1px;
			border-bottom-width: 1px;
			border-right-style: solid;
			border-bottom-style: solid;
			border-right-color: #000000;
			border-bottom-color: #000000;
		}
		.myrighttd {
			border-bottom-width: 1px;
			border-bottom-style: solid;
			border-bottom-color: #000000;
		}
		.mybottomtd {
			border-right-width: 1px;
			border-right-style: solid;
			border-right-color: #000000;
		}
		
	-->
	</style>
	<script language="javascript">
		/***********************权限检索*******************************/
		/*
		* modeflag 用户标识(自助/业务)1,2
		* 自助用户 
		* 	dbpre  人员库前缀
		* 	name   
		* 业务用户
		*	dbpre 空
		*	name 用户名
		*/ 
		function userPopedom(modeflag , dbpre, name){
				window.open("/system/options/userpopedom.do?b_query=link&operatorflag=1&modeflag="+modeflag+"&dbpre="+dbpre+"&name="+name,
				'_self','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,top=0,left=220,resizable=no,width=780,height=600');
		}
		
		/*
		*role_id   角色ID
		*role_flag 类别( 1 角色 0 用户组 2 单位,部门,职位) 
		*/
		function orgPopedom(role_id,role_flag){
			//window.open("/system/options/userpopedom.do?b_query=link&operatorflag=2&role_id="+role_id+"&role_flag="+role_flag,
			window.open("/system/options/userpopedom.do?b_query=link&operatorflag=2&role_id="+role_id+"&role_flag="+role_flag+"&btnBack=1&callback=<%=callback%>",//账号分配 权限明细 查看角色权限  添加 callback 控制   and 添加是否显示返回按妞参数  btnBack  wangb 20171204 
			'_self','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,top=0,left=220,resizable=no,width=780,height=600');
		}
	
		function winopen(){
	   		window.opener=null;//不会出现提示信息
	   		self.close();	
	    }
    
	</script>
	<body>
	<%if(lockclient.isHaveBM(31)){ %>
		<!-- 返回 jingq add -->
		<div id="topback">
		<table width="90%" border="0" align="center" style="margin-left:-3px;"><tr><td align="left" height="35px;">
			<INPUT type="button" value='<bean:message key="button.return"/>' onclick="history.back();"  class="mybutton">
		</td></tr></table></div>
		<table width="90%" border="0" align="center"  class="pagebgk">
		<tr>
			<td> 
				<br>
				
				<br>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<bean:write name="userPopedomForm" property="up.displayMessage" filter="false" />
				<!-- 
				<bean:write name="userPopedomForm" property="up.orgOrUserGroup" filter="false" />
				<bean:write name="userPopedomForm" property="up.dept" filter="false" />
				<bean:write name="userPopedomForm" property="up.a0100" filter="false" />
				-->
			</td>
		</tr>
		<tr>
		<td>
		<table width="90%" border="0" align="center" cellspacing="0" class="mytable"> 
		  <tr> 
		    <td colspan="3" class="mylefttd"><div align="center"><bean:message key="popedom.project"/></div></td>
		    <td width="78%" class="myrighttd"><div align="center"><bean:message key="report.conter"/></div></td>
		  </tr>
		  <logic:notEqual name="userPopedomForm" property="flag" value="hidden">
			  <tr> 
			    <td width="2%" rowspan="4"  class="mylefttd"><p><bean:message key="label.sys.warn.domain.role"/></p></td>
			    <td colspan="2" width="12%"  class="mylefttd"><bean:message key="popedom.org"/></td>
			    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.orgOrUserGroup" filter="false" /></td>
			  </tr>
			  <tr> 
			    <td colspan="2" class="mylefttd"><bean:message key="popedom.dept"/></td>
			    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.dept" filter="false" /></td>
			  </tr>
			  <tr> 
			    <td colspan="2" class="mylefttd"><bean:message key="popedom.job"/></td>
			    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.job" filter="false" /></td>
			  </tr>
			  <tr> 
			    <td colspan="2" class="mylefttd"><bean:message key="popedom.my"/></td>
			    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.a0100" filter="false" /></td>
			  </tr>
		  </logic:notEqual>
		  <tr> 
		    <td colspan="3" class="mylefttd" ><bean:message key="popedom.db"/></td>
		    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.dbPres" filter="false" /></td>
		  </tr>
		  <tr> 
		    <td rowspan="4" class="mylefttd" width="2%"><bean:message key="label.sys.cond"/></td>
		    <td rowspan="2" class="mylefttd" width="12%" ><bean:message key="menu.manage"/></td>
		    <td class="mylefttd" width="12%" ><bean:message key="menu.manage"/></td>
		    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.managerSpace" filter="false" /></td>
		  </tr>
		  <tr> 
		    <td class="mylefttd"><bean:message key="popedom.gjtj"/></td>
		    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.formula" filter="false" /></td>
		  </tr>
		  <tr> 
		    <td colspan="2" class="mylefttd"><bean:message key="menu.manage.party"/></td>
		    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.partymanager" filter="false" /></td>
		  </tr>
		  <tr> 
		    <td colspan="2" class="mylefttd"><bean:message key="menu.manage.member"/></td>
		    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.menbermanager" filter="false" /></td>
		  </tr>
		  <tr> 
		    <td rowspan="2" class="mylefttd"><bean:message key="popedom.setorfield"/></td>
		    <td colspan="2" height="59" class="mylefttd"><bean:message key="read.label"/></td>
		    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.setOrItemReadPriv" filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td colspan="2" height="39" class="mylefttd"><bean:message key="write.label"/></td>
		    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.setOrItemWritePriv" filter="false" />&nbsp;</td>
		  </tr>
		  <%
		  	 UserPopedomForm upf = (UserPopedomForm)session.getAttribute("userPopedomForm");
		  	 UserPopedom up = upf.getUp();
		  	 TreeMap privmap = up.getFunctionPriv();
		  	 Iterator i = privmap.keySet().iterator();
		  	 String key = (String)i.next();
		  	 StringBuffer sbhtml = (StringBuffer)privmap.get(key);
		   %>
		  <tr> 
		    <td rowspan="<%=privmap.keySet().size() %>" class="mylefttd"><bean:message key="menu.function"/></td>
		    <td colspan="2" class="mylefttd"><%=key.substring(1) %></td>
		    <td class="myrighttd"><%=sbhtml.toString() %>&nbsp;</td>
		  </tr>
		  <%for(;i.hasNext();){ 
		  
		  	key = (String)i.next();
		  	sbhtml = (StringBuffer)privmap.get(key); 
		  %>
		  <tr> 
		    <td  colspan="2" class="mylefttd"><%=key.substring(1) %></td>
		    <td class="myrighttd"><%=sbhtml.toString() %>&nbsp;</td>
		  </tr>
		  <%} %>
		  <tr> 
		    <td rowspan="26" class="mybottomtd"><bean:message key="popedom.resource"/></td>
		    <td colspan="2" class="mylefttd"><bean:message key="sys.res.card"/></td>
		    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.cardResourcePriv" filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td colspan="2" height="21" class="mylefttd"><bean:message key="sys.res.tjb"/></td>
		    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.reportResourcePriv" filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td colspan="2" class="mylefttd"><bean:message key="sys.res.query"/></td>
		    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.lexprResourcePriv" filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td colspan="2" class="mylefttd"><bean:message key="sys.res.static"/></td>
		    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.staticsResourcePriv" filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td colspan="2" class="mylefttd"><bean:message key="system.options.itemmuster"/></td>
		    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.musterResourcePriv" filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td colspan="2" class="mylefttd"><bean:message key="hmuster.label.info"/></td>
		    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.highMusterResourcePriv" filter="false" />&nbsp;</td>
		  </tr>
		  <hrms:priv module_id="12">
		  <tr> 
		    <td colspan="2" class="mylefttd"><bean:message key="menu.rule"/></td>
			 <td class="myrighttd"><bean:write name="userPopedomForm" property="up.lawruleResourcePriv" filter="false" />&nbsp;</td>
		  </tr>
		  </hrms:priv>
		  <tr> 
		    <td colspan="2" class="mylefttd"><bean:message key="sys.res.rsbd"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.rsbdResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td colspan="2" class="mylefttd"><bean:message key="sys.res.gzbd"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.xzbdResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td colspan="2" class="mylefttd"><bean:message key="sys.res.inv"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.wjdcResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		   <tr> 
		    <td colspan="2" class="mylefttd"><bean:message key="sys.res.trainjob"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.pxbResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		   <tr> 
		    <td colspan="2" class="mylefttd"><bean:message key="sys.res.announce"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.gglResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		   <tr> 
		    <td colspan="2" class="mylefttd"><bean:message key="sys.res.gzset"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.xzlbResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		   <tr> 
		    <td colspan="2" class="mylefttd"><bean:message key="sys.res.gzchart"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.gzfxtResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		   <tr> 
		    <td colspan="2" class="mylefttd"><bean:message key="sys.res.archtype"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.daflResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		   <tr> 
		    <td colspan="2" class="mylefttd"><bean:message key="sys.res.kq_mach"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.kqjResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>

		  <tr> 
		    <td colspan="2" class="mylefttd"><bean:message key="sys.res.ins_bd"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.bxbdResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td colspan="2" class="mylefttd"><bean:message key="sys.res.org"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.orgbdResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td colspan="2" class="mylefttd"><bean:message key="sys.res.pos"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.posbdResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td colspan="2" class="mylefttd"><bean:message key="sys.res.ins_set"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.bxlbResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		  <tr>
			<td class="mylefttd"><bean:message key="kq.class.title"/></td>
			<td class="myrighttd"><bean:write name="userPopedomForm" property="up.jbbcResourcePriv"  filter="false" /></td>
		  </tr>
		  <tr>
			<td class="mylefttd"><bean:message key="kq.init.kqbz"/></td>
			<td class="myrighttd"><bean:write name="userPopedomForm" property="up.kqbzResourcePriv"  filter="false" /></td>
		  </tr>
		  <hrms:priv module_id="12">
		  <tr> 
		    <td  colspan="2" class="mylefttd"><bean:message key="sys.res.filetype"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.wdflResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		  </hrms:priv>
		  <tr> 
		    <td colspan="2" class="mylefttd"><bean:message key="sys.res.knowtype"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.zsflResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td colspan="2" class="mylefttd"><bean:message key="sys.res.khfield"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.khzbResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td colspan="2" class="mybottomtd"><bean:message key="sys.res.khmodule"/></td>
		 	<td><bean:write name="userPopedomForm" property="up.khmbResourcePriv"  filter="false" /></td>
		  </tr>
		</table>
		</td></tr>
		</table>
		
		<!-- 返回 -->
		<div id="back">
			<table width="90%" border="0" align="center" style="margin-left:-3px;"><tr><td align="left" height="35px;">
			<INPUT type="button" value='<bean:message key="button.return"/>' onclick="history.back();"  class="mybutton">
		</td></tr></table>
		</div>
		<!-- 关闭 -->
		<div id="close">
			<table width="90%" border="0" align="center" style="margin-left:-3px;"><tr><td align="left" height="35px;">
			<INPUT id="btn_back" type="button" value='<bean:message key="button.return"/>' onclick="history.back();"  class="mybutton">
			<INPUT type="button" value='<bean:message key="button.close"/>' onclick="winopen();"  class="mybutton">
			</td></tr></table>
		</div>
		
		<!-- 返回和关闭 -->
		<div id="all">
			<table width="90%" border="0" align="center" style="margin-left:-3px;"><tr><td align="left" height="35px;">
			<INPUT type="button" value='<bean:message key="button.return"/>' onclick="history.back();"  class="mybutton">
			&nbsp;&nbsp;
			<INPUT type="button" value='<bean:message key="button.close"/>' onclick="winopen();"  class="mybutton">
			</td></tr></table>
		</div>
		
		<br><br>
		<br>
		<%}else{ %>
		<br>
		
		<table width="90%" border="0" align="center"  class="pagebgk">
		<tr>
			<td> 
				<br><br>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<bean:write name="userPopedomForm" property="up.displayMessage" filter="false" />
				<!-- 
				<bean:write name="userPopedomForm" property="up.orgOrUserGroup" filter="false" />
				<bean:write name="userPopedomForm" property="up.dept" filter="false" />
				<bean:write name="userPopedomForm" property="up.a0100" filter="false" />
				-->
			</td>
		</tr>
		<tr>
		<td>
		<table width="90%" border="0" align="center" cellspacing="0" class="mytable"> 
		  <tr> 
		    <td colspan="2" class="mylefttd"><div align="center"><bean:message key="popedom.project"/></div></td>
		    <td width="78%" class="myrighttd"><div align="center"><bean:message key="report.conter"/></div></td>
		  </tr>
		  <logic:notEqual name="userPopedomForm" property="flag" value="hidden">
			  <tr> 
			    <td width="2%" rowspan="4"  class="mylefttd"><p><bean:message key="label.sys.warn.domain.role"/></p></td>
			    <td width="12%"  class="mylefttd"><bean:message key="popedom.org"/></td>
			    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.orgOrUserGroup" filter="false" /></td>
			  </tr>
			  <tr> 
			    <td class="mylefttd"><bean:message key="popedom.dept"/></td>
			    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.dept" filter="false" /></td>
			  </tr>
			  <tr> 
			    <td class="mylefttd"><bean:message key="popedom.job"/></td>
			    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.job" filter="false" /></td>
			  </tr>
			  <tr> 
			    <td class="mylefttd"><bean:message key="popedom.my"/></td>
			    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.a0100" filter="false" /></td>
			  </tr>
		  </logic:notEqual>
		  <tr> 
		    <td colspan="2" class="mylefttd" ><bean:message key="popedom.db"/></td>
		    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.dbPres" filter="false" /></td>
		  </tr>
		  <tr> 
		    <td rowspan="2" class="mylefttd" width="2%"><bean:message key="label.sys.cond"/></td>
		    <td class="mylefttd" width="12%" ><bean:message key="menu.manage"/></td>
		    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.managerSpace" filter="false" /></td>
		  </tr>
		  <tr> 
		    <td class="mylefttd"><bean:message key="popedom.gjtj"/></td>
		    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.formula" filter="false" /></td>
		  </tr>
		  <tr> 
		    <td rowspan="2" class="mylefttd"><bean:message key="popedom.setorfield"/></td>
		    <td height="59" class="mylefttd"><bean:message key="read.label"/></td>
		    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.setOrItemReadPriv" filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td height="39" class="mylefttd"><bean:message key="write.label"/></td>
		    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.setOrItemWritePriv" filter="false" />&nbsp;</td>
		  </tr>
		  <%
		  	 UserPopedomForm upf = (UserPopedomForm)session.getAttribute("userPopedomForm");
		  	 UserPopedom up = upf.getUp();
		  	 TreeMap privmap = up.getFunctionPriv();
		  	 Iterator i = privmap.keySet().iterator();
		  	 String key = (String)i.next();
		  	 StringBuffer sbhtml = (StringBuffer)privmap.get(key);
		   %>
		  <tr> 
		    <td rowspan="<%=privmap.keySet().size() %>" class="mylefttd"><bean:message key="menu.function"/></td>
		    <td colspan="1" class="mylefttd"><%=key.substring(1) %></td>
		    <td class="myrighttd"><%=sbhtml.toString() %>&nbsp;</td>
		  </tr>
		  <%for(;i.hasNext();){ 
		  
		  	key = (String)i.next();
		  	sbhtml = (StringBuffer)privmap.get(key); 
		  %>
		  <tr> 
		    <td  colspan="1" class="mylefttd"><%=key.substring(1) %></td>
		    <td class="myrighttd"><%=sbhtml.toString() %>&nbsp;</td>
		  </tr>
		  <%} %>
		  <tr> 
		    <td rowspan="26" class="mybottomtd"><bean:message key="popedom.resource"/></td>
		    <td class="mylefttd"><bean:message key="sys.res.card"/></td>
		    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.cardResourcePriv" filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td height="21" class="mylefttd"><bean:message key="sys.res.tjb"/></td>
		    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.reportResourcePriv" filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td class="mylefttd"><bean:message key="sys.res.query"/></td>
		    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.lexprResourcePriv" filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td class="mylefttd"><bean:message key="sys.res.static"/></td>
		    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.staticsResourcePriv" filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td class="mylefttd"><bean:message key="system.options.itemmuster"/></td>
		    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.musterResourcePriv" filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td class="mylefttd"><bean:message key="hmuster.label.info"/></td>
		    <td class="myrighttd"><bean:write name="userPopedomForm" property="up.highMusterResourcePriv" filter="false" />&nbsp;</td>
		  </tr>
		  <hrms:priv module_id="12">
		  <tr> 
		    <td class="mylefttd"><bean:message key="menu.rule"/></td>
			 <td class="myrighttd"><bean:write name="userPopedomForm" property="up.lawruleResourcePriv" filter="false" />&nbsp;</td>
		  </tr>
		  </hrms:priv>
		  <tr> 
		    <td class="mylefttd"><bean:message key="sys.res.rsbd"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.rsbdResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td class="mylefttd"><bean:message key="sys.res.gzbd"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.xzbdResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td class="mylefttd"><bean:message key="sys.res.inv"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.wjdcResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		   <tr> 
		    <td class="mylefttd"><bean:message key="sys.res.trainjob"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.pxbResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		   <tr> 
		    <td class="mylefttd"><bean:message key="sys.res.announce"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.gglResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		   <tr> 
		    <td class="mylefttd"><bean:message key="sys.res.gzset"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.xzlbResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		   <tr> 
		    <td class="mylefttd"><bean:message key="sys.res.gzchart"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.gzfxtResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		   <tr> 
		    <td class="mylefttd"><bean:message key="sys.res.archtype"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.daflResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		   <tr> 
		    <td class="mylefttd"><bean:message key="sys.res.kq_mach"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.kqjResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>

		  <tr> 
		    <td class="mylefttd"><bean:message key="sys.res.ins_bd"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.bxbdResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td class="mylefttd"><bean:message key="sys.res.org"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.orgbdResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td class="mylefttd"><bean:message key="sys.res.pos"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.posbdResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td class="mylefttd"><bean:message key="sys.res.ins_set"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.bxlbResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		  <tr>
			<td class="mylefttd"><bean:message key="kq.class.title"/></td>
			<td class="myrighttd"><bean:write name="userPopedomForm" property="up.jbbcResourcePriv"  filter="false" /></td>
		  </tr>
		  <tr>
			<td class="mylefttd"><bean:message key="kq.init.kqbz"/></td>
			<td class="myrighttd"><bean:write name="userPopedomForm" property="up.kqbzResourcePriv"  filter="false" /></td>
		  </tr>
		  <hrms:priv module_id="12">
		  <tr> 
		    <td class="mylefttd"><bean:message key="sys.res.filetype"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.wdflResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		  </hrms:priv>
		  <tr> 
		    <td class="mylefttd"><bean:message key="sys.res.knowtype"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.zsflResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td class="mylefttd"><bean:message key="sys.res.khfield"/></td>
		 	<td class="myrighttd"><bean:write name="userPopedomForm" property="up.khzbResourcePriv"  filter="false" />&nbsp;</td>
		  </tr>
		  <tr> 
		    <td class="mybottomtd"><bean:message key="sys.res.khmodule"/></td>
		 	<td><bean:write name="userPopedomForm" property="up.khmbResourcePriv"  filter="false" /></td>
		  </tr>
		</table>
		</td></tr>
		<tr>
			<td> 
				<br><br>
			</td>
		</tr>
		</table>
		
		<br>
		<!-- 返回 -->
		<div id="back">
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<INPUT type="button" value='<bean:message key="button.return"/>' onclick="history.back();"  class="mybutton">
		</div>
		<!-- 关闭 -->
		<div id="close">
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<INPUT type="button" value='<bean:message key="button.close"/>' onclick="winopen();"  class="mybutton">
		</div>
		
		<!-- 返回和关闭 -->
		<div id="all">
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<INPUT type="button" value='<bean:message key="button.return"/>' onclick="history.back();"  class="mybutton">
			&nbsp;&nbsp;
			<INPUT type="button" value='<bean:message key="button.close"/>' onclick="winopen();"  class="mybutton">
		</div>
		
		<br><br>
		<br>
		<%} %>

	</body>
</html>

<script language="javaScript">
	if(<%=callback %> /*window.opener == null*/ ){	//window.opener 在 chrome 下判断有问题 修改  wangb 20190523 bug 48267
		//alert("一般链接");
		Element.show("back");
		Element.hide("close");
		Element.hide("all");
	}else{
		//alert("弹出窗体");
		Element.show("close");
		Element.hide("topback");
		Element.hide("back");
		Element.hide("all");
		/*弹窗显示权限明细  下添加返回按钮    wangb  20171204 32858*/
		var btnBack ='<%=btnBack %>';
		if(btnBack ==1)
			Element.show("btn_back");
		else
			Element.hide("btn_back");
			
	}
	
	//var url = window.opener.location;
	//if(url.indexOf("userpopedom.jsp") != -1){
	//		alert("弹出窗体之弹出窗体")
	//		Element.show("all");
	//		Element.hide("back");
	//		Element.hide("close");
	//	}else{}
	//}
</script>