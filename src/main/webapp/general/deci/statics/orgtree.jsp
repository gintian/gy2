<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.browse.SynthesisBrowseForm"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="java.util.*"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>

<%
	response.setHeader("Pragma", "No-cache");
 	response.setHeader("Cache-Control", "no-cache");
 	response.setDateHeader("Expires", 0);
%>

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
<script language='javascript'>

</script>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<hrms:themes></hrms:themes>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<html:form action="/general/deci/statics/loademploymakeupanalyse"> 
  <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >   
	<tr>        
           <td align="left" colspan="2" > 
                 <hrms:orgtree action="/general/deci/statics/employmakeupanalyse.do?b_search=link" target="nil_body" flag="0"  priv="1" showroot="false"/>			           
           </td>
         </tr>            
   </table>
</html:form>
