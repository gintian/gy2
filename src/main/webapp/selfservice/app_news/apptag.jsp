<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<br>
<html:form action="/selfservice/app_news/appmessage">
	<hrms:tabset name="sys_param" width="70%" height="500" type="true"> 
	      <hrms:tab name="param1" label="selfservice.appnews.collection" visible="true" url="/selfservice/app_news/appmessage.do?b_query=link&type=select&isdraft=1" >
	      </hrms:tab>
	      <hrms:tab name="param2" label="selfservice.appnews.postletter" visible="true" url="/selfservice/app_news/appmessage2.do?b_query2=link&type=receive&isdraft=1&news_id=" >
	      </hrms:tab>	
	</hrms:tabset>
</html:form>
