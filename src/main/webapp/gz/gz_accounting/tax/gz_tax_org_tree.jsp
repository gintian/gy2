<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_accounting.TaxTableForm"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
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
	
	TaxTableForm form=(TaxTableForm)session.getAttribute("taxTableForm"); 
	String returnFlag = form.getReturnFlag();
	String theyear = form.getTheyear();
	String themonth = form.getThemonth();
	String operOrg = form.getOperOrg();
	String url = "/gz/gz_accounting/tax/search_tax_table.do?b_query=link&init=first";
	if(returnFlag.equals("1"))
		url+="&returnFlag=1&theyear="+theyear+"&themonth="+themonth+"&operOrg="+operOrg;
	
%>

<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<hrms:themes />
<html:form action="/gz/gz_accounting/tax/gz_tax_org_tree"> 
  <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >   
	 <tr align="left">
		<td valign="top"></td>
	 </tr>          
     <tr>        
           <td align="left"> 
           <logic:equal value="0" name="taxTableForm" property="filterByMdule">
                 <hrms:orgtree action="<%=url %>" target="mil_body" flag="0"  loadtype="1" priv="1" showroot="true" dbpre="" rootaction="1" rootPriv="0"/>	
            </logic:equal>	
            <logic:equal value="1" name="taxTableForm" property="filterByMdule">
            	<%String clientName = SystemConfig.getPropertyValue("clientName");
			if(clientName==null||!clientName.equalsIgnoreCase("weichai")) 
			{ %>       
               <hrms:orgtree action="<%=url %>" target="mil_body" flag="0"  nmodule="3" loadtype="1" priv="1" showroot="true" dbpre="" rootaction="1" rootPriv="0"/>	
            <%}else{ %>
            	<hrms:orgtree action="<%=url %>" target="mil_body" flag="0"  nmodule="3" cascadingctrl="1"  loadtype="1" priv="1" showroot="true" dbpre="" rootaction="1" rootPriv="0"/>	
            <%} %>
            </logic:equal>	           
           </td>
           <html:hidden name="taxTableForm" property="filterByMdule"/>
      </tr>           
   </table>
</html:form>
<script>
	root.openURL();
</script>
