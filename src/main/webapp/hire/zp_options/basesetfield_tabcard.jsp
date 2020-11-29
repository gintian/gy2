<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html:form action="/hire/zp_options/basesetfield">
   <hrms:tabset name="pageset" width="100%" height="90%" type="true" align="center"> 
	  <hrms:tab name="tab1" label="menu.base" visible="true" url="/hire/zp_options/basesetfield.do?b_query=link&a_tab=dbpriv">
      </hrms:tab>	
	  <hrms:tab name="tab2" label="menu.table" visible="true" url="/hire/zp_options/basesetfield.do?b_query=link&a_tab=tablepriv">
      </hrms:tab>	
	  <hrms:tab name="tab3" label="menu.field" visible="true" url="/hire/zp_options/basesetfield.do?b_query=link&a_tab=fieldpriv">
      </hrms:tab>	
</hrms:tabset>
</html:form>
