<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.actionform.gz.gz_amount.CroPayMentForm"%>
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
<hrms:themes />
<html:form action="/gz/gz_amount/gz_gross_tree"> 
  <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >   
	 <tr align="left">
		<td valign="top"></td>
	 </tr>          
     <tr>      
     <%CroPayMentForm croPayMentForm = (CroPayMentForm)session.getAttribute("croPayMentForm");   
     String loadtype=croPayMentForm.getCtrl_type();
     String viewUnit = croPayMentForm.getViewUnit();
     String cascadingctrl=croPayMentForm.getCascadingctrl();
     %>
           <td align="left"> 
           <logic:equal value="3" name="croPayMentForm" property="viewUnit">
               <hrms:orgtree action="/gz/gz_amount/gropayment.do?b_query=link&opt=init" target="mil_body" flag="0"  loadtype="<%=loadtype%>" nmodule="2" cascadingctrl="<%=cascadingctrl%>" priv="1" showroot="false" dbpre="" rootaction="1" rootPriv="1"/>			           
           </logic:equal>
           <logic:notEqual value="3" name="croPayMentForm" property="viewUnit">
               <hrms:orgtree action="/gz/gz_amount/gropayment.do?b_query=link&opt=init" target="mil_body" flag="0"  loadtype="<%=loadtype%>" viewunit="<%=viewUnit%>" cascadingctrl="<%=cascadingctrl%>" priv="1" showroot="false" dbpre="" rootaction="1" rootPriv="1"/>			           
           </logic:notEqual>
           </td>
           <html:hidden name="croPayMentForm" property="ctrl_type"/>
           <html:hidden name="croPayMentForm" property="cascadingctrl"/>
           <html:hidden name="croPayMentForm" property="viewUnit"/>
      </tr>            
   </table>
</html:form>
<script>
	root.openURL();
</script>

