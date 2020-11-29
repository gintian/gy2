<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<style>
.important{
	background:#F7FAFF;
	font:bolid 14pt/18pt Helvetica;
	width:300;
	height:80;
	display:none;
}



.RecordRow_h {
	
	BORDER-BOTTOM: #0066cc 1pt solid; 
	BORDER-LEFT: #0066cc 1pt solid; 
	BORDER-RIGHT: #0066cc 1pt solid; 
	BORDER-TOP: #0066cc 1pt solid;
	font-size: 13px;
	font-weight:normal;
	background-color:#C1E0FF;
	height:22;
}

.RecordRow {
	border: inset 1px #C4D8EE;
    BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: #C4D8EE 1pt solid;
    font-size: 12px;
    border-collapse:collapse; 
    height:22px;
}

table {
border-widht: 1pt;
background-image:#ffffff;
}
.MyListTable 
{
    border:0px solid #C4D8EE;
    border-collapse:collapse; 
    BORDER-BOTTOM: medium none; 
    BORDER-LEFT: medium none; 
    BORDER-RIGHT: medium none; 
    BORDER-TOP: medium none; 
    margin-top:-1px;
    margin-left:-1px;
    margin-right:-1px;
}

</style>

<html:form action="/hire/interviewEvaluating/interviewExamine">

	<Br>
	<table >
	<tr><td>
	 <img src="../../images/mbsz.gif" width="30" height="30" border=0 >
	 </td><td> ${interviewExamineForm.titleName}
	 </td></table>
	
	<table >
	<tr><td>&nbsp;</td><td>
	<table id="tbl" class='MyListTable' >
	
		${interviewExamineForm.gradeHtml}
	
	</table>
	</td>
	</tr>
	</table>

<br>
&nbsp;
		
		 <html:button  styleClass="mybutton" property="b_save" onclick="javascript:history.go(-1)">
		            		  <bean:message key="kq.emp.button.return"/>
		 </html:button >
		

</html:form>




