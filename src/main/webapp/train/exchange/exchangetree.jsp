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
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<html:form action="/train/exchange/exchangetree"> 
  <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >   
	 <tr align="left">
		<td valign="top"></td>
	 </tr>          
     <tr>        
           <td align="left">
	           <logic:equal value="1" name="exchangeForm" property="model">
	                 <hrms:orgtree action="/train/exchange/exchangemanage.do?b_query=link" target="mil_body" flag="0" nmodule="6" loadtype="2" priv="1" showroot="false" dbpre="" rootaction="1"/>			           
	           </logic:equal>
	           <logic:equal value="2" name="exchangeForm" property="model">
	                 <hrms:orgtree action="/train/exchange/exchangerecord.do?b_query=link" target="mil_body" flag="1" nmodule="6" loadtype="0" priv="1" showroot="false" dbpre="" rootaction="1"/>			           
	           </logic:equal>
           </td>
      </tr>            
   </table>
</html:form>
<script>
	root.openURL();
</script>
