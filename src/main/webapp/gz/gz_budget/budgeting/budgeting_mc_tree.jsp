<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_budget.BudgetingForm"%>
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
	
	BudgetingForm form=(BudgetingForm)session.getAttribute("budgetingForm"); 


	
%>

<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<html:form action="/gz/gz_budget/budgeting/budgeting_table"> 
  <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >   
	 <tr align="left">
		<td valign="top"></td>
	 </tr>          
     <tr>        
           <td align="left"> 
				 <hrms:orgtree action="/gz/gz_budget/budgeting/budgeting_table.do?b_search=int&tab_id=2" target="mil_body_budgeting" flag="0"  loadtype="1" 
				 priv="1" showroot="true" rootPriv="0" dbpre="" rootaction="1"/>
		   </td>

      </tr>           
   </table>
</html:form>
<script>
 root.openURL();
</script>
