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
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<hrms:themes />
<html:form action="/selfservice/selfinfo/relation"> 
    <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >
       <tr>
           <td align="left"> 
             <hrms:relationtree action="${relationForm.action}" target="${relationForm.target}" dbnamekey="${relationForm.dbnamekey}" a0100key="${relationForm.a0100key}" paramkey="${relationForm.paramkey}" b0110key="${relationForm.b0110key}"/>            
           </td>
           </tr>           
    </table>
</html:form>
<script LANGUAGE="javascript">
	var action = "${relationForm.action}";
	if(action=="/general/sprelationmap/relation_map_drawable.do?b_init=init`relationType=1") // 汇报关系图
	{
		var height=self.parent.nil_body.document.body.clientHeight;
		var width =self.parent.nil_body.document.body.clientWidth;
		root.setAction(root.action+"&clientHeight="+height+"&clientWidth="+width);
	}
	root.openURL();
</script>
