<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

%>

<link rel="stylesheet" href="/ext/ext6/resources/ext-theme.css" type="text/css" />
<hrms:fieldEditor formName="headHunterGroupForm" tableName="z60" itemsProperty="editColumns" doScript="false" saveType="${headHunterGroupForm.subType}"  saveAction="/recruitment/headhuntermanage/searchheadhuntergroup.do?b_search=link" >
</hrms:fieldEditor>
