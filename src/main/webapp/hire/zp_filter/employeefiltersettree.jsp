<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hjsj.hrms.actionform.browse.BrowseForm"%>
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
	}
%>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<hrms:themes></hrms:themes>
<html:form action="/hire/zp_filter/browsemaininfo"> 
   <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" background="/images/back1.jpg" >    
          <tr>
           <td align="right"  >
             <hrms:ole name="zpFilterForm" dbpre="zpFilterForm.userbase" a0100="a0100" scope="session" height="120" width="80"/>
           </td>
          </tr>     
      <logic:iterate  id="setlist"   name="zpFilterForm"  property="zpsetlist">    
            <logic:equal name="setlist" property="fieldsetid" value="A01">
               <tr>
                 <td align="right"  nowrap>
                    <a href="/hire/zp_filter/browsemaininfo.do?b_searchfilter=link&userbase=<bean:write name="zpFilterForm" property="userbase" filter="true"/>&a0100=${zpFilterForm.a0100}&setname=${setlist.fieldsetid}" target="mil_body"><font styleClass="settext"> <bean:write  name="setlist" property="customdesc"/></font></a>
                 </td>
               </tr>
             </logic:equal>
            <logic:notEqual name="setlist" property="fieldsetid" value="A01">
              <tr>
               <td align="right"  nowrap>
                   <a href="/hire/zp_filter/browsedetailinfo.do?b_searchfilter=link&userbase=<bean:write name="zpFilterForm" property="userbase" filter="true"/>&a0100=${zpFilterForm.a0100}&setname=${setlist.fieldsetid}" target="mil_body"><font styleClass="settext"> <bean:write  name="setlist" property="customdesc"/></font></a>
               </td>
             </tr>
           </logic:notEqual>
         </logic:iterate>
   </table>
</html:form>
