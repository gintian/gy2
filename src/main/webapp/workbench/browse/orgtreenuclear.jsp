<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%!
	private static String org_expand_level;
	static{
		org_expand_level=com.hrms.struts.constant.SystemConfig.getPropertyValue("org_expand_level");
	}
 %>
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
<html:form action="/workbench/browse/showinfo"> 
    <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >
       <tr>
           <td align="left"> 
            <div id="treemenu"> 
             <SCRIPT LANGUAGE="javascript">    
              Global.defaultInput=0;
              Global.showroot=false;
              Global.defaultchecklevel=3;
              Global.defaultradiolevel=3;
              Global.showorg=1;
               
             </SCRIPT>
             <hrms:orgtree action="/workbench/browse/showinfodatanuclear.do?b_search=link" target="nil_body" flag="0"  priv="1" showroot="false" privtype="kq" lv="0"/>
             </div>             
           </td>
           </tr>           
    </table>
</html:form>
<script LANGUAGE="javascript">
	root.openURL();
	  <%
               	if("2".equals(org_expand_level)){
               	%>
					root.expand2level();
				 <%}
               %>
</script>
