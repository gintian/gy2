<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/gz/premium/param">
<hrms:tabset name="cardset" width="100%" height="99%" type="true"> 
		  <hrms:tab name="menu1" label="gz.premium.importformula" visible="true" function_id="" url="/gz/premium/param/formula.do?b_import=link&fmode=1">
	      </hrms:tab>
		 <hrms:tab name="menu2" label="gz.premium.statformula" visible="true" function_id="" url="/gz/premium/param/formula.do?b_stat=link&fmode=2">
	      </hrms:tab>	
	      <hrms:tab name="menu3" label="gz.premium.countformula" visible="true" function_id="" url="/gz/premium/param/formula.do?b_count=link&fmode=0">
	      </hrms:tab>	
</hrms:tabset>
</html:form>