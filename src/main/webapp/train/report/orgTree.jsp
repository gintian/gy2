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
	String viewunit = "1";
	if(userView != null){
		if(userView.getStatus()==4||userView.isSuper_admin()){
			viewunit="0";
		}
		/**liwc 业务用户走操作单位，没有操作单位时走管理范围=lmm*/
		if(userView.getStatus()==0&&!userView.isSuper_admin()){
			String codeall = userView.getUnit_id();
			if(codeall==null||codeall.length()<3)
				viewunit="0";
		}
		
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
%>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<html:form action="/train/report/orgTree"> 

  <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >   
	 <tr align="left">
		<td valign="top"></td>
	 </tr>          
     <tr>        
           <td align="left"> <!-- viewunit="<%=viewunit%>" -->
                 <hrms:orgtree action="/train/report/trainRepList.do?b_query=link" target="mil_body" flag="0" nmodule="6" loadtype="1" priv="1" showroot="false" dbpre="" rootaction="1"/>
           </td>
      </tr>            
   </table>
</html:form>
<script>
	root.openURL();
</script>
