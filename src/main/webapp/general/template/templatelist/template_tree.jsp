<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.general.template.TemplateListForm,java.util.*"%>
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
	
	TemplateListForm form=(TemplateListForm)session.getAttribute("templateListForm");  
	String url = "/general/template/templatelist.do?b_query=query&returnflag="+form.getReturnflag()+"&tabid="+form.getTabid()+"&pagecurent=1&cancelfilter=1";
	 
%>

<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<hrms:themes></hrms:themes>
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<html:form action="/general/template/templatelist"> 

  <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >   
	 <tr align="left">
		<td valign="top"></td>
	 </tr>          
     <tr>        
           <td align="left"> 
                 <hrms:orgtree action="<%=url %>" target="mil_body_child" flag="0"  loadtype="1" priv="1"   showroot="false" dbpre="" rootaction="1" rootPriv="0"/>			           
           </td>
      </tr>            
   </table>
   
 
</html:form>
 <script LANGUAGE=javascript >
 <% if(request.getParameter("refresh")==null||request.getParameter("refresh").equals("null")){ %>
	
	root.openURL();
	
<%  } %>

   </script>