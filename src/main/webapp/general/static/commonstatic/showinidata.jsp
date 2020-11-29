<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<html>
<head>
<title></title>
   <link href="/css/css1.css" rel="stylesheet" type="text/css">
</head>
<hrms:themes/>
<body>

  <logic:equal name="statForm" property="isonetwo" value="1">
      <jsp:forward page="/general/static/commonstatic/statshow.do?b_ini=link&infokind=1&home=0"/>
  </logic:equal>
   <logic:equal name="statForm" property="isonetwo" value="2">
      <jsp:forward page="/general/static/commonstatic/statshow.do?b_initwo=link&infokind=1&home=0"/>
  </logic:equal>
  
    
    
</body>
</html>
