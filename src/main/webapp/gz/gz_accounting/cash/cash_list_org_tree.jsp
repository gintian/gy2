<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.actionform.gz.gz_accounting.cash.CashListForm"%>
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
	String showUnitCodeTree="0";
	CashListForm clf=(CashListForm)session.getAttribute("cashListForm");
	 showUnitCodeTree=clf.getShowUnitCodeTree();
	 if(userView.getUnit_id()==null||userView.getUnit_id().length()<3)
	     showUnitCodeTree="0";
	 
%>

<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<hrms:themes />
<html:form action="/gz/gz_accounting/cash/cash_list_org_tree"> 

  <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >   
	 <tr align="left">
		<td valign="top"></td>
	 </tr>          
     <tr>        
           <td align="left"> 
                 <hrms:orgtree action="/gz/gz_accounting/cash/initCashList.do?b_query=init" target="mil_body" viewunit="<%=showUnitCodeTree%>" flag="0"  loadtype="1" priv="${cashListForm.priv}" showroot="false" dbpre="" rootaction="1" rootPriv="0"/>			           
           </td>
      </tr>            
   </table>
</html:form>
<script>
	root.openURL();
</script>
