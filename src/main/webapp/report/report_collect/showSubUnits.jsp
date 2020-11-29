<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page   import=" com.hjsj.hrms.actionform.report.edit_report.EditReportForm,java.util.ArrayList" %>

	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
	<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 
	<!-- <script language="JavaScript" src="/js/meizzDate.js"></script> -->
	<%
		EditReportForm editReportForm=(EditReportForm)session.getAttribute("editReportForm"); 
		ArrayList subunitlist=(ArrayList)editReportForm.getSubunitsInfo();
	 %>
<html>

<head>
<script type="text/javascript">
	function next(){
		parent.returnValue="go";
		closeWin();
	}
	function closeWindow(){
		window.returnValue="back";
		closeWin();
	}
	
	function closeWin(){
		var valWin = parent.Ext.getCmp('subcollect');
		if(valWin)
			valWin.close();
		else
			window.close();
	}
</script>
<title>Insert title here</title>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
</head>
<hrms:themes />
<style>
.DetailTable{
	width:expression(document.body.clientWidth-10);
}
</style>
<html:form action="/report/edit_collect/reportCollect">	
<body>
	<table width="100%" height="95%" border="0" cellpmoding="0" cellspacing="0" class="DetailTable" cellpadding="0">   
		        <tr>  
		         <td width="100%" height="95%" align='center' >		         	
						<TEXTAREA  name='area' rows='14' cols='70' style="height:99%;width:100%;">
						<%
							out.write("\r\n");
							for(int i=0;i<subunitlist.size();i++){
								out.write(subunitlist.get(i)+"\r\n");
							}
						 %>
						</TEXTAREA>
		         </td>
		         </tr>
		         <tr>
		         	<td align="center">
						<INPUT type='button' value=' <bean:message key="edit_report.continue"/> ' style="margin-top: 3px;" class='mybutton' onclick='next()'  >				
						<INPUT type='button' value=' <bean:message key="button.cancel"/> ' class='mybutton' style="margin-top: 3px;" onclick='closeWindow()'  >
		         	</td>
		         </tr>
		 </table>
</body>
</html:form>
</html>