<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_analyse.HistoryDataForm"%>
<%
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
 	HistoryDataForm historyDataForm = (HistoryDataForm)session.getAttribute("historyDataForm");   
 	String viewUnit = historyDataForm.getViewUnit();
%>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<hrms:themes />
<html:form action="/gz/gz_analyse/historydata/browse"> 

  <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >   
	 <tr align="left">
		<td valign="top"></td>
	 </tr>          
     <tr>        
           <td align="left"> 
           
                
          <logic:equal value="3" name="historyDataForm" property="viewUnit">
               <hrms:orgtree action="/gz/gz_analyse/historydata/browse.do?b_query=link" target="mil_body" flag="0"  loadtype="1" nmodule="1" priv="1" showroot="false" dbpre="" rootaction="1" rootPriv="1"/>			           
           </logic:equal>
           <logic:notEqual value="3" name="historyDataForm" property="viewUnit">
               <hrms:orgtree action="/gz/gz_analyse/historydata/browse.do?b_query=link" target="mil_body" flag="0"  loadtype="1" viewunit="<%=viewUnit%>" cascadingctrl="1" priv="1" showroot="false" dbpre="" rootaction="1" rootPriv="1"/>			           
           </logic:notEqual>
          
          
           </td>
      </tr>            
   </table>
</html:form>
<script>
<% if(request.getParameter("b_opt")==null){ %>
	
root.openURL();
	
<%  } %>
</script>
