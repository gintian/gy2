<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag="";
    String themes="default";
    if(userView!=null){
    	bosflag = userView.getBosflag();
    	themes = SysParamBo.getSysParamValue("THEMES",userView.getUserName());  
    }
%>
<html>
<head>
<style  type="text/css" >
img{position:relative}
area {position:relative; display:block;}
</style>

<title></title>
<link href="/css/css1.css" rel="stylesheet" type="text/css">   
</head>
<body class="body_sec">
<%if("hcm".equals(bosflag)){ %>
<table width="80%" align="center" >
<tr>
    <td align="center"> <img src="hcm/<%=themes %>/dtgh.jpg"  border="0" usemap="#Map"> </td>
</tr>
</table>
<map name="Map">
 
  <area shape="rect" coords="193,121,302,152" href="/dtgh/party/searchpartybusinesstree.do?returnvalue=dxt&b_query=link&param=Y&backdate=" alt="党组织机构">
  
  <area shape="rect" coords="321,119,430,153" href="/dtgh/party/person/searchbusinesstree.do?returnvalue=dxt&b_query=link&param=Y&backdate=&politics=&tabIndex=0" alt="党组织内人员">
 
  <area shape="rect" coords="193,372,301,405" href="/dtgh/party/searchpartybusinesstree.do?returnvalue=dxt&b_query=link&param=V&backdate=" alt="团组织机构">
 
   <area shape="rect" coords="322,373,431,407" href="/dtgh/party/person/searchbusinesstree.do?returnvalue=dxt&b_query=link&param=V&backdate=&politics=&tabIndex=0" alt="团组织内人员">
 
   <area shape="rect" coords="535,244,642,278" href="/dtgh/party/person/party_parameter.do?returnvalue=dxt&b_query=link" alt="参数设置">
  
</map>
<%} %>
</body>
</html>
