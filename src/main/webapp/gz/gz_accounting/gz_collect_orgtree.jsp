<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
%>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<hrms:themes />
<body oncontextmenu=return(false)>
<html:form action="/gz/gz_accounting/gz_collect_orgtree"> 

  <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >   
	 <tr align="left">
		<td valign="top"></td>
	 </tr>          
     <tr>        
           <td align="left"> 
           <logic:equal value="1" name="collectForm" property="sum_type">
                 <hrms:orgtree action="/gz/gz_accounting/gz_collect_table.do?b_query=link&salaryid=${collectForm.salaryid}" target="mil_body" flag="0"  loadtype="1" showroot="false" umlayer="${collectForm.layer }" rootaction="1" rootPriv="0" nmodule="1" />
           </logic:equal>
           <logic:equal value="0" name="collectForm" property="sum_type">
                 <hrms:orgtree action="/gz/gz_accounting/gz_collect_table.do?b_query=link&salaryid=${collectForm.salaryid}" target="mil_body" flag="0"  loadtype="2" showroot="false" rootaction="1" rootPriv="0" nmodule="1"/>
           </logic:equal>
           </td>
      </tr>            
   </table>
</html:form>
<script>
	 root.openURL();
</script>
</body>
