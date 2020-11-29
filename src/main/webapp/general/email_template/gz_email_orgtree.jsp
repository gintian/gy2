<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.actionform.general.email_template.GzEmailForm"%>
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
	GzEmailForm gef = (GzEmailForm)session.getAttribute("gzEmailForm");
	showUnitCodeTree = gef.getShowUnitCodeTree();
	if(userView.getUnit_id()==null||userView.getUnit_id().length()<3)
	       showUnitCodeTree="0";
%>

<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<hrms:themes></hrms:themes>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<html:form action="/general/email_template/gz_email_orgtree"> 

  <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >   
	 <tr align="left">
		<td valign="top"></td>
	 </tr>          
     <tr>        
           <td align="left"> 
                 <hrms:orgtree action="/general/email_template/gz_send_email.do?b_init=init" target="mil_body" flag="0" viewunit="<%=showUnitCodeTree%>"  loadtype="1" priv="${gzEmailForm.priv}" showroot="true" dbpre="" rootaction="1" rootPriv="0"/>			           
           </td>
      </tr>            
   </table>
</html:form>
<script>
	root.openURL();
</script>