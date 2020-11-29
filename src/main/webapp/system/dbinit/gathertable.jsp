<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<body>
  	<html:form action="/system/dbinit/inforlist">
  		<hrms:tabset name="pageset" width="70%" height="580" type="true">
  			<hrms:tab name="tab1" label="button.colcard" visible="true" url="/system/dbinit/inforlist.do?br_usegather=link">
  				</hrms:tab> 
  			<%-- 		
  			<hrms:tab name="tab2" label="kjg.title.unitname" visible="true" url="/system/dbinit/unitsgather.do?b_query=link">
  				</hrms:tab>
  			--%>	
  			<hrms:tab name="tab3" label="kjg.title.content" visible="true" url="/system/dbinit/indexgather.do?b_query=link">
  				</hrms:tab> 
  				</hrms:tabset>
  	</html:form>
</body>
