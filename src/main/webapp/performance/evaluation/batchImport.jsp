<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.utils.ResourceFactory"%>
<hrms:themes />
				 				 
<html>
<head>

<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript">
function save(o)
{
	var thevo=new Object();
	thevo.o=o;
    parent.window.returnValue=thevo;
    parent.window.close();
}
</script>
</head>

<style>
	body {TEXT-ALIGN: center;}
	div#tbl-container {	
	width:320;
	height:100;
	overflow:auto;
}
</style>
  <%
  String title=ResourceFactory.getProperty("button.batchinout");
  String prompt=ResourceFactory.getProperty("lable.performance.prompt1"); 
  %>
	<body>
		<html:form action="/performance/evaluation/performanceEvaluation">
			<table align="center">
				<tr><td align="left">
				 <div id='tbl-container'   >
				 	<%=title %>:<br />
				 	<%=prompt %>
				 </div>
				 </td></tr>
			</table>
		<table  width="50%" align="center">
          <tr>
            <td align="center">
            	<input type="button" class="mybutton" name="button" value="<bean:message key="button.download.template"/>" onclick="save('1');"	/>&nbsp;&nbsp;
            	<input type="button" class="mybutton" name="button" value="<bean:message key="button.batchinout"/>" onclick="save('2');"	/>&nbsp;&nbsp;
            	<input type="button" class="mybutton" name="button" value="<bean:message key="button.close"/>" onclick="window.close();"	/>
            </td>
          </tr>          
		</table>
	   </html:form>
  </body>
</html>
