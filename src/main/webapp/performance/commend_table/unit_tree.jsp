	<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
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
<!--
	function initTreeNode()
  {
  var obj=root.childNodes[0];
  if(obj)
  {
    root.expand();
    selectedClass("treeItem-text-"+obj.id);
    obj.openURL();
  }

}
//-->
</script>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<hrms:themes />
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<html:form action="/performance/commend_table/leadership_members"> 
  <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >   
	 <tr align="left">
		<td valign="top"></td>
	 </tr>          
     <tr>  
           <td align="left"> 
               <hrms:orgtree action="/performance/commend_table/leadership_members.do?b_search=search" target="mil_body" flag="0"  loadtype="2" priv="0" showroot="false" dbpre="" rootaction="1"/>			           
           </td>
      </tr>  
   </table>
</html:form>
<script>
	initTreeNode();
</script>