<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.competencymodal.personPostModal.PersonPostModalForm,				 
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>

<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	
	//  绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11	
	String operOrg =userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
	
	if(userView != null)
	{
	  	css_url=userView.getCssurl();
	  	if(css_url==null||css_url.equals(""))
	  	 	css_url="/css/css1.css";
	}
%>
<%
	PersonPostModalForm personPostModalForm=(PersonPostModalForm)session.getAttribute("personPostModalForm");	
	
%>

<script language="JavaScript">


</script>

<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<html:form action="/competencymodal/personPostModal/orgTree"> 

	  <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >	  		 	  		 	         
		<tr>        
		    <td align="left"> 
		     	  
		    <% if (operOrg.length() > 2){ %>
            	<hrms:orgtree action="/competencymodal/personPostModal/personPostMatch.do?b_query=link" target="mil_body" flag="0"  loadtype="0" priv="1" showroot="false" viewunit="1" nmodule="5" dbpre="" rootaction="1" />				           
 				              			
			<% }else{ %>	
				<hrms:orgtree action="/competencymodal/personPostModal/personPostMatch.do?b_query=link" target="mil_body" flag="0"  loadtype="0" priv="1" showroot="false" dbpre="" rootaction="1" />				           
			<% } %>					 	  
				 				 		                 		           
		   </td>
		</tr>            
	  </table>
	  
</html:form>
<script>
	root.openURL();
</script>
