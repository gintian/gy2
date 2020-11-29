<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hjsj.hrms.businessobject.ykcard.DataEncapsulation,com.hrms.struts.constant.SystemConfig"%>
<%
	String browser = DataEncapsulation.analyseBrowser(request.getHeader("user-agent"));
%>
<style>
body{background:#EEE8AA;text-align:center;}
</style>
<hrms:themes />
<html:form action="/general/card/searchcard">
	<hrms:ykcard name="cardTagParamForm" property="cardparam" istype="3" nid="${cardTagParamForm.a0100}" tabid="${cardTagParamForm.tabid}" cardtype="no" disting_pt="javascript:screen.width" userpriv="noinfo" havepriv="1" fieldpurv="1" queryflag="0" infokind="1" plan_id="" browser="<%=browser %>"/>
</html:form>
