<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<html>
  <head>
   

  </head>
  <script language='javascript' >
 	    function exportExcel()
		{
			
			var hashvo=new ParameterSet();
			
		    hashvo.setValue("unitCode",'<%=(request.getParameter("unitCode"))%>');
		    hashvo.setValue("tabid",'<%=(request.getParameter("tabid"))%>');
		    hashvo.setValue("yearid",'<%=(request.getParameter("yearid"))%>');
			hashvo.setValue("reportTypes","8");
		   	var In_paramters="flag=1"; 	
			var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'03040000013'},hashvo);			
		}
		
		function returnInfo(outparamters)
		{
			var outName=outparamters.getValue("outName");
			//var name=outName.substring(0,outName.length-1)+".xls";
		    window.open("/general/muster/hmuster/openFile.jsp?filename="+outName,"_blank");
		}
  
  </script>
  <link href="/css/css1_report.css" rel="stylesheet" type="text/css">
  <hrms:themes />
  <body>
   <form name="reportAnalyseForm" method="post" action="/report/report_analyse/reportanalyse.do">
   
  <table>
  <tr><td>
  <input type="button" name="b_export" value="<bean:message key="general.inform.muster.output.excel"/>" onclick='exportExcel()' class="mybutton" > 
  </td>
  <td width='95%' align='center' >
 <font size='5' >	 <bean:write name="reportAnalyseForm" property="reportTitle" filter="false" /> </font>
  </td>
  </tr>
  <tr><td colspan='2' >
   <bean:write name="reportAnalyseForm" property="reportHtml" filter="false" />
  </td></tr>
  </table>
   
   </form>
  </body>
</html>
