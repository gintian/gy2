<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<jsp:useBean id="test" class="com.hjsj.hrms.transaction.board.SearchBoardListTrans"/>


<html>
<head>
<title></title>
   <link href="/css/css1.css" rel="stylesheet" type="text/css">
</head>
<body>
    
   
    <jsp:forward page="/selfservice/infomanager/board/searchboard.do?b_query=link"/>
    
  
    
    
</body>
</html>
