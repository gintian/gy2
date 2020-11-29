<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/gz/bonus/param">
<hrms:tabset name="cardset" width="100%" height="96%" type="true"> 
		  <hrms:tab name="menu1" label="gz.bonus.baseparam" visible="true" function_id="" url="/gz/bonus/param/baseparam.do?b_query=link&menuid=1">
	      </hrms:tab>
		 <hrms:tab name="menu2" label="gz.bonus.item" visible="true" function_id="" url="/gz/bonus/param/otherparam.do?b_query=link&menuid=2">
	      </hrms:tab>	
	      <hrms:tab name="menu3" label="gz.bonus.spun" visible="true" function_id="" url="/gz/bonus/param/otherparam.do?b_query=link&menuid=3">
	      </hrms:tab>	
</hrms:tabset>
</html:form>