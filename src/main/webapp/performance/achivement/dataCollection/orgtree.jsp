<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
%>

<script LANGUAGE=javascript src="/js/xtree.js"></script> 

<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<hrms:themes />
<html:form action="/performance/achivement/dataCollection/dataCollect"> 

  <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >   
	 <tr align="left">
		<td valign="top"></td>
	 </tr>          
     <tr>        
           <td align="left"> 
           	<logic:equal name="dataCollectForm" property="object_type" value="2">
                 <hrms:orgtree action="/performance/achivement/dataCollection/dataCollect.do?b_query2=link" target="mil_body" flag="1"  loadtype="0" priv="1" showroot="false" dbpre="" rootaction="1"/>			           
            </logic:equal>  
            <logic:equal name="dataCollectForm" property="object_type" value="3">
                 <hrms:orgtree action="/performance/achivement/dataCollection/dataCollect.do?b_query2=link" target="mil_body" flag="0"  loadtype="2" priv="1" showroot="false" dbpre="" rootaction="1"/>			           
            </logic:equal> 
            <logic:notEqual name="dataCollectForm" property="object_type" value="3">
              <logic:notEqual name="dataCollectForm" property="object_type" value="2">
                 <hrms:orgtree action="/performance/achivement/dataCollection/dataCollect.do?b_query2=link" target="mil_body" flag="0"  loadtype="1" priv="1" showroot="false" dbpre="" rootaction="1"/>			           
              </logic:notEqual>   
            </logic:notEqual>   
           </td>
      </tr>            
   </table>
</html:form>
<script>
	root.openURL();
</script>
