<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<style type="text/css">
 
 </style>

   <%
   		 String chl_id=(String)request.getParameter("chl_id");
    %>
  
           <hrms:cms_channel chl_no="1" type="${employPortalForm.menuType}" chl_id="${employPortalForm.chl_id}" showtye='2'></hrms:cms_channel>

 