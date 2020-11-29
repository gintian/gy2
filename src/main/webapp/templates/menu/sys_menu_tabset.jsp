<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.sys.SysForm"%>
<%@ page import="org.jdom.Element"%>
<%@ page import="java.util.List"%>
<html>
<body>
<div style="margin-left:-2px;">
<%
SysForm sysForm = (SysForm)session.getAttribute("sysForm");
List menuList = sysForm.getMenuList();
if(menuList.size()>0){
 %>
<hrms:tabset name="sys_param" width="100%" height="100%" type="true"> 
         <%
         for(int i=0;i<menuList.size();i++){
                    Element element = (Element)menuList.get(i);
                    String name = element.getAttributeValue("name");
                    String url = element.getAttributeValue("url");
                    String func_id = element.getAttributeValue("func_id");
          %> 
      <hrms:tab function_id="<%=func_id %>" name="param" label="<%=name %>" visible="true" url="<%=url %>">
      </hrms:tab>    
          <%} %>                          
</hrms:tabset>
<% }%>
</div>
</body>
</html>