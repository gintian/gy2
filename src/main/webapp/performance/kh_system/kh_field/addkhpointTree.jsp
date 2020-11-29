<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
	
%>
<script type="text/javascript">
	function initqnode(){
		var codeitemid="${khFieldForm.codeitemid}";
		var obj=root.childNodes[0];
		if(obj){
	    	selectedClass("treeItem-text-"+obj.id);
	     	obj.select();
	     }else{
	   		if(root){
	     for(var j=0;j<root.childNodes.length;j++)
				{
				obj.expand();
				var obj=root.childNodes[j];
					if(obj.uid==codeitemid)
					{
						var obj=root.childNodes[j];
						  selectedClass("treeItem-text-"+obj.id);
	     				  obj.select();
	     				
						break;
					}
				}
			}
	  }
	  }
	
</script>
<HTML>
	<HEAD>
		<TITLE></TITLE>
<hrms:themes />
		<link href="/css/xtree.css" rel="stylesheet" type="text/css">
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
		<script language="javascript" src="/js/validate.js"></script>
		<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
		<SCRIPT LANGUAGE=javascript src="/js/codetree.js"></SCRIPT>
		<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>
		<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
		<script type="text/javascript">
		
		</script>
		<style>
		</style>
	</HEAD>
	
	<body>
	<html:form action="/performance/kh_system/kh_field/init_grade_template">
	<table width="100%" align="left" border="0" cellpadding="0" cellspacing="1">
	<tr>
	<td valign="top">
	</td>
	</tr> 
	<tr>
	<td align="left">
	 <hrms:orgtree action="/performance/kh_system/kh_field/init_grade_template.do?b_load=load&flag=0" target="mil_body" flag="0"  loadtype="1" priv="${khFieldForm.priv}" showroot="true" dbpre="" rootaction="1" rootPriv="0"/>
	</td>
	</tr>
	
	</table>
	<input type="hidden" name="points" value="${khFieldForm.points}"/>
	</html:form>
	</body>
	<script language="javascript">
	 	initDocument();
	 	initqnode();
	</script>
</HTML>


