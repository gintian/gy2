<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html>
<head>
	<title>HRPWEB3</title>
	<link href="../css/css1.css" rel="stylesheet" type="text/css">	
</head>
<hrms:themes/>
	<body style="background: white">
	   <table width="100%">
	     <tr>
	       <td>  
				<hrms:gaugeboard title="指标值" gvalue="20" yvalue="25" rvalue="30" cvalue="38"  width="-1" height="300"/>
		   </td>
		 </tr>
	   </table>	
	</body>
</html>