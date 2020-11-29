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
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<hrms:themes />
<html:form action="/general/card/searchcard"> 
   <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >          
         <tr>
           <td align="left"> 
                        <html:hidden name="cardTagParamForm" property="tabid" value="21"/>  
            <div id="treemenu" style="width: 100%;"> 
             <SCRIPT LANGUAGE=javascript>                 
               <bean:write name="cardTagParamForm" property="treeCode" filter="false"/>
             </SCRIPT>
             </div>             
           </td>
           </tr>           
    </table>  
</html:form>